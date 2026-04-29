import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { finalize, tap } from 'rxjs';

export interface AuthUser {
  id: string;
  email: string;
  name: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  passwordConfirmation: string;
}

export interface RegisterResponse {
  id: string;
  email: string;
  name: string;
}

export interface LoginResponse {
  accessToken: string;
  expiresIn: number;
  user: AuthUser;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http   = inject(HttpClient);
  private router = inject(Router);

  private _user = signal<AuthUser | null>(this.loadUser());
  readonly user            = this._user.asReadonly();
  readonly isAuthenticated = computed(() => !!this._user());

  register(req: RegisterRequest) {
    return this.http.post<RegisterResponse>('/api/auth/register', req);
  }

  login(email: string, password: string) {
    return this.http.post<LoginResponse>('/api/auth/login', { email, password }).pipe(
      tap(res => {
        localStorage.setItem('token', res.accessToken);
        localStorage.setItem('user', JSON.stringify(res.user));
        this._user.set(res.user);
      }),
    );
  }

  logout() {
    return this.http.post('/api/auth/logout', {}).pipe(
      finalize(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this._user.set(null);
        this.router.navigate(['/login']);
      }),
    );
  }

  private loadUser(): AuthUser | null {
    try {
      return JSON.parse(localStorage.getItem('user') ?? 'null');
    } catch {
      return null;
    }
  }
}
