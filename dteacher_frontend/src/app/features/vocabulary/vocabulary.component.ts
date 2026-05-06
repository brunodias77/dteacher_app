import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { IconComponent } from '../../shared/components/icon/icon.component';
import { VocabularyListResponse, VocabularyService, VocabularyWordResponse } from '../../core/services/vocabulary.service';

export interface VocabWord {
  id: string;
  word: string;
  translation: string;
  example: string;
  exampleTranslation: string;
}

@Component({
  selector: 'app-vocabulary',
  templateUrl: './vocabulary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
})
export class VocabularyComponent {
  private vocabularyService = inject(VocabularyService);

  readonly words      = signal<VocabWord[]>([]);
  readonly loading    = signal(true);
  readonly filter     = signal('');
  readonly manualWord = signal('');
  readonly adding     = signal(false);
  readonly apiErrors  = signal<string[]>([]);
  readonly speakingId = signal<string | null>(null);

  constructor() {
    this.vocabularyService.list().subscribe({
      next: (res: VocabularyListResponse) => {
        this.words.set(res.words.map(toVocabWord));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

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

    this.adding.set(true);
    this.apiErrors.set([]);

    this.vocabularyService.add(w).subscribe({
      next: (res: VocabularyWordResponse) => {
        this.words.update(list => [toVocabWord(res), ...list]);
        this.manualWord.set('');
        this.adding.set(false);
      },
      error: (err: HttpErrorResponse) => {
        const body = err.error as { errors?: string[] } | undefined;
        this.apiErrors.set(body?.errors ?? ['Erro ao adicionar palavra. Tente novamente.']);
        this.adding.set(false);
      },
    });
  }

  onManualWordInput(event: Event): void {
    this.manualWord.set((event.target as HTMLInputElement).value);
    this.apiErrors.set([]);
  }

  onFilterChange(event: Event): void {
    this.filter.set((event.target as HTMLInputElement).value);
  }

  onAddKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter') this.addWord();
  }

  speak(word: VocabWord): void {
    const synth = window.speechSynthesis;
    if (!synth) return;

    if (this.speakingId() === word.id) {
      synth.cancel();
      this.speakingId.set(null);
      return;
    }

    synth.cancel();
    const utter = new SpeechSynthesisUtterance(word.word);
    utter.lang = 'en-US';
    utter.rate = 0.9;

    utter.onstart = () => this.speakingId.set(word.id);
    utter.onend   = () => this.speakingId.set(null);
    utter.onerror = () => this.speakingId.set(null);

    synth.speak(utter);
  }
}

function toVocabWord(r: VocabularyWordResponse): VocabWord {
  return {
    id: r.id,
    word: r.word,
    translation: r.translation,
    example: r.example,
    exampleTranslation: r.exampleTranslation,
  };
}
