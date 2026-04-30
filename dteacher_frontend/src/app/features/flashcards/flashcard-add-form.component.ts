import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Input } from '../../shared/components/input/input.component';

export interface AddFlashcardPayload {
  english: string;
  portuguese: string;
}

@Component({
  selector: 'app-flashcard-add-form',
  templateUrl: './flashcard-add-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, Input],
})
export class FlashcardAddFormComponent {
  private fb = inject(FormBuilder);

  loading   = input(false);
  apiErrors = input<string[]>([]);

  submitted = output<AddFlashcardPayload>();
  cancelled = output<void>();

  form = this.fb.nonNullable.group({
    english:    ['', [Validators.required, Validators.maxLength(500)]],
    portuguese: ['', [Validators.required, Validators.maxLength(500)]],
  });

  englishError(): string {
    const c = this.form.controls.english;
    if (!c.touched) return '';
    if (c.errors?.['required'])   return 'Campo obrigatório.';
    if (c.errors?.['maxlength'])  return 'Máximo de 500 caracteres.';
    return '';
  }

  portugueseError(): string {
    const c = this.form.controls.portuguese;
    if (!c.touched) return '';
    if (c.errors?.['required'])   return 'Campo obrigatório.';
    if (c.errors?.['maxlength'])  return 'Máximo de 500 caracteres.';
    return '';
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.submitted.emit(this.form.getRawValue());
  }

  cancel(): void {
    this.cancelled.emit();
  }
}
