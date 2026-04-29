import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { IconComponent } from '../../shared/components/icon/icon.component';

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

const MOCK_RESULT: TextResult = {
  overview:
    'Excerto de um artigo jornalístico curto sobre a importância do sono para a produtividade e o bem-estar.',
  analysis: [
    {
      original:
        'Scientists have long known that sleep plays a crucial role in memory consolidation.',
      translation:
        'Os cientistas sabem há muito tempo que o sono desempenha um papel crucial na consolidação da memória.',
      notes:
        '"Long known" usa o Present Perfect para indicar algo verdadeiro desde há muito tempo. "Plays a crucial role" é uma expressão muito comum para dizer que algo é muito importante.',
    },
    {
      original:
        'Getting less than seven hours a night can impair your focus and decision-making.',
      translation:
        'Dormir menos de sete horas por noite pode prejudicar a sua concentração e tomada de decisão.',
      notes:
        '"Impair" significa prejudicar ou comprometer. "Decision-making" é um substantivo composto muito usado em contextos profissionais.',
    },
    {
      original:
        'Even a short nap of twenty minutes can restore alertness and boost performance.',
      translation:
        'Mesmo uma soneca curta de vinte minutos pode restaurar o estado de alerta e melhorar o desempenho.',
      notes:
        '"Nap" é uma palavra informal para uma soneca curta. "Boost" significa aumentar ou melhorar de forma rápida — muito comum em inglês informal e profissional.',
    },
  ],
  ankiCards: [
    {
      front: 'Memory consolidation happens during sleep.',
      back: 'A consolidação da memória acontece durante o sono.',
    },
    {
      front: 'Even a short nap can boost performance.',
      back: 'Mesmo uma soneca curta pode melhorar o desempenho.',
    },
    {
      front: 'Lack of sleep impairs decision-making.',
      back: 'A falta de sono prejudica a tomada de decisão.',
    },
    {
      front: 'Getting less than seven hours can affect focus.',
      back: 'Dormir menos de sete horas pode afetar a concentração.',
    },
  ],
};

@Component({
  selector: 'app-text-study',
  templateUrl: './text-study.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
})
export class TextStudyComponent {
  readonly textInput = signal('');
  readonly studying  = signal(false);
  readonly errorMsg  = signal('');
  readonly result    = signal<TextResult | null>(MOCK_RESULT);

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

    setTimeout(() => {
      this.result.set(MOCK_RESULT);
      this.studying.set(false);
    }, 1200);
  }

  clear(): void {
    this.result.set(null);
    this.textInput.set('');
    this.errorMsg.set('');
  }

  onKeydown(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') this.analyse();
  }
}
