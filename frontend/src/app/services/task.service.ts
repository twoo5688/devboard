import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { Task, TaskRequest } from '../models/task.models';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);

  private base(projectId: number): string {
    return `/api/projects/${projectId}/tasks`;
  }

  getTasks(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(this.base(projectId)).pipe(catchError((e) => this.mapError(e)));
  }

  createTask(projectId: number, body: TaskRequest): Observable<Task> {
    return this.http.post<Task>(this.base(projectId), body).pipe(catchError((e) => this.mapError(e)));
  }

  updateTask(projectId: number, taskId: number, body: TaskRequest): Observable<Task> {
    return this.http
      .put<Task>(`${this.base(projectId)}/${taskId}`, body)
      .pipe(catchError((e) => this.mapError(e)));
  }

  deleteTask(projectId: number, taskId: number): Observable<void> {
    return this.http
      .delete<void>(`${this.base(projectId)}/${taskId}`)
      .pipe(catchError((e) => this.mapError(e)));
  }

  private mapError(err: HttpErrorResponse): Observable<never> {
    const message =
      err.error && typeof err.error === 'object' && 'error' in err.error
        ? String((err.error as { error: string }).error)
        : err.message || 'Request failed';
    return throwError(() => new Error(message));
  }
}
