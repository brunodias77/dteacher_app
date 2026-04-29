import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface FlashcardResponse {
  id: string;
  english: string;
  portuguese: string;
  source: string;
  nextReview: string;
  intervalDays: number;
  easeFactor: number;
  repetitions: number;
  createdAt: string;
}

export interface FlashcardPayload {
  english: string;
  portuguese: string;
  source?: string;
}

@Injectable({ providedIn: 'root' })
export class FlashcardService {
  private http = inject(HttpClient);

  add(payload: FlashcardPayload) {
    return this.http.post<FlashcardResponse>('/api/flashcards', payload);
  }
}
