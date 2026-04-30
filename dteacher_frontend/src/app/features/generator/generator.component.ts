import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { GeneratorService, Sentence } from '../../core/services/generator.service';
import { FlashcardService } from '../../core/services/flashcard.service';
import { ChipAddButton } from '../../shared/components/chip-add-button/chip-add-button.component';
import { ClickableSentenceComponent } from '../../shared/components/clickable-sentence/clickable-sentence.component';
import { WordFlashcardDialogComponent } from '../../shared/components/word-flashcard-dialog/word-flashcard-dialog.component';
import { AudioPillComponent } from '../../shared/components/audio-pill/audio-pill.component';

@Component({
  selector: 'app-generator',
  templateUrl: './generator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, ChipAddButton, ClickableSentenceComponent, WordFlashcardDialogComponent, AudioPillComponent],
})
export class GeneratorComponent {
  private generatorService = inject(GeneratorService);
  private flashcardService = inject(FlashcardService);

  words      = signal('');
  sentences  = signal<Sentence[]>([]);
  generating = signal(false);
  errorMsg   = signal('');

  addingIdx = signal<Set<number>>(new Set());
  addedIdx  = signal<Set<number>>(new Set());

  selectedWord  = signal<string | null>(null);
  wordAdding    = signal(false);

  readonly SUGGESTIONS = ['small talk', 'commute', 'ordering coffee', 'remote work', 'weekend plans'];

  generate() {
    if (!this.words().trim()) return;
    this.generating.set(true);
    this.errorMsg.set('');
    this.sentences.set([]);
    this.addingIdx.set(new Set());
    this.addedIdx.set(new Set());

    this.generatorService.generate(this.words()).subscribe({
      next: res => {
        this.sentences.set(res.sentences);
        this.generating.set(false);
      },
      error: (err: HttpErrorResponse) => {
        const body = err.error as { errors?: string[] } | undefined;
        this.errorMsg.set(body?.errors?.[0] ?? 'Ocorreu um erro ao gerar as frases. Tente novamente.');
        this.generating.set(false);
      },
    });
  }

  addOne(item: Sentence, idx: number) {
    if (this.addedIdx().has(idx) || this.addingIdx().has(idx)) return;
    this.addingIdx.update(s => new Set(s).add(idx));

    this.flashcardService.add({ english: item.english, portuguese: item.portuguese, source: 'generator' }).subscribe({
      next: () => {
        this.addingIdx.update(s => { const n = new Set(s); n.delete(idx); return n; });
        this.addedIdx.update(s => new Set(s).add(idx));
      },
      error: () => {
        this.addingIdx.update(s => { const n = new Set(s); n.delete(idx); return n; });
      },
    });
  }

  addAll() {
    this.sentences().forEach((item, idx) => this.addOne(item, idx));
  }

  onKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter') this.generate();
  }

  isAdding(idx: number) { return this.addingIdx().has(idx); }
  isAdded(idx: number)  { return this.addedIdx().has(idx);  }

  padIndex(n: number) { return String(n + 1).padStart(2, '0'); }

  onWordClick(word: string) {
    this.selectedWord.set(word);
  }

  onConfirmWordFlashcard(portuguese: string) {
    const word = this.selectedWord();
    if (!word) return;
    this.wordAdding.set(true);
    this.flashcardService.add({ english: word, portuguese, source: 'generator-word' }).subscribe({
      next: () => {
        this.wordAdding.set(false);
        this.selectedWord.set(null);
      },
      error: () => {
        this.wordAdding.set(false);
      },
    });
  }

  onCancelWordFlashcard() {
    this.selectedWord.set(null);
  }
}
