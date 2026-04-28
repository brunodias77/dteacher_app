# dTeacher — Design System

Referência completa do frontend dTeacher.
Stack: **Angular 20** · **TailwindCSS v4** · **Signals** · **Standalone Components**.

Fontes: `Inter` (corpo) · `JetBrains Mono` (código/labels) · `Fraunces` (display/serif).

---

## 1. Configuração TailwindCSS v4

```css
/* styles.css */
@import 'tailwindcss';

@theme {
  --color-bg-0: #0a0a0b;
  --color-bg-1: #111113;
  --color-bg-2: #18181b;
  --color-bg-3: #1f1f22;

  --color-line: #26262a;
  --color-line-strong: #34343a;

  --color-text-hi: #f5f5f4;
  --color-text-mid: #a1a1aa;
  --color-text-lo: #71717a;
  --color-text-dim: #52525b;

  --color-accent: #ccf381;
  --color-accent-ink: #0a0a0b;

  --color-danger: #f87171;
  --color-warn: #fbbf24;
  --color-ok: #86efac;
  --color-info: #93c5fd;

  --font-sans: 'Inter', ui-sans-serif, system-ui, sans-serif;
  --font-mono: 'JetBrains Mono', ui-monospace, monospace;
  --font-display: 'Fraunces', ui-serif, serif;
}

:root {
  --accent-soft: rgba(204, 243, 129, 0.1);
  --accent-border: rgba(204, 243, 129, 0.35);
}

html,
body {
  background: var(--color-bg-0);
  color: var(--color-text-hi);
  font-family: var(--font-sans);
  font-feature-settings: 'ss01', 'cv11';
  -webkit-font-smoothing: antialiased;
}
```

Com `@theme`, os tokens ficam disponíveis como classes Tailwind: `bg-bg-0`, `text-text-hi`, `border-line`.

---

## 2. Tokens de cor

**Use sempre os tokens — nunca cores hardcoded.**

### Backgrounds

| Token Tailwind | Hex       | Uso                         |
| -------------- | --------- | --------------------------- |
| `bg-bg-0`      | `#0a0a0b` | Fundo raiz (`html`, `body`) |
| `bg-bg-1`      | `#111113` | Superfícies primárias       |
| `bg-bg-2`      | `#18181b` | Superfícies elevadas        |
| `bg-bg-3`      | `#1f1f22` | Tooltips, dropdowns         |

### Bordas

| Token Tailwind       | Hex       | Uso                 |
| -------------------- | --------- | ------------------- |
| `border-line`        | `#26262a` | Borda padrão        |
| `border-line-strong` | `#34343a` | Borda em hover/foco |

### Texto

| Token Tailwind  | Hex       | Uso                         |
| --------------- | --------- | --------------------------- |
| `text-text-hi`  | `#f5f5f4` | Títulos, conteúdo principal |
| `text-text-mid` | `#a1a1aa` | Subtítulos, descrições      |
| `text-text-lo`  | `#71717a` | Eyebrows, metadados         |
| `text-text-dim` | `#52525b` | Placeholders, desabilitados |

### Accent

| Token Tailwind                  | Valor                    | Uso                      |
| ------------------------------- | ------------------------ | ------------------------ |
| `bg-accent` / `text-accent`     | `#ccf381`                | Ação principal, seleção  |
| `text-accent-ink`               | `#0a0a0b`                | Texto sobre fundo accent |
| `bg-[var(--accent-soft)]`       | `rgba(204,243,129,0.10)` | Background tinted sutil  |
| `border-[var(--accent-border)]` | `rgba(204,243,129,0.35)` | Borda tinted             |

### Status

| Token Tailwind | Hex       | Uso        |
| -------------- | --------- | ---------- |
| `text-danger`  | `#f87171` | Erros      |
| `text-warn`    | `#fbbf24` | Avisos     |
| `text-ok`      | `#86efac` | Sucesso    |
| `text-info`    | `#93c5fd` | Informação |

### Paleta de accent (temas)

Aplicado via `PreferenceService` atualizando CSS vars em `:root`:

