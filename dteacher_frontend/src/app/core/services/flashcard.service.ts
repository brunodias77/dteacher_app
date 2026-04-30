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

export interface FlashcardListResponse {
  cards: FlashcardResponse[];
  total: number;
  totalDue: number;
}

export interface FlashcardPayload {
  english: string;
  portuguese: string;
  source?: string;
}

export type ReviewGrade = 'again' | 'hard' | 'good' | 'easy';

@Injectable({ providedIn: 'root' })
export class FlashcardService {
  private http = inject(HttpClient);

  list() {
    return this.http.get<FlashcardListResponse>('/api/flashcards');
  }

  add(payload: FlashcardPayload) {
    return this.http.post<FlashcardResponse>('/api/flashcards', payload);
  }

  review(id: string, grade: ReviewGrade) {
    return this.http.patch<FlashcardResponse>(`/api/flashcards/${id}/review`, { grade });
  }
}
