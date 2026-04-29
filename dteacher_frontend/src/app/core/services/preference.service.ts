import { Injectable, effect, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, debounceTime, switchMap, tap } from 'rxjs';

export interface Preferences {
  accent: 'lime' | 'amber' | 'cyan' | 'magenta' | 'white';
  density: 'compact' | 'cozy' | 'roomy';
  uppercaseLevel: 'labels' | 'headings' | 'off';
  showStreakBar: boolean;
  defaultCefr: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
  lastActiveTab: string;
}

const ACCENT_MAP: Record<string, { color: string; soft: string; border: string }> = {
  lime:    { color: '#ccf381', soft: 'rgba(204,243,129,0.10)', border: 'rgba(204,243,129,0.35)' },
  amber:   { color: '#fbbf24', soft: 'rgba(251,191,36,0.10)',  border: 'rgba(251,191,36,0.35)'  },
  cyan:    { color: '#67e8f9', soft: 'rgba(103,232,249,0.10)', border: 'rgba(103,232,249,0.35)' },
  magenta: { color: '#f0abfc', soft: 'rgba(240,171,252,0.12)', border: 'rgba(240,171,252,0.40)' },
  white:   { color: '#f5f5f4', soft: 'rgba(245,245,244,0.08)', border: 'rgba(245,245,244,0.30)' },
};

const DEFAULTS: Preferences = {
  accent: 'lime',
  density: 'cozy',
  uppercaseLevel: 'labels',
  showStreakBar: true,
  defaultCefr: 'B1',
  lastActiveTab: 'generator',
};

@Injectable({ providedIn: 'root' })
export class PreferenceService {
  private http  = inject(HttpClient);
  private save$ = new Subject<Preferences>();

  readonly prefs = signal<Preferences>({ ...DEFAULTS });

  constructor() {
    effect(() => this.applyToDOM(this.prefs()));

    this.save$.pipe(
      debounceTime(500),
      switchMap(p => this.http.put<Preferences>('/api/me/preferences', p)),
    ).subscribe();
  }

  load() {
    return this.http.get<Preferences>('/api/me/preferences').pipe(
      tap(p => this.prefs.set(p)),
    );
  }

  update(patch: Partial<Preferences>) {
    const next = { ...this.prefs(), ...patch };
    this.prefs.set(next);
    this.save$.next(next);
  }

  private applyToDOM(p: Preferences) {
    const root = document.documentElement;
    const a = ACCENT_MAP[p.accent] ?? ACCENT_MAP['lime'];
    root.style.setProperty('--color-accent', a.color);
    root.style.setProperty('--accent-soft',  a.soft);
    root.style.setProperty('--accent-border', a.border);
    root.dataset['density'] = p.density;
  }
}