| Chave           | `--color-accent` | `--accent-soft`          | `--accent-border`        |
| --------------- | ---------------- | ------------------------ | ------------------------ |
| `lime` (padrão) | `#ccf381`        | `rgba(204,243,129,0.10)` | `rgba(204,243,129,0.35)` |
| `amber`         | `#fbbf24`        | `rgba(251,191,36,0.10)`  | `rgba(251,191,36,0.35)`  |
| `cyan`          | `#67e8f9`        | `rgba(103,232,249,0.10)` | `rgba(103,232,249,0.35)` |
| `magenta`       | `#f0abfc`        | `rgba(240,171,252,0.12)` | `rgba(240,171,252,0.40)` |
| `white`         | `#f5f5f4`        | `rgba(245,245,244,0.08)` | `rgba(245,245,244,0.30)` |

---

## 3. Tipografia

| Classe Tailwind | Família          | Uso                              |
| --------------- | ---------------- | -------------------------------- |
| `font-sans`     | `Inter`          | Corpo de texto, UI geral         |
| `font-mono`     | `JetBrains Mono` | Código, labels, tags, números    |
| `font-display`  | `Fraunces`       | Títulos hero, destaque editorial |

### Escala de tamanhos

| Uso                  | Tailwind                        |
| -------------------- | ------------------------------- |
| Eyebrow / label mono | `text-[10.5px]`–`text-[11px]`   |
| Tag / chip label     | `text-[10.5px]`–`text-[11.5px]` |
| Body / descrição     | `text-[13px]`–`text-sm`         |
| Título de card       | `text-[20px]`                   |
| Título de seção      | `text-[22px]`                   |
| Título de tela       | `text-[32px]`–`text-[38px]`     |
| Display hero         | `text-[36px]+`                  |

---

## 4. Sistema de densidade

Atributo `data-density` no elemento raiz, controlado por `PreferenceService`:

```css
@layer utilities {
  [data-density='compact'] .den-pad {
    @apply px-4 py-3;
  }
  [data-density='cozy'] .den-pad {
    @apply px-6 py-5;
  }
  [data-density='roomy'] .den-pad {
    @apply px-8 py-7;
  }
}
```

---

## 5. Componentes base (`@layer components`)

```css
@layer components {
  .surface {
    @apply bg-bg-1 border border-line;
  }
  .surface-2 {
    @apply bg-bg-2 border border-line;
  }

  .input {
    @apply w-full bg-bg-0 border border-line-strong text-text-hi px-4 py-3.5
           transition-colors outline-none focus:border-accent;
  }

  .btn-primary {
    @apply inline-flex items-center gap-2 bg-accent text-accent-ink font-bold text-[13px]
           px-[18px] py-3 border border-accent transition-[filter,transform]
           hover:brightness-110 active:translate-y-px
           disabled:opacity-45 disabled:cursor-not-allowed disabled:brightness-100;
  }

  .btn-ghost {
    @apply inline-flex items-center gap-2 bg-transparent text-text-hi font-semibold text-[13px]
           px-4 py-[11px] border border-line-strong transition-colors
           hover:border-text-lo;
  }

  .chip-btn {
    @apply inline-flex items-center gap-2 font-mono text-[11.5px] uppercase tracking-[0.12em]
           px-3 py-[9px] border border-line bg-bg-1 text-text-hi
           transition-[border-color,color,background];
  }
  .chip-btn:hover {
    @apply border-line-strong;
  }
  .chip-btn.active {
    @apply border-accent bg-accent text-accent-ink;
  }
  .chip-btn.ghost {
    @apply bg-transparent;
  }

  .tag {
    @apply inline-flex items-center gap-1.5 font-mono text-[10.5px] uppercase tracking-[0.1em]
           px-[7px] py-[3px] border border-line text-text-mid bg-bg-1;
  }
  .tag.accent {
    @apply border-[var(--accent-border)] text-accent bg-[var(--accent-soft)];
  }

  .w-hover {
    @apply cursor-pointer px-0.5 -mx-0.5 border-b border-dashed border-transparent
           transition-all duration-[120ms]
           hover:bg-accent hover:text-accent-ink hover:border-accent-ink;
  }

  .hl-t {
    box-shadow: inset 0 1px 0 var(--color-line);
  }
  .hl-b {
    box-shadow: inset 0 -1px 0 var(--color-line);
  }
}
```

---

## 6. Animações

