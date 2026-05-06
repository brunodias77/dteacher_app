import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface AnalyzeTextResponse {
  overview: string;
  analysis: { sentence: string; translation: string; notes: string }[];
  ankiCards: { english: string; portuguese: string }[];
}

@Injectable({ providedIn: 'root' })
export class TextStudyService {
  private http = inject(HttpClient);

  analyze(text: string) {
    return this.http.post<AnalyzeTextResponse>('/api/ai/analyze-text', { text });
  }
}
