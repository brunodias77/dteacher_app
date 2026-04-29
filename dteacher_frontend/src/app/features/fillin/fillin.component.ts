import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { IconComponent } from '../../shared/components/icon/icon.component';

export type FillMode = 'typing' | 'multiple';
export type FillState = 'setup' | 'exercise' | 'summary';

export interface FillExercise {
  sentence: string;
  answer: string;
  translation: string;
  hint: string;
  difficulty: string;
  options: string[];
}

export interface FillResult {
  correct: boolean;
  userInput: string;
}

const MOCK_EXERCISES: FillExercise[] = [
  {
    sentence: 'She needs to ___ her passport before the trip.',
    answer: 'renew',
    translation: 'Ela precisa renovar o passaporte antes da viagem.',
    hint: 'Verbo que significa atualizar ou tornar válido novamente.',
    difficulty: 'B1',
    options: ['renew', 'repair', 'refund', 'return'],
  },
  {
    sentence: 'The flight was ___ for two hours due to bad weather.',
    answer: 'delayed',
    translation: 'O voo atrasou duas horas por causa do mau tempo.',
    hint: 'Participado passado de um verbo que significa atrasar.',
    difficulty: 'B1',
    options: ['delayed', 'cancelled', 'rerouted', 'departed'],
  },
  {
    sentence: 'Could you ___ me a window seat, please?',
    answer: 'reserve',
    translation: 'Pode reservar um assento de janela para mim, por favor?',
    hint: 'Verbo sinónimo de "book" — guardar um lugar com antecedência.',
    difficulty: 'A2',
    options: ['reserve', 'request', 'return', 'remind'],
  },
  {
    sentence: 'He forgot to ___ his luggage at the check-in counter.',
    answer: 'check in',
    translation: 'Ele se esqueceu de fazer o check-in da bagagem no balcão.',
    hint: 'Phrasal verb usado no aeroporto para registar a bagagem.',
    difficulty: 'A2',
    options: ['check in', 'check out', 'pick up', 'drop off'],
  },
  {
    sentence: 'You must show your boarding ___ at the gate.',
    answer: 'pass',
    translation: 'Tem de mostrar o cartão de embarque na porta.',
    hint: 'Palavra que completa "boarding ___" — o documento para embarcar.',
    difficulty: 'A1',
    options: ['pass', 'card', 'ticket', 'form'],
  },
  {
    sentence: 'The airline will ___ you a refund if the flight is cancelled.',
    answer: 'issue',
    translation: 'A companhia aérea vai emitir um reembolso se o voo for cancelado.',
    hint: 'Verbo formal que significa emitir ou fornecer oficialmente.',
    difficulty: 'B2',
    options: ['issue', 'offer', 'grant', 'provide'],
  },
  {
    sentence: 'We need to ___ a connecting flight in Frankfurt.',
    answer: 'catch',
    translation: 'Precisamos apanhar uma ligação em Frankfurt.',
    hint: 'Verbo informal que usamos quando "apanhamos" um transporte a tempo.',
    difficulty: 'B1',
    options: ['catch', 'take', 'board', 'book'],
  },
  {
    sentence: 'Please ___ your seatbelt during turbulence.',
    answer: 'fasten',
    translation: 'Por favor, aperte o cinto durante a turbulência.',
    hint: 'Verbo que significa apertar ou prender algo com segurança.',
    difficulty: 'A2',
    options: ['fasten', 'wear', 'attach', 'tighten'],
  },
  {
    sentence: 'The customs officer asked her to ___ her bag.',
    answer: 'open',
    translation: 'O agente alfandegário pediu que ela abrisse a mala.',
    hint: 'O verbo mais simples para revelar o conteúdo de algo.',
    difficulty: 'A1',
    options: ['open', 'carry', 'show', 'check'],
  },
  {
    sentence: 'Passengers should ___ at the gate at least 30 minutes before departure.',
    answer: 'arrive',
    translation: 'Os passageiros devem chegar à porta pelo menos 30 minutos antes da partida.',
    hint: 'Verbo básico de movimento que indica chegar a um local.',
    difficulty: 'A1',
    options: ['arrive', 'appear', 'attend', 'approach'],
  },
];