```css
@keyframes fadeSlide {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: none;
  }
}
@keyframes pulseDot {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}
@keyframes wavebar {
  0%,
  100% {
    transform: scaleY(0.6);
  }
  50% {
    transform: scaleY(1.3);
  }
}

@layer utilities {
  .animate-fade-slide {
    animation: fadeSlide 0.28s cubic-bezier(0.2, 0.8, 0.2, 1);
  }
  .pulse-dot {
    animation: pulseDot 1.4s ease-in-out infinite;
  }
}
```

| Classe               | Uso                                          |
| -------------------- | -------------------------------------------- |
| `animate-fade-slide` | Entrada de telas e seções (div raiz da tela) |
| `pulse-dot`          | Indicador "ao vivo" / loading sutil          |
| `animate-spin`       | Spinner (nativo Tailwind)                    |
| `wavebar`            | Barras de áudio animadas                     |

---

## 7. Estrutura do projeto Angular 20

```
src/
├── app/
│   ├── app.config.ts           ← ApplicationConfig (providers)
│   ├── app.routes.ts           ← Rotas principais com loadComponent
│   ├── app.ts                  ← Root shell com RouterOutlet
│   ├── core/
│   │   ├── guards/
│   │   │   └── auth.guard.ts   ← CanActivateFn
│   │   ├── interceptors/
│   │   │   └── auth.interceptor.ts ← HttpInterceptorFn JWT
│   │   └── services/
│   │       ├── auth.service.ts     ← signal<User|null>
│   │       ├── preference.service.ts ← aplica accent/density no DOM
│   │       └── stats.service.ts    ← streak e daily log
│   ├── features/
│   │   ├── auth/
│   │   │   ├── login/login.component.ts
│   │   │   └── register/register.component.ts
│   │   ├── generator/generator.component.ts
│   │   ├── flashcards/flashcards.component.ts
│   │   ├── vocabulary/vocabulary.component.ts
│   │   ├── text-study/text-study.component.ts
│   │   ├── tutor/tutor.component.ts
│   │   ├── fill-in/fill-in.component.ts
│   │   ├── phonetics/phonetics.component.ts
│   │   └── immersion/immersion.component.ts
│   └── shared/
│       ├── components/
│       │   ├── icon/icon.component.ts
│       │   ├── spinner/spinner.component.ts
│       │   ├── audio-pill/audio-pill.component.ts
│       │   └── clickable-sentence/clickable-sentence.component.ts
│       └── pipes/
│           └── pad-start.pipe.ts
├── styles.css                  ← @import "tailwindcss" + @theme + @layer
└── index.html
```

---

## 8. Bootstrap

```typescript
// main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { appConfig } from './app/app.config';

bootstrapApplication(App, appConfig).catch(console.error);
```

```typescript
// app.config.ts
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding, withInMemoryScrolling } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes,
      withComponentInputBinding(),
      withInMemoryScrolling({ scrollPositionRestoration: 'enabled' }),
    ),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
```

---

## 9. Roteamento

```typescript
// app.routes.ts
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/generator', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'generator',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/generator/generator.component').then((m) => m.GeneratorComponent),
  },
  {
    path: 'flashcards',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/flashcards/flashcards.component').then((m) => m.FlashcardsComponent),
  },
  {
    path: 'vocabulary',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/vocabulary/vocabulary.component').then((m) => m.VocabularyComponent),
  },
  {
    path: 'text-study',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/text-study/text-study.component').then((m) => m.TextStudyComponent),
  },
  {
    path: 'tutor',
    canActivate: [authGuard],
    loadComponent: () => import('./features/tutor/tutor.component').then((m) => m.TutorComponent),
  },
  {
    path: 'fill-in',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/fill-in/fill-in.component').then((m) => m.FillInComponent),
  },
  {
    path: 'phonetics',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/phonetics/phonetics.component').then((m) => m.PhoneticsComponent),
  },
  {
    path: 'immersion',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/immersion/immersion.component').then((m) => m.ImmersionComponent),
  },
];
```

---

## 10. Guard e Interceptor

```typescript
// core/guards/auth.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.user()) return true;
  return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};
```

```typescript
// core/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (!token) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
```

---

## 11. Padrões de serviço com Signals

### AuthService

