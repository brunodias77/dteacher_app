import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface VocabularyWordResponse {
  id: string;
  word: string;
  translation: string;
  example: string;
  exampleTranslation: string;
  createdAt: string;
}

export interface VocabularyListResponse {
  words: VocabularyWordResponse[];
  total: number;
}

@Injectable({ providedIn: 'root' })
export class VocabularyService {
  private http = inject(HttpClient);

  list() {
    return this.http.get<VocabularyListResponse>('/api/vocabulary');
  }

  add(word: string) {
    return this.http.post<VocabularyWordResponse>('/api/vocabulary', { word });
  }
}
