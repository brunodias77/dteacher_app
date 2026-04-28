# UI Patterns — TailwindCSS v4

## Card

```html
<div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
  <h3 class="text-base font-semibold text-gray-900">Title</h3>
  <p class="mt-1 text-sm text-gray-500">Description</p>
</div>
```

---

## Badge

```html
<!-- Green: active -->
<span class="inline-flex items-center rounded-full bg-green-50 px-2 py-0.5 text-xs font-medium text-green-700 ring-1 ring-green-600/20">
  Active
</span>

<!-- Red: inactive -->
<span class="inline-flex items-center rounded-full bg-red-50 px-2 py-0.5 text-xs font-medium text-red-700 ring-1 ring-red-600/20">
  Inactive
</span>

<!-- Yellow: pending -->
<span class="inline-flex items-center rounded-full bg-yellow-50 px-2 py-0.5 text-xs font-medium text-yellow-700 ring-1 ring-yellow-600/20">
  Pending
</span>
```

---

## Empty State

```html
<div class="flex flex-col items-center justify-center py-16 text-center">
  <h3 class="text-sm font-semibold text-gray-900">No results</h3>
  <p class="mt-1 text-sm text-gray-500">Try adjusting your filters.</p>
</div>
```

---

## Data Table

```html
<div class="overflow-x-auto rounded-xl border border-gray-200">
  <table class="min-w-full divide-y divide-gray-200 text-sm">
    <thead class="bg-gray-50">
      <tr>
        <th class="px-4 py-3 text-left font-semibold text-gray-600">Name</th>
        <th class="px-4 py-3 text-right font-semibold text-gray-600">Actions</th>
      </tr>
    </thead>
    <tbody class="divide-y divide-gray-100 bg-white">
      @for (row of rows(); track row.id) {
        <tr class="hover:bg-gray-50 transition-colors">
          <td class="px-4 py-3 font-medium text-gray-900">{{ row.name }}</td>
          <td class="px-4 py-3 text-right"><!-- actions --></td>
        </tr>
      }
    </tbody>
  </table>
</div>
```

---

## Alert / Notification Banner

```html
<!-- Error -->
<div class="rounded-lg bg-red-50 p-4 flex gap-3">
  <span class="text-red-500 shrink-0">⚠</span>
  <p class="text-sm text-red-700">{{ errorMessage }}</p>
</div>

<!-- Success -->
<div class="rounded-lg bg-green-50 p-4 flex gap-3">
  <span class="text-green-500 shrink-0">✓</span>
  <p class="text-sm text-green-700">{{ successMessage }}</p>
</div>
```

---

## Skeleton Loader

```html
<div class="animate-pulse space-y-3">
  <div class="h-4 w-3/4 rounded bg-gray-200"></div>
  <div class="h-4 w-1/2 rounded bg-gray-200"></div>
  <div class="h-4 w-5/6 rounded bg-gray-200"></div>
</div>
```

---

## Modal Dialog

```typescript
@Component({
  selector: 'app-modal',
  template: `
    @if (open()) {
      <div
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
        (click)="closeOnBackdrop($event)"
      >
        <div class="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900">{{ title() }}</h2>
            <button (click)="closed.emit()" class="text-gray-400 hover:text-gray-600">✕</button>
          </div>
          <ng-content />
        </div>
      </div>
    }
  `,
})
export class ModalComponent {
  open  = input.required<boolean>();
  title = input('');
  closed = output();

  closeOnBackdrop(e: MouseEvent) {
    if (e.target === e.currentTarget) this.closed.emit();
  }
}
```

---

## Page Header with Action

```html
<div class="flex items-center justify-between mb-6">
  <div>
    <h1 class="text-2xl font-bold text-gray-900">Page Title</h1>
    <p class="mt-1 text-sm text-gray-500">Subtitle or description</p>
  </div>
  <button class="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white hover:bg-indigo-700">
    Action
  </button>
</div>
```

---

## Input with Label and Error

```html
<div class="space-y-1">
  <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
  <input
    id="email"
    type="email"
    class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
    [class.border-red-500]="hasError"
  />
  @if (hasError) {
    <p class="text-xs text-red-500">Enter a valid email address.</p>
  }
</div>
```