```typescript
// core/services/auth.service.ts
import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

export interface AuthUser {
  id: string;
  email: string;
  name: string;
}
export interface LoginResponse {
  accessToken: string;
  expiresIn: number;
  user: AuthUser;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private _user = signal<AuthUser | null>(this.loadUser());
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => !!this._user());

  login(email: string, password: string) {
    return this.http.post<LoginResponse>('/api/auth/login', { email, password }).pipe(
      tap((res) => {
        localStorage.setItem('token', res.accessToken);
        localStorage.setItem('user', JSON.stringify(res.user));
        this._user.set(res.user);
      }),
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  private loadUser(): AuthUser | null {
    try {
      return JSON.parse(localStorage.getItem('user') ?? 'null');
    } catch {
      return null;
    }
  }
}
```

### PreferenceService

```typescript
// core/services/preference.service.ts
import { Injectable, signal, effect, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { debounceTime, Subject, switchMap } from 'rxjs';

export interface Preferences {
  accent: 'lime' | 'amber' | 'cyan' | 'magenta' | 'white';
  density: 'compact' | 'cozy' | 'roomy';
  uppercaseLevel: 'labels' | 'headings' | 'off';
  showStreakBar: boolean;
  defaultCefr: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
  lastActiveTab: string;
}

const ACCENT_MAP: Record<string, { color: string; soft: string; border: string }> = {
  lime: { color: '#ccf381', soft: 'rgba(204,243,129,0.10)', border: 'rgba(204,243,129,0.35)' },
  amber: { color: '#fbbf24', soft: 'rgba(251,191,36,0.10)', border: 'rgba(251,191,36,0.35)' },
  cyan: { color: '#67e8f9', soft: 'rgba(103,232,249,0.10)', border: 'rgba(103,232,249,0.35)' },
  magenta: { color: '#f0abfc', soft: 'rgba(240,171,252,0.12)', border: 'rgba(240,171,252,0.40)' },
  white: { color: '#f5f5f4', soft: 'rgba(245,245,244,0.08)', border: 'rgba(245,245,244,0.30)' },
};

@Injectable({ providedIn: 'root' })
export class PreferenceService {
  private http = inject(HttpClient);
  private save$ = new Subject<Preferences>();

  readonly prefs = signal<Preferences>({
    accent: 'lime',
    density: 'cozy',
    uppercaseLevel: 'labels',
    showStreakBar: true,
    defaultCefr: 'B1',
    lastActiveTab: 'generator',
  });

  constructor() {
    effect(() => this.applyToDOM(this.prefs()));

    this.save$
      .pipe(
        debounceTime(500),
        switchMap((p) => this.http.put('/api/me/preferences', p)),
      )
      .subscribe();
  }

  load() {
    return this.http.get<Preferences>('/api/me/preferences').pipe(tap((p) => this.prefs.set(p)));
  }

  update(patch: Partial<Preferences>) {
    const next = { ...this.prefs(), ...patch };
    this.prefs.set(next);
    this.save$.next(next);
  }

  private applyToDOM(p: Preferences) {
    const root = document.documentElement;
    const a = ACCENT_MAP[p.accent];
    root.style.setProperty('--color-accent', a.color);
    root.style.setProperty('--accent-soft', a.soft);
    root.style.setProperty('--accent-border', a.border);
    root.dataset['density'] = p.density;
  }
}
```

---

## 12. Padrões de componente Angular 20

### Regras obrigatórias

| Regra                      | Detalhe                                                        |
| -------------------------- | -------------------------------------------------------------- |
| **Standalone**             | Todo componente usa `standalone: true` (omitido no Angular 20) |
| **`inject()`**             | Nunca injeção por construtor                                   |
| **`input()` / `output()`** | Nunca `@Input()` / `@Output()`                                 |
| **`model()`**              | Two-way binding em vez de par input+output                     |
| **`@if` / `@for`**         | Nunca `*ngIf` / `*ngFor`                                       |
| **`OnPush`**               | Todos os componentes usam `ChangeDetectionStrategy.OnPush`     |
| **Sem `ngOnInit`**         | Inicialização no `constructor()` + `effect()` / `resource()`   |

### Componente mínimo

```typescript
import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-foo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<div class="surface den-pad">...</div>`,
})
export class FooComponent {}
```

### Componente com `input()` e `output()`

```typescript
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

