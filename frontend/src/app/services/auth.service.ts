import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { TOKEN_KEY } from '../core/interceptors/auth.interceptor';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/auth';

  login(body: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, body)
      .pipe(catchError((e) => this.mapError(e)));
  }

  register(body: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/register`, body)
      .pipe(catchError((e) => this.mapError(e)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  saveSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
  }

  private mapError(err: HttpErrorResponse): Observable<never> {
    const message =
      err.error && typeof err.error === 'object' && 'error' in err.error
        ? String((err.error as { error: string }).error)
        : err.message || 'Request failed';
    return throwError(() => new Error(message));
  }
}
