import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';

export interface Sentence {
  english: string;
  portuguese: string;
}

export interface GenerateSentencesResponse {
  sentences: Sentence[];
}

@Injectable({ providedIn: 'root' })
export class GeneratorService {
  private http = inject(HttpClient);

  generate(words: string) {
    return this.http.post<GenerateSentencesResponse>('/api/generator/sentences', { words });
  }

  translate(word: string) {
    return this.http
      .post<{ portuguese: string }>('/api/generator/translate', { word })
      .pipe(map(r => r.portuguese));
  }
}
