import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../shared/components/icon/icon.component';
import { FlashcardResponse, FlashcardService, ReviewGrade } from '../../core/services/flashcard.service';
import { AddFlashcardPayload, FlashcardAddFormComponent } from './flashcard-add-form.component';

interface GradeOption {
  key: ReviewGrade;
  label: string;
  sub: string;
  color: string;
  icon: string;
}

const GRADES: GradeOption[] = [
  { key: 'again', label: 'Errei',   sub: '1 dia',    color: 'var(--color-danger)', icon: 'rotate' },
  { key: 'hard',  label: 'Difícil', sub: '1+ dias',  color: 'var(--color-warn)',   icon: 'x'      },
  { key: 'good',  label: 'Bom',     sub: '1-6 dias', color: 'var(--color-info)',   icon: 'check'  },
  { key: 'easy',  label: 'Fácil',   sub: '4+ dias',  color: 'var(--color-accent)', icon: 'zap'    },
];

const SEGMENTS = Array.from({ length: 20 }, (_, i) => i);

@Component({
  selector: 'app-flashcards',
  templateUrl: './flashcards.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, IconComponent, DecimalPipe, FlashcardAddFormComponent],
})
export class FlashcardsComponent {
  private flashcardService = inject(FlashcardService);

  readonly cards          = signal<FlashcardResponse[]>([]);
  readonly loading        = signal(true);
  readonly sessionStarted = signal(false);
  readonly isFlipped      = signal(false);
  readonly reviewMode     = signal<'classic' | 'typing'>('classic');
  readonly userInput      = signal('');
  readonly hoveredGrade   = signal<string | null>(null);
  readonly gradingKey     = signal<ReviewGrade | null>(null);
  readonly showAddForm    = signal(false);
  readonly addLoading     = signal(false);
  readonly addErrors      = signal<string[]>([]);

  readonly GRADES   = GRADES;
  readonly SEGMENTS = SEGMENTS;

  readonly total = computed(() => this.cards().length);

  readonly dueCards = computed(() =>
    this.cards().filter(c => new Date(c.nextReview) <= new Date()),
  );

  readonly currentCard = computed(() => this.dueCards()[0] ?? null);

  readonly progress = computed(() => {
    const t = this.total();
    if (t === 0) return 0;
    return Math.max(5, ((t - this.dueCards().length) / t) * 100);
  });

  readonly isCorrect = computed(() => {
    const card = this.currentCard();
    if (!card || !this.userInput().trim()) return false;
    const ni = this.normalize(this.userInput());
    if (ni === this.normalize(card.portuguese)) return true;
    return card.portuguese.split(/,|\/|\bou\b/i).some(p => this.normalize(p) === ni);
  });

  constructor() {
    this.flashcardService.list().subscribe({
      next: res => {
        this.cards.set(res.cards);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  isSegOn(i: number): boolean {
    return i < Math.round(this.progress() / 5);
  }

  setMode(mode: 'classic' | 'typing'): void {
    this.reviewMode.set(mode);
    this.isFlipped.set(false);
    this.userInput.set('');
  }

  showAnswer(): void {
    this.isFlipped.set(true);
  }

  grade(g: GradeOption): void {
    const card = this.currentCard();
    if (!card || this.gradingKey() !== null) return;

    this.gradingKey.set(g.key);
    this.flashcardService.review(card.id, g.key).subscribe({
      next: updated => {
        this.cards.update(list => list.map(c => c.id === updated.id ? updated : c));
        if (this.dueCards().length === 0) {
          this.sessionStarted.set(false);
        }
        this.isFlipped.set(false);
        this.userInput.set('');
        this.gradingKey.set(null);
      },
      error: () => this.gradingKey.set(null),
    });
  }

  onUserInput(event: Event): void {
    this.userInput.set((event.target as HTMLInputElement).value);
  }

  onInputKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.userInput().trim()) this.isFlipped.set(true);
  }

  startSession(): void {
    this.sessionStarted.set(true);
    this.isFlipped.set(false);
    this.userInput.set('');
  }

  openAddForm(): void {
    this.addErrors.set([]);
    this.showAddForm.set(true);
  }

  onAddSubmit(payload: AddFlashcardPayload): void {
    this.addLoading.set(true);
    this.addErrors.set([]);
    this.flashcardService.add({ ...payload, source: 'manual' }).subscribe({
      next: card => {
        this.cards.update(list => [...list, card]);
        this.addLoading.set(false);
        this.showAddForm.set(false);
      },
      error: (err: HttpErrorResponse) => {
        const body = err.error as { errors?: string[] } | undefined;
        this.addErrors.set(body?.errors ?? ['Erro ao salvar o cartão. Tente novamente.']);
        this.addLoading.set(false);
      },
    });
  }

  onAddCancel(): void {
    this.showAddForm.set(false);
  }

  private normalize(s: string): string {
    return s
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '')
      .replace(/[.,!?;:]/g, '')
      .trim();
  }
}
