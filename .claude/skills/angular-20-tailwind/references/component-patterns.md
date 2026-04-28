# Component Patterns — Angular 20

## Full Standalone Component Example (Button)

```typescript
// shared/components/button/button.component.ts
import {
  Component,
  ChangeDetectionStrategy,
  input,
  output,
  computed,
} from '@angular/core';
import { booleanAttribute } from '@angular/core';

type Variant = 'primary' | 'secondary' | 'danger' | 'ghost';
type Size    = 'sm' | 'md' | 'lg';

@Component({
  selector: 'app-button',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    '[class]': 'hostClass()',
    '[attr.aria-disabled]': 'disabled()',
    '(click)': '_onClick($event)',
    '(keydown.enter)': '_onClick($event)',
    '(keydown.space)': '$event.preventDefault(); _onClick($event)',
    'role': 'button',
    '[attr.tabindex]': 'disabled() ? -1 : 0',
  },
  template: `
    @if (loading()) {
      <span class="animate-spin mr-2 h-4 w-4 border-2 border-white border-t-transparent rounded-full inline-block"></span>
    }
    <ng-content />
  `,
})
export class Button {
  variant  = input<Variant>('primary');
  size     = input<Size>('md');
  disabled = input(false, { transform: booleanAttribute });
  loading  = input(false, { transform: booleanAttribute });

  clicked  = output<MouseEvent | KeyboardEvent>();

  private readonly BASE = 'inline-flex items-center justify-center font-semibold rounded-lg transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 cursor-pointer';

  private readonly VARIANTS: Record<Variant, string> = {
    primary:   'bg-indigo-600 text-white hover:bg-indigo-700 focus-visible:ring-indigo-500',
    secondary: 'bg-gray-100 text-gray-900 hover:bg-gray-200 focus-visible:ring-gray-400',
    danger:    'bg-red-600 text-white hover:bg-red-700 focus-visible:ring-red-500',
    ghost:     'bg-transparent text-gray-700 hover:bg-gray-100 focus-visible:ring-gray-400',
  };

  private readonly SIZES: Record<Size, string> = {
    sm: 'text-sm px-3 py-1.5 gap-1.5',
    md: 'text-sm px-4 py-2 gap-2',
    lg: 'text-base px-6 py-3 gap-2',
  };

  hostClass = computed(() => [
    this.BASE,
    this.VARIANTS[this.variant()],
    this.SIZES[this.size()],
    this.disabled() ? 'opacity-50 pointer-events-none' : '',
  ].join(' '));

  _onClick(e: MouseEvent | KeyboardEvent) {
    if (!this.disabled() && !this.loading()) {
      this.clicked.emit(e);
    }
  }
}
```

---

## model() — Two-Way Binding

```typescript
// Input field with [(value)]
@Component({
  selector: 'app-input',
  host: { '(input)': 'value.set($any($event.target).value)' },
  template: `<input [value]="value()" class="..." />`,
})
export class AppInput {
  value       = model('');
  placeholder = input('');
  disabled    = input(false, { transform: booleanAttribute });
}

// Usage
<app-input [(value)]="username" placeholder="Username" />
```

---

## @defer — Lazy UI Sections

```typescript
@Component({
  template: `
    @defer (on viewport; prefetch on idle) {
      <app-heavy-chart [data]="data()" />
    } @placeholder {
      <div class="h-64 rounded-xl bg-gray-100 animate-pulse"></div>
    } @error {
      <p class="text-red-500 text-sm">Chart failed to load.</p>
    }
  `,
})
export class Dashboard { data = input.required<ChartData>(); }
```

---

## viewChild / viewChildren

```typescript
@Component({
  template: `<div #wrap><app-card *... /></div>`,
})
export class List {
  wrap  = viewChild.required<ElementRef<HTMLDivElement>>('wrap');
  cards = viewChildren(CardComponent);

  constructor() {
    afterNextRender(() => {
      console.log('wrapper width:', this.wrap().nativeElement.offsetWidth);
    });
  }
}
```

---

## contentChildren — Compound Components

```typescript
@Component({
  selector: 'app-accordion',
  template: `<ng-content />`,
})
export class Accordion {
  panels = contentChildren(AccordionPanel);
  active = signal<AccordionPanel | null>(null);

  open(panel: AccordionPanel) {
    this.active.set(panel);
  }
}

@Component({
  selector: 'app-accordion-panel',
  host: { '[class.open]': 'isOpen()' },
  template: `
    <button (click)="accordion.open(this)" class="w-full text-left px-4 py-3 font-medium">
      {{ label() }}
    </button>
    @if (isOpen()) {
      <div class="px-4 pb-4"><ng-content /></div>
    }
  `,
})
export class AccordionPanel {
  label     = input.required<string>();
  accordion = inject(Accordion);
  isOpen    = computed(() => this.accordion.active() === this);
}
```

---

## Standalone Pipe

```typescript
@Pipe({ name: 'timeAgo' })
export class TimeAgoPipe implements PipeTransform {
  transform(date: string | Date): string {
    const diff = Date.now() - new Date(date).getTime();
    const mins = Math.floor(diff / 60_000);
    if (mins < 1)  return 'just now';
    if (mins < 60) return `${mins}m ago`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24)  return `${hrs}h ago`;
    return `${Math.floor(hrs / 24)}d ago`;
  }
}
```

---

## Standalone Directive

```typescript
@Directive({
  selector: '[appAutoFocus]',
})
export class AutoFocusDirective {
  private el = inject(ElementRef<HTMLElement>);

  constructor() {
    afterNextRender(() => this.el.nativeElement.focus());
  }
}
```
