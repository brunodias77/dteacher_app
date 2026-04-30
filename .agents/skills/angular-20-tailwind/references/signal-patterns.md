# Signal Patterns — Angular 20

## Signal Service (state management)

```typescript
// core/services/auth.service.ts
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http   = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly _user    = signal<User | null>(null);
  private readonly _loading = signal(false);
  private readonly _error   = signal<string | null>(null);

  readonly user            = this._user.asReadonly();
  readonly loading         = this._loading.asReadonly();
  readonly error           = this._error.asReadonly();
  readonly isAuthenticated = computed(() => this._user() !== null);
  readonly isAdmin         = computed(() => this._user()?.role === 'admin');

  async login(credentials: { email: string; password: string }): Promise<boolean> {
    this._loading.set(true);
    this._error.set(null);
    try {
      const res = await firstValueFrom(
        this.http.post<{ token: string; user: User }>('/api/auth/login', credentials),
      );
      localStorage.setItem('token', res.token);
      this._user.set(res.user);
      return true;
    } catch {
      this._error.set('Invalid credentials.');
      return false;
    } finally {
      this._loading.set(false);
    }
  }

  logout(): void {
    this._user.set(null);
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
```

---

## linkedSignal — Dependent Reset

```typescript
const categories = signal(['All', 'Tech', 'Design']);
// Resets to first category whenever the list changes
const activeCategory = linkedSignal(() => categories()[0]);
```

---

## resource() — Async with Abort

```typescript
listResource = resource({
  params: () => ({ page: this.page(), search: this.search() }),
  loader: async ({ params, abortSignal }) => {
    const url = `/api/items?page=${params.page}&q=${params.search}`;
    const res = await fetch(url, { signal: abortSignal });
    if (!res.ok) throw new Error('Request failed');
    return res.json() as Promise<Item[]>;
  },
  defaultValue: [] as Item[],
});
```

## resource() in a Component

```typescript
@Component({
  selector: 'app-user-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (userResource.isLoading()) {
      <span class="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></span>
    } @else if (userResource.error()) {
      <p class="text-sm text-red-700">Failed to load user.</p>
    } @else if (userResource.hasValue()) {
      <h2 class="text-xl font-bold">{{ userResource.value()!.name }}</h2>
    }
  `,
})
export class UserDetailComponent {
  id = input.required<string>();
  private http = inject(HttpClient);

  userResource = resource({
    params: () => ({ id: this.id() }),
    loader: ({ params }) =>
      firstValueFrom(this.http.get<User>(`/api/users/${params.id}`)),
  });
}
```

---

## Signal Store (complex state)

```typescript
@Injectable({ providedIn: 'root' })
export class CartStore {
  private state = signal({ items: [] as CartItem[], open: false });

  readonly items  = computed(() => this.state().items);
  readonly open   = computed(() => this.state().open);
  readonly total  = computed(() =>
    this.items().reduce((s, i) => s + i.price * i.qty, 0),
  );
  readonly count  = computed(() =>
    this.items().reduce((s, i) => s + i.qty, 0),
  );

  add(item: CartItem) {
    this.state.update(s => {
      const existing = s.items.find(i => i.id === item.id);
      const items = existing
        ? s.items.map(i => i.id === item.id ? { ...i, qty: i.qty + 1 } : i)
        : [...s.items, { ...item, qty: 1 }];
      return { ...s, items };
    });
  }

  remove(id: string) {
    this.state.update(s => ({ ...s, items: s.items.filter(i => i.id !== id) }));
  }

  toggle() {
    this.state.update(s => ({ ...s, open: !s.open }));
  }
}
```

---

## Optimistic Updates

```typescript
async toggleDone(id: string) {
  const prev = this.todos();
  // 1. Optimistic UI update
  this.todos.update(list =>
    list.map(t => t.id === id ? { ...t, done: !t.done } : t),
  );
  try {
    await firstValueFrom(this.http.patch(`/api/todos/${id}/toggle`, {}));
  } catch {
    // 2. Rollback on error
    this.todos.set(prev);
  }
}
```

---

## RxJS ↔ Signals Interop

```typescript
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs';
import { of } from 'rxjs';

@Component({...})
export class SearchComponent {
  query   = signal('');
  private http = inject(HttpClient);

  results = toSignal(
    toObservable(this.query).pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q =>
        q.length >= 2
          ? this.http.get<Result[]>(`/api/search?q=${q}`)
          : of([]),
      ),
      catchError(() => of([])),
    ),
    { initialValue: [] as Result[] },
  );
}
```
