import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { IconComponent } from '../../shared/components/icon/icon.component';
import { TextStudyService } from '../../core/services/text-study.service';

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
  imports: [IconComponent],
})
export class TextStudyComponent {
  private textStudyService = inject(TextStudyService);

  readonly textInput = signal('');
  readonly studying  = signal(false);
  readonly errorMsg  = signal('');
  readonly result    = signal<TextResult | null>(null);

  readonly charCount = computed(() => this.textInput().length);
  readonly wordCount = computed(
    () => this.textInput().split(/\s+/).filter(Boolean).length,
  );

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
  }

  onTextInput(event: Event): void {
    this.textInput.set((event.target as HTMLTextAreaElement).value);
  }

  onKeydown(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') this.analyse();
  }
}
