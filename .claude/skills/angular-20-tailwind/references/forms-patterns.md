# Forms Patterns — Angular 20

## Reactive Forms (login example)

```typescript
import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="submit()" class="space-y-4">
      <div>
        <input
          formControlName="email"
          type="email"
          placeholder="Email"
          class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        @if (emailInvalid()) {
          <p class="mt-1 text-xs text-red-500">Enter a valid email address.</p>
        }
      </div>
      <div>
        <input
          formControlName="password"
          type="password"
          placeholder="Password"
          class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        @if (passwordInvalid()) {
          <p class="mt-1 text-xs text-red-500">Password must be at least 8 characters.</p>
        }
      </div>
      <button
        type="submit"
        [disabled]="form.invalid"
        class="w-full rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white hover:bg-indigo-700 disabled:opacity-50"
      >
        Sign in
      </button>
    </form>
  `,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);

  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  emailInvalid    = signal(false);
  passwordInvalid = signal(false);

  submit() {
    this.form.markAllAsTouched();
    const email    = this.form.get('email')!;
    const password = this.form.get('password')!;
    this.emailInvalid.set(email.invalid);
    this.passwordInvalid.set(password.invalid);
    if (this.form.invalid) return;
    // handle submit
  }
}
```

---

## FormArray — Dynamic Fields

```typescript
@Component({
  imports: [ReactiveFormsModule],
  template: `
    <form [formGroup]="form">
      <div formArrayName="emails">
        @for (ctrl of emails.controls; track $index) {
          <div class="flex gap-2 mb-2">
            <input [formControlName]="$index" type="email" class="flex-1 rounded border px-3 py-2 text-sm" />
            <button type="button" (click)="removeEmail($index)" class="text-red-500 text-sm">Remove</button>
          </div>
        }
      </div>
      <button type="button" (click)="addEmail()" class="text-sm text-indigo-600">+ Add email</button>
    </form>
  `,
})
export class EmailListComponent {
  private fb = inject(FormBuilder);

  form = this.fb.group({
    emails: this.fb.array([this.fb.control('', Validators.email)]),
  });

  get emails() {
    return this.form.get('emails') as FormArray;
  }

  addEmail() {
    this.emails.push(this.fb.control('', Validators.email));
  }

  removeEmail(index: number) {
    this.emails.removeAt(index);
  }
}
```

---

## Custom Validator

```typescript
import { AbstractControl, ValidationErrors } from '@angular/forms';

export function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password        = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  if (password && confirmPassword && password !== confirmPassword) {
    return { passwordMismatch: true };
  }
  return null;
}

// Usage in group:
this.fb.group(
  {
    password:        ['', Validators.required],
    confirmPassword: ['', Validators.required],
  },
  { validators: passwordMatchValidator },
)
```

---

## Async Validator (unique email)

```typescript
import { AsyncValidatorFn, AbstractControl } from '@angular/forms';
import { map, catchError, of } from 'rxjs';

export function uniqueEmailValidator(http: HttpClient): AsyncValidatorFn {
  return (control: AbstractControl) =>
    http.get<{ exists: boolean }>(`/api/users/check-email?email=${control.value}`).pipe(
      map(res => res.exists ? { emailTaken: true } : null),
      catchError(() => of(null)),
    );
}
```
