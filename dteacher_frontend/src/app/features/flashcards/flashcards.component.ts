import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../shared/components/icon/icon.component';

export interface Flashcard {
  id: string;
  english: string;
  portuguese: string;
  nextReview: Date;
  repetitions: number;
}

const MOCK_CARDS: Flashcard[] = [
  {
    id: '1',
    english: 'She arrived at the airport three hours before her flight.',
    portuguese: 'Ela chegou ao aeroporto três horas antes do voo.',
    nextReview: new Date(Date.now() - 1000),
    repetitions: 0,
  },
  {
    id: '2',
    english: 'He bought a round-trip ticket to London.',
    portuguese: 'Ele comprou uma passagem de ida e volta para Londres.',
    nextReview: new Date(Date.now() - 1000),
    repetitions: 0,
  },
  {
    id: '3',
    english: 'The meeting has been postponed until next week.',
    portuguese: 'A reunião foi adiada para a próxima semana.',
    nextReview: new Date(Date.now() + 86_400_000),
    repetitions: 3,
  },
];

interface GradeOption {
  key: 'again' | 'hard' | 'good' | 'easy';
  label: string;
  sub: string;
  color: string;
  icon: string;
  delay: number;
}

const GRADES: GradeOption[] = [
  { key: 'again', label: 'Errei',   sub: '<1 min',  color: 'var(--color-danger)', icon: 'rotate', delay: 60_000 },
  { key: 'hard',  label: 'Difícil', sub: '10 min',  color: 'var(--color-warn)',   icon: 'x',      delay: 600_000 },
  { key: 'good',  label: 'Bom',     sub: '1 dia',   color: 'var(--color-info)',   icon: 'check',  delay: 86_400_000 },
  { key: 'easy',  label: 'Fácil',   sub: '4 dias',  color: 'var(--color-accent)', icon: 'zap',    delay: 345_600_000 },
];

const SEGMENTS = Array.from({ length: 20 }, (_, i) => i);

@Component({
  selector: 'app-flashcards',
  templateUrl: './flashcards.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, IconComponent, DecimalPipe],
})
export class FlashcardsComponent {
  readonly cards        = signal<Flashcard[]>(MOCK_CARDS);
  readonly isFlipped    = signal(false);
  readonly reviewMode   = signal<'classic' | 'typing'>('classic');
  readonly userInput    = signal('');
  readonly hoveredGrade = signal<string | null>(null);

  readonly GRADES   = GRADES;
  readonly SEGMENTS = SEGMENTS;

  readonly dueCards = computed(() =>
    this.cards().filter(c => c.nextReview <= new Date()),
  );

  readonly currentCard = computed(() => this.dueCards()[0] ?? null);

  readonly progress = computed(() => {
    const total = this.cards().length;
    if (total === 0) return 0;
    return Math.max(5, ((total - this.dueCards().length) / total) * 100);
  });

  readonly isCorrect = computed(() => {
    const card = this.currentCard();
    if (!card || !this.userInput().trim()) return false;
    const ni = this.normalize(this.userInput());
    if (ni === this.normalize(card.portuguese)) return true;
    return card.portuguese.split(/,|\/|\bou\b/i).some(p => this.normalize(p) === ni);
  });

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
    if (!card) return;
    const nextReview = new Date(Date.now() + g.delay);
    this.cards.update(list =>
      list.map(c =>
        c.id === card.id ? { ...c, nextReview, repetitions: c.repetitions + 1 } : c,
      ),
    );
    this.isFlipped.set(false);
    this.userInput.set('');
  }

  onUserInput(event: Event): void {
    this.userInput.set((event.target as HTMLInputElement).value);
  }

  onInputKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.userInput().trim()) this.isFlipped.set(true);
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
