import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { IconComponent } from '../../shared/components/icon/icon.component';

export interface StoryParagraph {
  en: string;
  pt: string;
}

export interface QuizQuestion {
  question: string;
  options: string[];
  answer: string;
}

export interface StoryData {
  title: string;
  paragraphs: StoryParagraph[];
  quiz: QuizQuestion[];
}

const MOCK_STORY: StoryData = {
  title: "The Detective's Last Case",
  paragraphs: [
    {
      en: "Detective Sarah Blake sat in her small office, staring at the rain outside. It was late November, and the city felt cold and quiet. She had not worked on a case for three months.",
      pt: "A detetive Sarah Blake estava sentada em seu pequeno escritório, olhando para a chuva lá fora. Era final de novembro, e a cidade parecia fria e silenciosa. Ela não trabalhava em um caso há três meses.",
    },
    {
      en: "Then the phone rang. A man named Thomas called to say his sister had disappeared. He sounded nervous. Sarah wrote down his address and put on her coat.",
      pt: "Então o telefone tocou. Um homem chamado Thomas ligou para dizer que sua irmã havia desaparecido. Ele parecia nervoso. Sarah anotou o endereço dele e colocou seu casaco.",
    },
    {
      en: "She arrived at a tall apartment building near the river. Thomas opened the door quickly. 'She left yesterday morning,' he said. 'She never came back.'",
      pt: "Ela chegou a um prédio alto perto do rio. Thomas abriu a porta rapidamente. 'Ela saiu ontem de manhã,' ele disse. 'Ela nunca voltou.'",
    },
  ],
  quiz: [
    {
      question: "How long had Sarah not worked on a case?",
      options: ["One month", "Three months", "Six months"],
      answer: "Three months",
    },
    {
      question: "Who called Sarah Blake?",
      options: ["A man named Thomas", "Her boss", "A police officer"],
      answer: "A man named Thomas",
    },
    {
      question: "Where did Sarah go?",
      options: ["A hotel near the park", "A tall building near the river", "Thomas's office"],
      answer: "A tall building near the river",
    },
  ],
};

const EXAMPLE_TOPICS = ['Ficção Científica', 'Viagem', 'Rotina Diária'];
const CEFR_LEVELS    = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

@Component({
  selector: 'app-immersion',
  templateUrl: './immersion.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
})
export class ImmersionComponent {
  readonly topic      = signal('');
  readonly level      = signal('B1');
  readonly loading    = signal(false);
  readonly storyData  = signal<StoryData | null>(null);
  readonly errorMsg   = signal('');
  readonly showResults = signal(false);

  // Paragraph translation visibility: index → revealed
  readonly revealedPars = signal<Record<number, boolean>>({});
  // Playing audio per-paragraph: paragraph index or -1
  readonly playingPar   = signal<number>(-1);
  // Quiz selected answers: question index → selected option
  readonly quizAnswers  = signal<Record<number, string>>({});

  readonly EXAMPLE_TOPICS = EXAMPLE_TOPICS;
  readonly CEFR_LEVELS    = CEFR_LEVELS;

  readonly hasStory = computed(() => this.storyData() !== null);

  readonly answeredCount = computed(() => Object.keys(this.quizAnswers()).length);

  readonly allAnswered = computed(() => {
    const story = this.storyData();
    return story !== null && this.answeredCount() >= story.quiz.length;
  });

  readonly quizScore = computed(() => {
    const story = this.storyData();
    if (!story) return 0;
    return story.quiz.filter((q, i) => this.quizAnswers()[i] === q.answer).length;
  });

  setTopic(value: string): void {
    this.topic.set(value);
  }

  onTopicInput(event: Event): void {
    this.topic.set((event.target as HTMLInputElement).value);
  }

  onLevelChange(event: Event): void {
    this.level.set((event.target as HTMLSelectElement).value);
  }

  onTopicKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter') this.generate();
  }

  generate(): void {
    if (this.loading()) return;
    this.loading.set(true);
    this.errorMsg.set('');
    setTimeout(() => {
      this.storyData.set(MOCK_STORY);
      this.revealedPars.set({});
      this.quizAnswers.set({});
      this.showResults.set(false);
      this.playingPar.set(-1);
      this.loading.set(false);
    }, 1400);
  }

  newStory(): void {
    this.storyData.set(null);
    this.revealedPars.set({});
    this.quizAnswers.set({});
    this.showResults.set(false);
    this.playingPar.set(-1);
  }

  toggleReveal(idx: number): void {
    this.revealedPars.update(r => ({ ...r, [idx]: !r[idx] }));
  }

  isRevealed(idx: number): boolean {
    return !!this.revealedPars()[idx];
  }

  toggleParAudio(idx: number): void {
    if (this.playingPar() === idx) {
      this.playingPar.set(-1);
      return;
    }
    this.playingPar.set(idx);
    setTimeout(() => {
      if (this.playingPar() === idx) this.playingPar.set(-1);
    }, 3000);
  }

  isParPlaying(idx: number): boolean {
    return this.playingPar() === idx;
  }

  selectAnswer(qIdx: number, opt: string): void {
    if (this.showResults()) return;
    this.quizAnswers.update(a => ({ ...a, [qIdx]: opt }));
  }

  getSelectedAnswer(qIdx: number): string | undefined {
    return this.quizAnswers()[qIdx];
  }

  correctAnswers(): void {
    if (!this.allAnswered()) return;
    this.showResults.set(true);
  }

  optionState(qIdx: number, opt: string): 'correct' | 'wrong' | 'selected' | 'default' {
    const story = this.storyData();
    if (!story) return 'default';
    const selected = this.quizAnswers()[qIdx];
    if (this.showResults()) {
      if (opt === story.quiz[qIdx].answer) return 'correct';
      if (opt === selected) return 'wrong';
      return 'default';
    }
    if (opt === selected) return 'selected';
    return 'default';
  }
}
