import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  output,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  passwordConfirmation: string;
}

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const password     = control.get('password')?.value as string;
  const confirmation = control.get('passwordConfirmation')?.value as string;
  if (password && confirmation && password !== confirmation) {
    return { passwordMismatch: true };
  }
  return null;
}

function hasNumber(control: AbstractControl): ValidationErrors | null {
  const value = control.value as string;
  if (value && !/\d/.test(value)) return { noNumber: true };
  return null;
}

@Component({
  selector: 'app-register-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterFormComponent {
  private fb = inject(FormBuilder);

  loading   = input(false);
  apiErrors = input<string[]>([]);
  submitted = output<RegisterPayload>();

  form = this.fb.nonNullable.group(
    {
      name:                 ['', [Validators.maxLength(100)]],
      email:                ['', [Validators.required, Validators.email]],
      password:             ['', [Validators.required, Validators.minLength(8), hasNumber]],
      passwordConfirmation: ['', [Validators.required]],
    },
    { validators: passwordsMatch },
  );

  submit() {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.submitted.emit(this.form.getRawValue());
  }
}
