import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { IconComponent } from '../../shared/components/icon/icon.component';

export interface VocabWord {
  word: string;
  translation: string;
  example: string;
  exampleTranslation: string;
}

const MOCK_WORDS: VocabWord[] = [
  {
    word: 'commute',
    translation: 'deslocamento / ir para o trabalho',
    example: 'My daily commute takes about 45 minutes.',
    exampleTranslation: 'O meu deslocamento diário demora cerca de 45 minutos.',
  },
  {
    word: 'postpone',
    translation: 'adiar',
    example: 'We had to postpone the meeting until Friday.',
    exampleTranslation: 'Tivemos de adiar a reunião para sexta-feira.',
  },
  {
    word: 'struggle',
    translation: 'dificuldade / lutar',
    example: 'She struggles to wake up early every morning.',
    exampleTranslation: 'Ela tem dificuldade em acordar cedo todas as manhãs.',
  },
  {
    word: 'afford',
    translation: 'ter condições de',
    example: "I can't afford a new laptop right now.",
    exampleTranslation: 'Não tenho condições de comprar um portátil novo agora.',
  },
  {
    word: 'awkward',
    translation: 'constrangedor / desajeitado',
    example: 'There was an awkward silence after the joke.',
    exampleTranslation: 'Houve um silêncio constrangedor depois da piada.',
  },
  {
    word: 'deadline',
    translation: 'prazo',
    example: 'We need to meet the deadline by Friday.',
    exampleTranslation: 'Precisamos de cumprir o prazo até sexta-feira.',
  },
];

@Component({
  selector: 'app-vocabulary',
  templateUrl: './vocabulary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
})
export class VocabularyComponent {
  readonly words      = signal<VocabWord[]>(MOCK_WORDS);
  readonly filter     = signal('');
  readonly manualWord = signal('');
  readonly adding     = signal(false);

  readonly filtered = computed(() => {
    const q = this.filter().toLowerCase();
    if (!q) return this.words();
    return this.words().filter(
      w =>
        w.word.toLowerCase().includes(q) ||
        w.translation.toLowerCase().includes(q),
    );
  });

  readonly thisWeek = computed(() => Math.min(this.words().length, 8));

  padIndex(n: number): string {
    return String(n + 1).padStart(3, '0');
  }

  addWord(): void {
    const w = this.manualWord().trim();
    if (!w || this.adding()) return;
    this.words.update(list => [
      { word: w, translation: '—', example: '—', exampleTranslation: '' },
      ...list,
    ]);
    this.manualWord.set('');
  }

  onManualWordInput(event: Event): void {
    this.manualWord.set((event.target as HTMLInputElement).value);
  }

  onFilterChange(event: Event): void {
    this.filter.set((event.target as HTMLInputElement).value);
  }

  onAddKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter') this.addWord();
  }
}
