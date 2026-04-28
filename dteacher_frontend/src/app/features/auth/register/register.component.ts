import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterFormComponent, RegisterPayload } from './register-form.component';

interface ApiError {
  errors: string[];
}

@Component({
  selector: 'app-register',
  imports: [RegisterFormComponent],
  templateUrl: './register.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
  private auth   = inject(AuthService);
  private router = inject(Router);

  loading   = signal(false);
  apiErrors = signal<string[]>([]);

  onSubmit(payload: RegisterPayload) {
    this.loading.set(true);
    this.apiErrors.set([]);

    this.auth.register(payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/login'], { queryParams: { registered: '1' } });
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        const body = err.error as ApiError | undefined;
        this.apiErrors.set(body?.errors ?? ['Ocorreu um erro. Tente novamente.']);
      },
    });
  }
}
