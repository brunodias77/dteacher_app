import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IconComponent } from '../../shared/components/icon/icon.component';
import { TextStudyService } from '../../core/services/text-study.service';
import { FlashcardService } from '../../core/services/flashcard.service';
import { ClickableSentenceComponent } from '../../shared/components/clickable-sentence/clickable-sentence.component';
import { WordFlashcardDialogComponent } from '../../shared/components/word-flashcard-dialog/word-flashcard-dialog.component';

export interface AnalysisItem {
  original: string;
  translation: string;
  notes: string;
}

export interface AnkiCard {
  front: string;
  back: string;
}

export interface TextResult {
  overview: string;
  analysis: AnalysisItem[];
  ankiCards: AnkiCard[];
}

@Component({
  selector: 'app-text-study',
  templateUrl: './text-study.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent, ClickableSentenceComponent, WordFlashcardDialogComponent],
})
export class TextStudyComponent {
  private textStudyService  = inject(TextStudyService);
  private flashcardService  = inject(FlashcardService);

  readonly textInput = signal('');
  readonly studying  = signal(false);
  readonly errorMsg  = signal('');
  readonly result    = signal<TextResult | null>(null);

  readonly speakingText  = signal('');
  readonly selectedWord  = signal<string | null>(null);
  readonly wordAdding    = signal(false);

  readonly ankiAdded     = signal<Set<number>>(new Set());
  readonly ankiAddingIdx = signal<number | null>(null);
  readonly ankiAddingAll = signal(false);

  readonly charCount = computed(() => this.textInput().length);
  readonly wordCount = computed(
    () => this.textInput().split(/\s+/).filter(Boolean).length,
  );

  speak(text: string): void {
    if (!('speechSynthesis' in window)) return;
    if (this.speakingText() === text) {
      window.speechSynthesis.cancel();
      this.speakingText.set('');
      return;
    }
    window.speechSynthesis.cancel();
    const utt = new SpeechSynthesisUtterance(text);
    utt.lang = 'en-US';
    utt.rate = 0.9;
    utt.onstart = () => this.speakingText.set(text);
    utt.onend = () => this.speakingText.set('');
    utt.onerror = () => this.speakingText.set('');
    window.speechSynthesis.speak(utt);
  }

  padIndex(n: number): string {
    return String(n + 1).padStart(2, '0');
  }

  analyse(): void {
    if (!this.textInput().trim() || this.studying()) return;
    this.studying.set(true);
    this.errorMsg.set('');
    this.result.set(null);

    this.textStudyService.analyze(this.textInput()).subscribe({
      next: res => {
        this.result.set({
          overview: res.overview,
          analysis: res.analysis.map(a => ({
            original:    a.sentence,
            translation: a.translation,
            notes:       a.notes,
          })),
          ankiCards: res.ankiCards.map(c => ({
            front: c.english,
            back:  c.portuguese,
          })),
        });
        this.studying.set(false);
      },
      error: (err: HttpErrorResponse) => {
        const body = err.error as { errors?: string[] } | undefined;
        this.errorMsg.set(body?.errors?.[0] ?? 'Erro ao analisar o texto. Tente novamente.');
        this.studying.set(false);
      },
    });
  }

  clear(): void {
    this.result.set(null);
    this.textInput.set('');
    this.errorMsg.set('');
    this.ankiAdded.set(new Set());
    this.ankiAddingIdx.set(null);
    this.ankiAddingAll.set(false);
  }

  onTextInput(event: Event): void {
    this.textInput.set((event.target as HTMLTextAreaElement).value);
  }

  onKeydown(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') this.analyse();
  }

  onWordClick(word: string): void {
    this.selectedWord.set(word);
  }

  onConfirmWordFlashcard(portuguese: string): void {
    const word = this.selectedWord();
    if (!word) return;
    this.wordAdding.set(true);
    this.flashcardService.add({ english: word, portuguese, source: 'text-study-word' }).subscribe({
      next: () => {
        this.wordAdding.set(false);
        this.selectedWord.set(null);
      },
      error: () => {
        this.wordAdding.set(false);
      },
    });
  }

  onCancelWordFlashcard(): void {
    this.selectedWord.set(null);
  }

  addAnkiCard(card: AnkiCard, idx: number): void {
    if (this.ankiAdded().has(idx) || this.ankiAddingIdx() !== null || this.ankiAddingAll()) return;
    this.ankiAddingIdx.set(idx);
    this.flashcardService.add({ english: card.front, portuguese: card.back, source: 'text_study' }).subscribe({
      next: () => {
        this.ankiAdded.update(s => new Set([...s, idx]));
        this.ankiAddingIdx.set(null);
      },
      error: () => {
        this.ankiAddingIdx.set(null);
      },
    });
  }

  addAllAnkiCards(cards: AnkiCard[]): void {
    if (this.ankiAddingAll()) return;
    const remaining = cards
      .map((card, idx) => ({ card, idx }))
      .filter(({ idx }) => !this.ankiAdded().has(idx));
    if (remaining.length === 0) return;
    this.ankiAddingAll.set(true);
    forkJoin(
      remaining.map(({ card, idx }) =>
        this.flashcardService.add({ english: card.front, portuguese: card.back, source: 'text_study' }).pipe(
          map(() => idx),
          catchError(() => of(null)),
        )
      )
    ).subscribe(results => {
      results.forEach(idx => {
        if (idx !== null) this.ankiAdded.update(s => new Set([...s, idx]));
      });
      this.ankiAddingAll.set(false);
    });
  }
}