@Component({
  selector: 'app-chip-btn',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <button class="chip-btn" [class.active]="added()" [disabled]="added()" (click)="clicked.emit()">
      @if (added()) {
        ✓ Adicionado
      } @else {
        + Adicionar
      }
    </button>
  `,
})
export class ChipBtnComponent {
  added = input(false);
  clicked = output<void>();
}
```

### Componente com `model()` (two-way)

```typescript
import { ChangeDetectionStrategy, Component, model } from '@angular/core';

@Component({
  selector: 'app-cefr-select',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="inline-flex border border-line">
      @for (level of levels; track level) {
        <button
          class="chip-btn ghost border-0 border-r border-line last:border-r-0"
          [class.active]="value() === level"
          (click)="value.set(level)"
        >
          {{ level }}
        </button>
      }
    </div>
  `,
})
export class CefrSelectComponent {
  value = model<string>('B1');
  levels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
}
```

### `effect()` em vez de `ngOnChanges`

```typescript
constructor() {
  effect(() => {
    const q = this.query();
    if (q.length > 1) this.search(q);
  });
}
```

### `afterNextRender` em vez de `ngAfterViewInit`

```typescript
import { afterNextRender, inject, ElementRef } from '@angular/core';

constructor() {
  const el = inject(ElementRef);
  afterNextRender(() => {
    el.nativeElement.querySelector('input')?.focus();
  });
}
```

### `DestroyRef` em vez de `ngOnDestroy`

```typescript
import { inject, DestroyRef } from '@angular/core';

private destroyRef = inject(DestroyRef);

constructor() {
  const sub = this.someObservable$.subscribe(...);
  this.destroyRef.onDestroy(() => sub.unsubscribe());
}
```

---

## 13. Formulários (Reactive Forms)

```typescript
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <form [formGroup]="form" (ngSubmit)="submit()" class="space-y-4">
      <div class="space-y-1">
        <input formControlName="email" type="email" class="input" placeholder="seu@email.com" />
        @if (form.controls.email.invalid && form.controls.email.touched) {
          <p class="text-[13px] text-danger">E-mail inválido</p>
        }
      </div>

      <div class="space-y-1">
        <input formControlName="password" type="password" class="input" placeholder="Senha" />
        @if (form.controls.password.invalid && form.controls.password.touched) {
          <p class="text-[13px] text-danger">Senha obrigatória</p>
        }
      </div>

      @if (error()) {
        <p class="text-[13px] text-danger">{{ error() }}</p>
      }

      <button
        type="submit"
        class="btn-primary w-full justify-center"
        [disabled]="loading() || form.invalid"
      >
        @if (loading()) {
          A entrar…
        } @else {
          Entrar
        }
      </button>
    </form>
  `,
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  error = signal('');

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set('');
    const { email, password } = this.form.getRawValue();
    this.auth.login(email, password).subscribe({
      next: () => this.router.navigate(['/generator']),
      error: () => {
        this.error.set('E-mail ou senha incorretos.');
        this.loading.set(false);
      },
    });
  }
}
```

---

## 14. Padrões de requisição HTTP com signals

### `resource()` — dados assíncronos

```typescript
import { Component, inject, signal, resource } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Component({ ... })
export class FlashcardsComponent {
  private http = inject(HttpClient);

  cards = resource({
    loader: () => firstValueFrom(
      this.http.get<{ cards: Flashcard[]; totalDue: number }>('/api/flashcards')
    ),
  });
}
```

```html
@if (cards.isLoading()) {
<app-spinner />
} @else if (cards.error()) {
<p class="text-danger">Erro ao carregar cartões.</p>
} @else { @for (card of cards.value()?.cards; track card.id) { ... } }
```

### Mutação com `signal` de loading

```typescript
loading = signal(false);
error   = signal('');

addFlashcard(english: string, portuguese: string, source: string) {
  this.loading.set(true);
  this.http.post('/api/flashcards', { english, portuguese, source }).subscribe({
    next: () => this.loading.set(false),
    error: () => {
      this.error.set('Não foi possível salvar.');
      this.loading.set(false);
    },
  });
}
```

### Optimistic update (chat)

```typescript
messages = signal<ChatMessage[]>([]);

sendMessage(content: string) {
  const optimistic: ChatMessage = { id: crypto.randomUUID(), role: 'user', content };
  this.messages.update(msgs => [...msgs, optimistic]);

  this.http.post<{ aiMessage: AiMessage }>(`/api/chat/sessions/${this.sessionId}/messages`, { content })
    .subscribe({
      next: res => this.messages.update(msgs => [...msgs, res.aiMessage]),
      error: () => this.messages.update(msgs => msgs.filter(m => m.id !== optimistic.id)),
    });
}
```

