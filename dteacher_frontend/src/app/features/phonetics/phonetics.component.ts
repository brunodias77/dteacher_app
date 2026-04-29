import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { IconComponent } from '../../shared/components/icon/icon.component';

export interface PhoneticWord {
  word: string;
  phonetic: string;
  tip: string;
}

export interface PhoneticResult {
  original: string;
  phonetic: string;
  translation: string;
  words: PhoneticWord[];
}

const MOCK_RESULT: PhoneticResult = {
  original: 'I would like a cup of coffee, please.',
  phonetic: 'ai uud laik a cap ov cófi, plíz.',
  translation: 'Eu gostaria de uma xícara de café, por favor.',
  words: [
    {
      word: 'would',
      phonetic: 'uud',
      tip: "O 'w' soa como 'u' arredondado — lábios em forma de 'o'.",
    },
    {
      word: 'like',
      phonetic: 'laik',
      tip: "Termina com 'k' seco, não 'ki'. Soa como 'laik'.",
    },
    {
      word: 'cup',
      phonetic: 'cap',
      tip: "Vogal central curta e aberta — parecida com o 'a' do PT.",
    },
    {
      word: 'coffee',
      phonetic: 'cófi',
      tip: "Acento forte na primeira sílaba — 'CÓ-fi'.",
    },
    {
      word: 'please',
      phonetic: 'plíz',
      tip: "O 'z' final é sonoro — a garganta vibra.",
    },
  ],
};

const EXAMPLE_PHRASES = [
  'Good morning, how are you?',
  'I would like a table for two.',
  'Could you speak more slowly?',
  'Nice to meet you!',
  'Where is the nearest station?',
];

@Component({
  selector: 'app-phonetics',
  templateUrl: './phonetics.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
})
export class PhoneticsComponent {
  readonly input    = signal('');
  readonly loading  = signal(false);
  readonly playing  = signal(false);
  readonly result   = signal<PhoneticResult | null>(null);
  readonly errorMsg = signal('');

  readonly EXAMPLE_PHRASES = EXAMPLE_PHRASES;

  readonly hasResult = computed(() => this.result() !== null);

  setInput(value: string): void {
    this.input.set(value);
  }

  onInput(event: Event): void {
    this.input.set((event.target as HTMLTextAreaElement).value);
  }

  transcribe(): void {
    if (!this.input().trim() || this.loading()) return;
    this.loading.set(true);
    this.errorMsg.set('');
    this.result.set(null);
    this.playing.set(false);
    setTimeout(() => {
      this.result.set({ ...MOCK_RESULT, original: this.input().trim() });
      this.loading.set(false);
    }, 1000);
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && event.ctrlKey) {
      event.preventDefault();
      this.transcribe();
    }
  }

  togglePlay(): void {
    if (this.playing()) {
      this.playing.set(false);
      return;
    }
    this.playing.set(true);
    setTimeout(() => this.playing.set(false), 3000);
  }
}
