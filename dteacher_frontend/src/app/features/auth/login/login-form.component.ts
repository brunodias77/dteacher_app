import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  output,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Input } from '../../../shared/components/input/input.component';
import { Button } from '../../../shared/components/button/button.component';

export interface LoginPayload {
  email: string;
  password: string;
}

@Component({
  selector: 'app-login-form',
  imports: [ReactiveFormsModule, RouterLink, Input, Button],
  templateUrl: './login-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginFormComponent {
  private fb = inject(FormBuilder);

  loading   = input(false);
  apiErrors = input<string[]>([]);
  submitted = output<LoginPayload>();

  form = this.fb.nonNullable.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

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
    return '';
  }

  submit() {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.submitted.emit(this.form.getRawValue());
  }
}