---

## 15. Componentes compartilhados

### `IconComponent`

```typescript
@Component({
  selector: 'app-icon',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<svg
    [attr.width]="size()"
    [attr.height]="size()"
    [attr.stroke-width]="stroke()"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-linecap="round"
  >
    <ng-container [ngSwitch]="name()">
      <!-- paths por nome -->
    </ng-container>
  </svg>`,
})
export class IconComponent {
  name = input.required<string>();
  size = input(16);
  stroke = input(1.5);
}
```

### `SpinnerComponent`

```typescript
@Component({
  selector: 'app-spinner',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <svg
      class="animate-spin text-text-lo"
      [attr.width]="size()"
      [attr.height]="size()"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
    >
      <circle cx="12" cy="12" r="10" stroke-width="2" class="opacity-20" />
      <path d="M12 2a10 10 0 0 1 10 10" stroke-width="2" />
    </svg>
  `,
})
export class SpinnerComponent {
  size = input(16);
}
```

### `ClickableSentenceComponent`

```typescript
@Component({
  selector: 'app-clickable-sentence',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <span>
      @for (word of words(); track $index) {
        <span class="w-hover" (click)="wordClicked.emit(word)">{{ word }}</span
        >{{ ' ' }}
      }
    </span>
  `,
})
export class ClickableSentenceComponent {
  sentence = input.required<string>();
  wordClicked = output<string>();

