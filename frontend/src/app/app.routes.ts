import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./auth/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'projects',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./projects/projects-list/projects-list.component').then((m) => m.ProjectsListComponent),
  },
  {
    path: 'projects/:id/board',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./projects/kanban-board/kanban-board.component').then((m) => m.KanbanBoardComponent),
  },
  { path: '**', redirectTo: 'login' },
];
