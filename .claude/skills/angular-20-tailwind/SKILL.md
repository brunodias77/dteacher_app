---
name: angular-20-tailwind
description: >
  Scaffold and implement Angular v20+ applications using only Angular CLI 20+, standalone components, signals,
  Reactive Forms, functional guards/interceptors/resolvers, provideRouter(), loadComponent() and loadChildren().
  No NgModules, no OnInit, no external UI libraries. All UI built from scratch with TailwindCSS v4.
  Triggers on: creating components, forms, routes, guards, interceptors, resolvers, services, pipes, directives,
  or any Angular architecture task that requires modern, idiomatic Angular 20 code.
when_to_use: >
  Use when the user asks to create, scaffold, or fix Angular components, services, guards, interceptors,
  resolvers, pipes, directives, routes, forms, or any Angular architecture. Also triggers on phrases like
  "create an Angular component", "add a route", "set up a service", "write a guard", "build a form",
  "lazy load a module", "use signals", "refactor to Angular 20", or "how do I do X in Angular".
allowed-tools: Bash(ng *) Bash(npm *) Bash(npx *)
---

# Angular 20 + TailwindCSS — Skill

Produce clean, fully typed Angular v20+ code. Every file must compile with Angular CLI 20+ without warnings.

---

## ⚙️ Non-Negotiable Rules

| Rule | Detail |
|---|---|
| **No NgModules** | Never generate `NgModule`. Use standalone APIs everywhere. |
| **No `OnInit`** | Initialisation logic goes in the `constructor()` or via `effect()` / `resource()`. |
| **No external UI libs** | No Angular Material, PrimeNG, Bootstrap, Ng-Zorro, etc. |
| **TailwindCSS only** | All styling via Tailwind utility classes. Use `@apply` sparingly. |
| **`inject()` everywhere** | Never use constructor-parameter injection. |
| **Signal-first** | Prefer `signal()`, `computed()`, `linkedSignal()`, `resource()` over `BehaviorSubject`. |
| **Typed strictly** | No `any`. Enable `strict: true` in `tsconfig.json`. |
| **`input()` / `output()`** | Never use `@Input()` / `@Output()` decorators. |
| **`model()`** | Use `model()` for two-way bindings instead of paired input+output. |
| **Native control flow** | Use `@if`, `@for`, `@switch` — never `*ngIf`, `*ngFor`, `*ngSwitch`. |
| **`host` object** | Use the `host: {}` property — never `@HostBinding` / `@HostListener`. |

---

## 📁 Project Structure

```
src/
├── app/
│   ├── app.config.ts          ← ApplicationConfig (providers)
│   ├── app.routes.ts          ← Route definitions
│   ├── app.component.ts       ← Root shell
│   ├── core/
│   │   ├── guards/            ← Functional CanActivateFn guards
│   │   ├── interceptors/      ← Functional HttpInterceptorFn
│   │   ├── resolvers/         ← Functional ResolveFn
│   │   └── services/          ← Injectable services with signal state
│   ├── features/
│   │   └── <feature>/
│   │       ├── <feature>.routes.ts
│   │       └── components/
│   └── shared/
│       ├── components/        ← Reusable UI components
│       ├── pipes/             ← Standalone pipes
│       └── directives/        ← Standalone directives
├── styles.css                 ← @import "tailwindcss";
└── index.html
```

---

## 🔧 Bootstrap

```typescript
// main.ts
bootstrapApplication(App, appConfig).catch(console.error);
```

```typescript
// app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes,
      withComponentInputBinding(),
      withInMemoryScrolling({ scrollPositionRestoration: 'enabled' }),
    ),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
```

```css
/* styles.css */
@import "tailwindcss";
```

---

## 🔑 Functional Guard

```typescript
export const authGuard: CanActivateFn = async (route, state) => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated()) return true;
  return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};

export const roleGuard = (roles: string[]): CanActivateFn => () => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  const role   = auth.user()?.role;
  if (role && roles.includes(role)) return true;
  return router.createUrlTree(['/unauthorized']);
};
```

---

## 🌐 Functional HTTP Interceptor

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (!token) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
```

---

## 🗺️ Routing

```typescript
export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard(['admin'])],
    loadChildren: () =>
      import('./features/admin/admin.routes').then(m => m.adminRoutes),
  },
  {
    path: '**',
    loadComponent: () =>
      import('./features/not-found/not-found.component').then(m => m.NotFoundComponent),
  },
];
```

---

## 🔢 Lifecycle Replacement

| Old (avoid) | New (use) |
|---|---|
| `ngOnInit` | `constructor()` + `effect()` or `resource()` |
| `ngOnChanges` | `computed()` reacting to `input()` signals |
| `ngAfterViewInit` | `afterNextRender(() => { ... })` |
| `ngOnDestroy` | `DestroyRef` via `inject(DestroyRef).onDestroy(fn)` |

```typescript
export class MyComponent {
  private destroyRef = inject(DestroyRef);

  constructor() {
    effect(() => console.log('userId changed:', this.userId()));
    afterNextRender(() => { /* DOM access — SSR safe */ });
    this.destroyRef.onDestroy(() => { /* cleanup */ });
  }
}
```

---

## ✅ Checklist Before Committing

- [ ] No `NgModule`, `CommonModule`, or `BrowserModule` imported anywhere
- [ ] No `@Input()` / `@Output()` decorators — using `input()` / `output()` / `model()`
- [ ] No `ngOnInit` — constructor / `effect()` / `resource()` used instead
- [ ] No external UI library dependencies in `package.json`
- [ ] All components use `ChangeDetectionStrategy.OnPush`
- [ ] All components import only what they use (no wildcard imports)
- [ ] `provideRouter()` and `provideHttpClient()` present in `app.config.ts`
- [ ] `withComponentInputBinding()` enabled in `provideRouter()`
- [ ] TailwindCSS v4 installed: `@import "tailwindcss"` in `styles.css`
- [ ] Strict TypeScript — no `any`, no implicit types

---

## 📚 Additional References

Load these files when the task requires detailed patterns:

- **Components**: full Button example, `model()`, `@defer`, `viewChild`, `contentChildren`, standalone pipes/directives → [references/component-patterns.md](references/component-patterns.md)
- **Signals & async**: Signal Service, `linkedSignal`, `resource()`, Signal Store, optimistic updates, RxJS interop → [references/signal-patterns.md](references/signal-patterns.md)
- **Routing**: route params via `input()`, `canMatch`, nested tabs, resolver, programmatic navigation → [references/routing-patterns.md](references/routing-patterns.md)
- **Forms**: Reactive Forms patterns, validation, form submission → [references/forms-patterns.md](references/forms-patterns.md)
- **UI**: TailwindCSS component patterns (cards, badges, tables, empty states) → [references/ui-patterns.md](references/ui-patterns.md)
