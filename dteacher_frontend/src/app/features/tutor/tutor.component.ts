import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { IconComponent } from '../../shared/components/icon/icon.component';

export type TutorMode = 'chat' | 'teacher';
export type CefrLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';

export interface ChatMessage {
  sender: 'USER' | 'AI';
  text: string;
  translation?: string;
  feedback?: string;
}

export interface TeacherMessage {
  sender: 'USER' | 'AI';
  text: string;
}

export interface CorrectionResult {
  correctedText: string;
  explanation: string;
}

const MOCK_CHAT: ChatMessage[] = [
  {
    sender: 'AI',
    text: 'Hello! Ready to practice some English today?',
    translation: 'Olá! Pronto para praticar um pouco de inglês hoje?',
  },
  {
    sender: 'USER',
    text: 'Yes, I am! I would like to talk about my weekend plans.',
    feedback:
      "Muito bom! Frase natural e correcta. Repara que também podias dizer \"I'd like to\" — é mais natural em conversa informal.",
  },
  {
    sender: 'AI',
    text: "That sounds great! What are you planning to do this weekend?",
    translation: 'Que ótimo! O que estás a planear fazer este fim de semana?',
  },
  {
    sender: 'USER',
    text: "I'm thinking about going to the beach with my friends.",
    feedback: '"I\'m thinking about" + gerúndio é perfeito aqui. Excelente nível B1!',
  },
  {
    sender: 'AI',
    text: "Oh, that sounds like fun! Is it going to be sunny?",
    translation: 'Oh, parece divertido! Vai estar sol?',
  },
];

const MOCK_TEACHER: TeacherMessage[] = [
  { sender: 'USER', text: "Quando devo usar 'present perfect' em vez de 'simple past'?" },
  {
    sender: 'AI',
    text: "Ótima pergunta! Use o \"Present Perfect\" quando a ação tem ligação com o presente — o resultado ainda é relevante ou o tempo exato não importa. Exemplo: \"I have lost my keys\" (ainda não as encontrei). Use o \"Simple Past\" para ações concluídas num tempo específico: \"I lost my keys yesterday\".",
  },
  { sender: 'USER', text: "E a diferença entre 'since' e 'for'?" },
  {
    sender: 'AI',
    text: "Simples: usa \"since\" com um ponto no tempo (desde quando) e \"for\" com uma duração (durante quanto tempo). Exemplos: \"I have lived here since 2018\" vs \"I have lived here for six years\". Ambos combinam perfeitamente com o Present Perfect!",
  },
];

const MOCK_CORRECTION: CorrectionResult = {
  correctedText: "I'm thinking about going to the beach with my friends.",
  explanation: "A tua frase está correcta e natural! O gerúndio depois de 'thinking about' é perfeito. Excelente trabalho!",
};

const MOCK_SUGGESTIONS = ["That sounds amazing!", "What about you?", "I'm not sure yet."];

const QUICK_TOPICS = [
  "Diferença entre 'make' e 'do'",
  "Quando usar 'present perfect'?",
  "Phrasal verbs mais comuns",
  "Pronúncia do 'th'",
  "Artigos: a, an, the",
];

const CEFR_LEVELS: CefrLevel[] = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

@Component({
  selector: 'app-tutor',
  templateUrl: './tutor.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
})
export class TutorComponent {
  readonly mode           = signal<TutorMode>('chat');
  readonly chatLevel      = signal<CefrLevel>('B1');
  readonly chatMessages   = signal<ChatMessage[]>(MOCK_CHAT);
  readonly teacherMessages = signal<TeacherMessage[]>(MOCK_TEACHER);
  readonly input          = signal('');
  readonly loading        = signal(false);
  readonly suggestions    = signal<string[]>(MOCK_SUGGESTIONS);
  readonly correction     = signal<CorrectionResult | null>(MOCK_CORRECTION);

  readonly CEFR_LEVELS = CEFR_LEVELS;
  readonly QUICK_TOPICS = QUICK_TOPICS;

  readonly isChat = computed(() => this.mode() === 'chat');
  readonly messages = computed(() =>
    this.isChat() ? this.chatMessages() : this.teacherMessages(),
  );

  readonly wordCount = computed(() =>
    this.chatMessages()
      .filter(m => m.sender === 'USER')
      .reduce((acc, m) => acc + m.text.split(/\s+/).length, 0),
  );

  setMode(m: TutorMode): void {
    this.mode.set(m);
  }

  setInput(value: string): void {
    this.input.set(value);
  }

  applyCorrection(): void {
    const c = this.correction();
    if (c) this.input.set(c.correctedText);
    this.correction.set(null);
  }

  dismissCorrection(): void {
    this.correction.set(null);
  }

  dismissSuggestions(): void {
    this.suggestions.set([]);
  }

  send(): void {
    if (!this.input().trim()) return;
    this.input.set('');
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  onLevelChange(event: Event): void {
    this.chatLevel.set((event.target as HTMLSelectElement).value as CefrLevel);
  }
}
