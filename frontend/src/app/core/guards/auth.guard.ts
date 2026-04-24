import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TOKEN_KEY } from '../interceptors/auth.interceptor';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) {
    return router.parseUrl('/login');
  }
  return true;
};
