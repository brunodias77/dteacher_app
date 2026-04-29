import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { PreferenceService } from '../../../core/services/preference.service';
import { LoginFormComponent, LoginPayload } from './login-form.component';

interface ApiError {
  errors: string[];
}

@Component({
  selector: 'app-login',
  imports: [LoginFormComponent],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
  private auth   = inject(AuthService);
  private prefs  = inject(PreferenceService);
  private router = inject(Router);
  private route  = inject(ActivatedRoute);

  loading      = signal(false);
  apiErrors    = signal<string[]>([]);
  registered   = signal(this.route.snapshot.queryParams['registered'] === '1');

  onSubmit(payload: LoginPayload) {
    this.loading.set(true);
    this.apiErrors.set([]);

    this.auth.login(payload.email, payload.password).subscribe({
      next: () => {
        this.loading.set(false);
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] as string | undefined;
        this.prefs.load().subscribe({
          complete: () => this.router.navigateByUrl(returnUrl ?? '/dashboard'),
          error:    () => this.router.navigateByUrl(returnUrl ?? '/dashboard'),
        });
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        const body = err.error as ApiError | undefined;
        this.apiErrors.set(body?.errors ?? ['Ocorreu um erro. Tente novamente.']);
      },
    });
  }
}