  words = computed(() =>
    this.sentence()
      .split(' ')
      .map((w) => w.replace(/[.,!?;:()"'—]/g, '')),
  );
}
```

### `AudioPillComponent`

```typescript
@Component({
  selector: 'app-audio-pill',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="inline-flex items-stretch border border-line-strong bg-bg-1">
      <button
        (click)="play()"
        [disabled]="loading()"
        class="flex items-center gap-2 px-3 py-2 border-r border-line hover:bg-bg-2 disabled:opacity-50"
      >
        @if (loading()) {
          <app-spinner [size]="14" />
        } @else if (playing()) {
          <span class="wave">...</span>
        } @else {
          <app-icon name="play" [size]="14" />
        }
      </button>
      <button
        (click)="changeSpeed(-1)"
        class="px-2 text-text-lo hover:text-text-hi hover:bg-bg-2 border-r border-line"
      >
        <app-icon name="minus" [size]="12" />
      </button>
      <span
        class="px-2 py-2 font-mono text-[11px] text-text-mid tabular-nums w-[42px] text-center select-none"
      >
        {{ speed() }}×
      </span>
      <button
        (click)="changeSpeed(1)"
        class="px-2 text-text-lo hover:text-text-hi hover:bg-bg-2 border-l border-line"
      >
        <app-icon name="plus" [size]="12" />
      </button>
    </div>
  `,
})
export class AudioPillComponent {
  text = input.required<string>();

  loading = signal(false);
  playing = signal(false);
  speed = signal(1.0);

  changeSpeed(delta: number) {
    const steps = [0.5, 0.75, 1.0, 1.25, 1.5];
    const idx = steps.indexOf(this.speed());
    const next = steps[Math.max(0, Math.min(steps.length - 1, idx + delta))];
    this.speed.set(next);
  }

  play() {
    const utt = new SpeechSynthesisUtterance(this.text());
    utt.rate = this.speed();
    utt.lang = 'en-US';
    this.playing.set(true);
    utt.onend = () => this.playing.set(false);
    speechSynthesis.speak(utt);
  }
}
```

---

## 16. Padrões de UI

### Botões

```html
<!-- Primário com loading -->
<button class="btn-primary" [disabled]="loading()">
  @if (loading()) { <app-spinner [size]="14" /> A gerar… } @else {
  <app-icon name="sparkles" [size]="14" /> Gerar frases }
</button>

<!-- Secundário -->
<button class="btn-ghost"><app-icon name="volume" [size]="14" /> Ouvir pronúncia</button>

<!-- Segmented control -->
<div class="inline-flex border border-line">
  <button
    class="chip-btn ghost border-0 border-r border-line"
    [class.active]="mode() === 'classic'"
    (click)="mode.set('classic')"
  >
    <app-icon name="eye" [size]="12" /> Clássico
  </button>
  <button
    class="chip-btn ghost border-0"
    [class.active]="mode() === 'typing'"
    (click)="mode.set('typing')"
  >
    <app-icon name="kbd" [size]="12" /> Escrita
  </button>
</div>
```

### Tags

```html
<span class="tag">42 cartões</span>
<span class="tag accent"><app-icon name="flame" [size]="11" /> 7 dias</span>
```

### Superfície com barra de progresso

```html
<div class="surface-2 relative overflow-hidden">
  <div
    class="absolute top-0 left-0 h-0.5 bg-accent transition-all"
    [style.width]="progress() + '%'"
  ></div>
  <div class="den-pad">...</div>
</div>
```

### Input com hint de teclado

```html
<div class="relative flex-1">
  <input class="input pr-28 font-mono text-[14px]" placeholder="travel, airport..." />
  <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
    <kbd>⏎</kbd>
  </div>
</div>
```

### Card de item de lista

```html
<div class="surface den-pad group hover:border-line-strong transition-colors">
  <div class="flex items-start gap-5">
    <div class="font-mono text-[11px] text-text-dim pt-1 tabular-nums">
      {{ (index() + 1).toString().padStart(2, '0') }}
    </div>
    <div class="flex-1 min-w-0 space-y-2">
      <div class="text-[20px] leading-snug text-text-hi font-medium">{{ item().english }}</div>
      <div
        class="text-[13px] text-text-mid blur-sm hover:blur-none transition-all duration-300 cursor-help"
        title="Ver tradução"
      >
        {{ item().portuguese }}
      </div>
    </div>
    <div class="flex items-center gap-2 flex-shrink-0">
      <app-audio-pill [text]="item().english" />
      <app-chip-btn [added]="added()" (clicked)="onAdd()" />
    </div>
  </div>
</div>
```

### Cabeçalho de tela

```html
<div class="animate-fade-slide space-y-10">
  <div>
    <div class="font-mono text-[11px] uppercase tracking-[0.12em] text-text-lo mb-2">
      01 · Generator
    </div>
    <h1 class="text-[38px] font-semibold leading-[1.05] tracking-tight">
      Gere frases com
      <span class="font-display italic text-accent">suas palavras</span>
    </h1>
    <p class="text-text-mid text-[14px] mt-3 max-w-[52ch]">
      Digite até cinco palavras e receba frases naturais em inglês.
    </p>
  </div>
  <!-- conteúdo da tela -->
</div>
```

### Cabeçalho de seção

```html
<div class="flex items-end justify-between gap-4 mb-5">
  <div>
    <div class="font-mono text-[11px] uppercase tracking-[0.12em] text-text-lo mb-2">Eyebrow</div>
    <h2 class="text-[22px] font-semibold text-text-hi tracking-tight">Título</h2>
  </div>
  <div><!-- slot direito: tags, botões --></div>
</div>
```

### Estado vazio

```html
<div class="surface p-10 flex flex-col items-center text-center">
  <pre class="font-mono text-[11px] leading-[1.2] text-text-dim mb-6">
·  ·  ·  ·  ·  ·  ·
·  ·        ·  ·  ·
·  ·  ( )   ·  ·  ·
·  ·        ·  ·  ·
·  ·  ·  ·  ·  ·  ·</pre
  >
  <div class="font-display text-[26px] text-text-hi leading-tight mb-2">Nenhum cartão ainda</div>
  <p class="text-text-mid text-[13px] max-w-sm">
    Gere frases ou adicione palavras para criar o seu deck.
  </p>
  <div class="mt-6">
    <button class="btn-primary" routerLink="/generator">Gerar frases</button>
  </div>
</div>
```

### Feedback correto / incorreto

```html
<!-- Correto -->
<div class="border border-[var(--accent-border)] bg-[var(--accent-soft)] p-4">
  <div class="font-mono text-[10px] uppercase tracking-[0.1em] text-text-lo mb-1">
    A sua resposta
  </div>
  <div class="font-medium text-accent">{{ resposta }}</div>
</div>

<!-- Incorreto -->
<div class="border border-[rgba(248,113,113,0.3)] bg-[rgba(248,113,113,0.05)] p-4">
  <div class="font-mono text-[10px] uppercase tracking-[0.1em] text-text-lo mb-1">
    A sua resposta
  </div>
  <div class="font-medium text-danger">{{ resposta }}</div>
</div>
```

### Tradução oculta (blur reveal)

```html
<span class="blur-sm hover:blur-none transition-all duration-300 cursor-help" title="Ver tradução">
  {{ traducao }}
</span>
```

### Barra de progresso segmentada

```html
<div class="flex gap-0.5 h-1">
  @for (seg of segments(); track $index) {
  <span
    class="flex-1 transition-colors"
    [class]="$index < filled() ? 'bg-accent' : 'bg-line'"
  ></span>
  }
</div>
```

### Brand mark (logo)

```html
<div class="w-7 h-7 grid grid-cols-2 grid-rows-2 gap-0.5 bg-bg-0">
  <i class="block bg-accent"></i>
  <i class="block bg-text-hi"></i>
  <i class="block bg-text-hi"></i>
  <i class="block bg-text-hi"></i>
</div>
```

---

## 17. Tabs de navegação

| `id`         | Label     | Ícone      | Rota          |
| ------------ | --------- | ---------- | ------------- |
| `generator`  | Gerar     | `sparkles` | `/generator`  |
| `flashcards` | Cartões   | `layers`   | `/flashcards` |
| `vocabulary` | Vocab     | `bookmark` | `/vocabulary` |
| `textStudy`  | Texto     | `text`     | `/text-study` |
| `tutor`      | Tutor IA  | `brain`    | `/tutor`      |
| `fillin`     | Completar | `kbd`      | `/fill-in`    |
| `phonetics`  | Fonética  | `zap`      | `/phonetics`  |
| `immersion`  | Imersão   | `book`     | `/immersion`  |

---

## 18. Ícones disponíveis (`<app-icon name="" [size]="16" [stroke]="1.5" />`)

SVG inline — sem biblioteca externa.

| Nome             | Uso                 |
| ---------------- | ------------------- |
| `sparkles`       | Gerador / IA        |
| `layers`         | Flashcards / deck   |
| `bookmark`       | Vocabulário         |
| `text`           | Texto               |
| `brain`          | Tutor IA            |
| `kbd`            | Fill-in / teclado   |
| `zap`            | Fonética            |
| `book`           | Imersão             |
| `play` / `pause` | Áudio               |
| `volume`         | Pronúncia           |
| `plus` / `minus` | Adicionar / remover |
| `check`          | Confirmação         |
| `x`              | Fechar / remover    |
| `arrow`          | Navegação           |
| `rotate`         | Reiniciar           |
| `send`           | Enviar              |
| `loader`         | Loading             |
| `flame`          | Streak              |
| `settings`       | Configurações       |
| `bulb`           | Dica                |
| `eye`            | Visualizar          |
| `alert`          | Alerta              |

---

## 19. Checklist de implementação

### Angular 20

- [ ] Componente standalone (sem `NgModule`, `CommonModule`, `BrowserModule`)
- [ ] `ChangeDetectionStrategy.OnPush` em todo componente
- [ ] `inject()` — sem injeção por parâmetro de construtor
- [ ] `input()` / `output()` / `model()` — sem `@Input()` / `@Output()`
- [ ] `@if`, `@for`, `@switch` — sem `*ngIf` / `*ngFor`
- [ ] Sem `ngOnInit` — usar `constructor()` + `effect()` / `resource()`
- [ ] Imports mínimos: apenas o que o template usa

### TailwindCSS v4

- [ ] Div raiz usa `animate-fade-slide`
- [ ] Tokens Tailwind (`text-text-hi`, `bg-bg-1`, `border-line`) — sem cores hardcoded
- [ ] `accent-soft` e `accent-border` via `bg-[var(--accent-soft)]` (valor arbitrário)
- [ ] Cards usam `surface den-pad` com `hover:border-line-strong transition-colors`
- [ ] Eyebrow: `font-mono text-[11px] uppercase tracking-[0.12em] text-text-lo`
- [ ] Estados vazios: padrão `Empty` com ASCII art + `font-display`
- [ ] Números de índice: `tabular-nums` + `padStart(2, '0')`
- [ ] Botão com loading: `<app-spinner />` + `[disabled]="loading()"`

### HTTP

- [ ] Interceptor JWT ativo em `provideHttpClient(withInterceptors([authInterceptor]))`
- [ ] `authGuard` protegendo todas as rotas autenticadas
- [ ] Rotas lazy com `loadComponent()` — sem imports estáticos de telas
- [ ] Erros HTTP tratados com `signal('error message')` e exibidos no template
