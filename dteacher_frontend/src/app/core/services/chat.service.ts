import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export type CefrLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';

export interface ChatMessageDto {
  id: string;
  sender: 'USER' | 'AI';
  content: string;
  translation?: string;
  createdAt: string;
}

export interface StartSessionResponse {
  sessionId: string;
  messages: ChatMessageDto[];
}

export interface AiMessageDto {
  id: string;
  sender: 'AI';
  content: string;
  translation?: string;
  feedback?: string;
  createdAt: string;
}

export interface SendMessageResponse {
  aiMessage: AiMessageDto;
}

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);

  startSession(cefrLevel: CefrLevel) {
    return this.http.post<StartSessionResponse>('/api/chat/sessions', {
      mode: 'chat',
      cefrLevel,
    });
  }

  sendMessage(sessionId: string, content: string) {
    return this.http.post<SendMessageResponse>(
      `/api/chat/sessions/${sessionId}/messages`,
      { content },
    );
  }
}
