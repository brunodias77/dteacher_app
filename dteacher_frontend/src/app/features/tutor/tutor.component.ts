import {
  AfterViewChecked,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  ViewChild,
  computed,
  inject,
  signal,
} from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { IconComponent } from '../../shared/components/icon/icon.component';
import { ChatService, CefrLevel } from '../../core/services/chat.service';

export type TutorMode = 'chat' | 'teacher';

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

const MOCK_TEACHER: TeacherMessage[] = [
  { sender: 'USER', text: "Quando devo usar 'present perfect' em vez de 'simple past'?" },
  {
    sender: 'AI',
    text: 'Ótima pergunta! Use o "Present Perfect" quando a ação tem ligação com o presente — o resultado ainda é relevante ou o tempo exato não importa. Exemplo: "I have lost my keys" (ainda não as encontrei). Use o "Simple Past" para ações concluídas num tempo específico: "I lost my keys yesterday".',
  },
  { sender: 'USER', text: "E a diferença entre 'since' e 'for'?" },
  {
    sender: 'AI',
    text: 'Simples: usa "since" com um ponto no tempo (desde quando) e "for" com uma duração (durante quanto tempo). Exemplos: "I have lived here since 2018" vs "I have lived here for six years". Ambos combinam perfeitamente com o Present Perfect!',
  },
];

const QUICK_TOPICS = [
  "Diferença entre 'make' e 'do'",
  "Quando usar 'present perfect'?",
  'Phrasal verbs mais comuns',
  "Pronúncia do 'th'",
  'Artigos: a, an, the',
];

const CEFR_LEVELS: CefrLevel[] = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

@Component({
  selector: 'app-tutor',
  templateUrl: './tutor.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
})
export class TutorComponent implements AfterViewChecked {
  @ViewChild('messageList') private messageListRef!: ElementRef<HTMLElement>;

  private chatService = inject(ChatService);
  private shouldScroll = false;

  readonly mode            = signal<TutorMode>('chat');
  readonly chatLevel       = signal<CefrLevel>('B1');
  readonly sessionId       = signal<string | null>(null);
  readonly chatMessages    = signal<ChatMessage[]>([]);
  readonly teacherMessages = signal<TeacherMessage[]>(MOCK_TEACHER);
  readonly input           = signal('');
  readonly loading         = signal(false);
  readonly errorMsg        = signal('');
  readonly suggestions     = signal<string[]>([]);
  readonly correction      = signal<CorrectionResult | null>(null);

  readonly CEFR_LEVELS = CEFR_LEVELS;
  readonly QUICK_TOPICS = QUICK_TOPICS;

  readonly isChat = computed(() => this.mode() === 'chat');

  readonly wordCount = computed(() =>
    this.chatMessages()
      .filter(m => m.sender === 'USER')
      .reduce((acc, m) => acc + m.text.split(/\s+/).length, 0),
  );

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  private scrollToBottom(): void {
    const el = this.messageListRef?.nativeElement;
    if (el) el.scrollTop = el.scrollHeight;
  }

  setMode(m: TutorMode): void {
    this.mode.set(m);
  }

  setInput(value: string): void {
    this.input.set(value);
  }

  newSession(): void {
    if (this.loading()) return;
    this.loading.set(true);
    this.errorMsg.set('');
    this.chatMessages.set([]);
    this.sessionId.set(null);
    this.correction.set(null);
    this.suggestions.set([]);

    this.chatService.startSession(this.chatLevel()).subscribe({
      next: res => {
        this.sessionId.set(res.sessionId);
        this.chatMessages.set(
          res.messages.map(m => ({
            sender: m.sender,
            text: m.content,
            translation: m.translation,
          })),
        );
        this.loading.set(false);
        this.shouldScroll = true;
      },
      error: (err: HttpErrorResponse) => {
        const body = err.error as { errors?: string[] } | undefined;
        this.errorMsg.set(body?.errors?.[0] ?? 'Erro ao iniciar sessão. Tente novamente.');
        this.loading.set(false);
      },
    });
  }

  send(): void {
    const content = this.input().trim();
    const sessionId = this.sessionId();
    if (!content || !sessionId || this.loading()) return;

    const userMsgIdx = this.chatMessages().length;
    this.chatMessages.update(msgs => [...msgs, { sender: 'USER', text: content }]);
    this.input.set('');
    this.loading.set(true);
    this.shouldScroll = true;

    this.chatService.sendMessage(sessionId, content).subscribe({
      next: res => {
        this.chatMessages.update(msgs => {
          const updated = [...msgs];
          if (res.aiMessage.feedback) {
            updated[userMsgIdx] = { ...updated[userMsgIdx], feedback: res.aiMessage.feedback };
          }
          updated.push({
            sender: 'AI',
            text: res.aiMessage.content,
            translation: res.aiMessage.translation,
          });
          return updated;
        });
        this.loading.set(false);
        this.shouldScroll = true;
      },
      error: () => {
        this.chatMessages.update(msgs => [
          ...msgs,
          { sender: 'AI', text: 'Ocorreu um erro. Por favor, tente novamente.' },
        ]);
        this.loading.set(false);
        this.shouldScroll = true;
      },
    });
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

  onInputChange(event: Event): void {
    this.input.set((event.target as HTMLInputElement).value);
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
