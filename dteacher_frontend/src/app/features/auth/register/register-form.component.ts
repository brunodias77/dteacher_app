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
import { Input } from '../../../shared/components/input/input.component';
import { Button } from '../../../shared/components/button/button.component';

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
  imports: [ReactiveFormsModule, RouterLink, Input, Button],
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

  nameError(): string {
    const c = this.form.controls.name;
    if (!c.touched || !c.invalid) return '';
    return 'Nome deve ter no máximo 100 caracteres.';
  }

  emailError(): string {
    const c = this.form.controls.email;
    if (!c.touched) return '';
    if (c.errors?.['required']) return 'E-mail é obrigatório.';
    if (c.errors?.['email'])    return 'E-mail deve ser um endereço válido.';
    return '';
  }

  passwordError(): string {
    const c = this.form.controls.password;
    if (!c.touched) return '';
    if (c.errors?.['required'])  return 'Senha é obrigatória.';
    if (c.errors?.['minlength']) return 'Senha deve ter no mínimo 8 caracteres.';
    if (c.errors?.['noNumber'])  return 'Senha deve conter pelo menos 1 número.';
    return '';
  }

  passwordConfirmError(): string {
    const c = this.form.controls.passwordConfirmation;
    if (!c.touched) return '';
    if (c.errors?.['required'])                return 'Confirmação de senha é obrigatória.';
    if (this.form.errors?.['passwordMismatch']) return 'Confirmação de senha não confere.';
    return '';
  }

  submit() {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.submitted.emit(this.form.getRawValue());
  }
}