const TOPIC_SUGGESTIONS = ['business meeting', 'ordering food', 'job interview', 'small talk', 'feelings'];
const CEFR_LEVELS = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

@Component({
  selector: 'app-fillin',
  templateUrl: './fillin.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent, DecimalPipe],
})
export class FillinComponent {
  readonly state      = signal<FillState>('setup');
  readonly mode       = signal<FillMode>('typing');
  readonly level      = signal('B1');
  readonly words      = signal('');
  readonly loading    = signal(false);
  readonly exercises  = signal<FillExercise[]>([]);
  readonly idx        = signal(0);
  readonly userInput  = signal('');
  readonly revealed   = signal(false);
  readonly showHint   = signal(false);
  readonly results    = signal<FillResult[]>([]);

  readonly CEFR_LEVELS       = CEFR_LEVELS;
  readonly TOPIC_SUGGESTIONS = TOPIC_SUGGESTIONS;

  readonly exercise = computed(() => this.exercises()[this.idx()]);
  readonly total    = computed(() => this.exercises().length);
  readonly progress = computed(() => (this.idx() / this.total()) * 100);
  readonly correct  = computed(() => this.results().filter(r => r.correct).length);
  readonly isCorrect = computed(() => {
    const ex = this.exercise();
    return ex ? this.userInput().trim().toLowerCase() === ex.answer.toLowerCase() : false;
  });

  readonly isSummary  = computed(() => this.state() === 'summary');
  readonly isSetup    = computed(() => this.state() === 'setup');
  readonly isExercise = computed(() => this.state() === 'exercise');

  setLevel(l: string): void {
    this.level.set(l);
  }

  setMode(m: FillMode): void {
    this.mode.set(m);
  }

  setWords(value: string): void {
    this.words.set(value);
  }

  setSuggestion(topic: string): void {
    this.words.set(topic.split(' ').join(', '));
  }

  generate(): void {
    if (!this.words().trim() || this.loading()) return;
    this.loading.set(true);
    setTimeout(() => {
      this.exercises.set(MOCK_EXERCISES);
      this.idx.set(0);
      this.userInput.set('');
      this.revealed.set(false);
      this.showHint.set(false);
      this.results.set([]);
      this.loading.set(false);
      this.state.set('exercise');
    }, 1200);
  }

  selectOption(opt: string): void {
    if (!this.revealed()) this.userInput.set(opt);
  }

  reveal(): void {
    if (!this.userInput().trim()) return;
    const correct = this.isCorrect();
    this.results.update(r => [...r, { correct, userInput: this.userInput() }]);
    this.revealed.set(true);
  }

  next(): void {
    const next = this.idx() + 1;
    if (next >= this.total()) {
      this.state.set('summary');
    } else {
      this.idx.set(next);
      this.userInput.set('');
      this.revealed.set(false);
      this.showHint.set(false);
    }
  }

  showSummary(): void {
    this.state.set('summary');
  }

  reset(): void {
    this.state.set('setup');
    this.exercises.set([]);
    this.results.set([]);
    this.idx.set(0);
    this.userInput.set('');
    this.revealed.set(false);
    this.showHint.set(false);
  }

  toggleHint(): void {
    this.showHint.update(v => !v);
  }

  onWordsInput(event: Event): void {
    this.words.set((event.target as HTMLTextAreaElement).value);
  }

  onUserInput(event: Event): void {
    this.userInput.set((event.target as HTMLInputElement).value);
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.userInput().trim() && !this.revealed()) {
      event.preventDefault();
      this.reveal();
    }
  }

  sentenceParts(): string[] {
    return this.exercise()?.sentence.split('___') ?? ['', ''];
  }

  resultClass(i: number): string {
    const r = this.results()[i];
    if (r) return r.correct ? 'on' : '';
    if (i === this.idx() && !r) return 'on';
    return '';
  }
}
