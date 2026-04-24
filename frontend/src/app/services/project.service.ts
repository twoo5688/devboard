import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { Project, ProjectRequest } from '../models/project.models';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/projects';

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.baseUrl).pipe(catchError((e) => this.mapError(e)));
  }

  createProject(body: ProjectRequest): Observable<Project> {
    return this.http.post<Project>(this.baseUrl, body).pipe(catchError((e) => this.mapError(e)));
  }

  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(catchError((e) => this.mapError(e)));
  }

  private mapError(err: HttpErrorResponse): Observable<never> {
    const message =
      err.error && typeof err.error === 'object' && 'error' in err.error
        ? String((err.error as { error: string }).error)
        : err.message || 'Request failed';
    return throwError(() => new Error(message));
  }
}
