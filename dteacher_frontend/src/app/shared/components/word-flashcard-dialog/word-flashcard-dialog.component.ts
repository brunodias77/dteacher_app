import { ChangeDetectionStrategy, Component, effect, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GeneratorService } from '../../../core/services/generator.service';

@Component({
  selector: 'app-word-flashcard-dialog',
  templateUrl: './word-flashcard-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule],
})
export class WordFlashcardDialogComponent {
  private generatorService = inject(GeneratorService);

  word    = input.required<string>();
  loading = input(false);

  confirmed = output<string>();
  cancelled = output<void>();

  translating   = signal(true);
  translateError = signal('');
  portuguese     = signal('');

  constructor() {
    effect(() => {
      const w = this.word();
      if (!w) return;

      this.translating.set(true);
      this.translateError.set('');
      this.portuguese.set('');

      this.generatorService.translate(w).subscribe({
        next: pt => {
          this.portuguese.set(pt);
          this.translating.set(false);
        },
        error: () => {
          this.translateError.set('Não foi possível obter a tradução. Tente novamente.');
          this.translating.set(false);
        },
      });
    });
  }

  onConfirm() {
    if (!this.portuguese().trim()) return;
    this.confirmed.emit(this.portuguese().trim());
  }

  onCancel() {
    this.cancelled.emit();
  }

  setPortuguese(value: string) {
    this.portuguese.set(value);
  }
}
