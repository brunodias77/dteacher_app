import {
  ChangeDetectionStrategy,
  Component,
  booleanAttribute,
  computed,
  input,
} from '@angular/core';

type Variant = 'primary' | 'ghost' | 'danger' | 'secondary';
type Size    = 'sm' | 'md' | 'lg';

const VARIANTS: Record<Variant, string> = {
  primary:   'bg-accent text-accent-ink font-bold border border-accent transition-[filter,transform] hover:brightness-110 active:translate-y-px disabled:brightness-100',
  ghost:     'bg-transparent text-text-hi font-semibold border border-line-strong transition-colors hover:border-text-lo',
  danger:    'bg-transparent text-danger font-semibold border border-danger/40 transition-colors hover:bg-danger/10',
  secondary: 'bg-bg-2 text-text-hi font-semibold border border-line-strong transition-colors hover:border-line',
};

const SIZES: Record<Size, string> = {
  sm: 'text-[11px] px-3 py-1.5 gap-1.5',
  md: 'text-[13px] px-[18px] py-3 gap-2',
  lg: 'text-[13px] px-6 py-3.5 gap-2',
};

@Component({
  selector: 'app-button',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { style: 'display: contents' },
  template: `
    <button
      [type]="type()"
      [disabled]="disabled() || loading()"
      [class]="buttonClass()"
    >
      @if (loading()) {
        <svg
          class="w-[14px] h-[14px] animate-spin shrink-0"
          fill="none"
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <circle class="opacity-20" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path stroke="currentColor" stroke-width="2" d="M12 2a10 10 0 0 1 10 10"/>
        </svg>
      }
      <ng-content />
    </button>
  `,
})
export class Button {
  readonly type     = input<'button' | 'submit' | 'reset'>('button');
  readonly variant  = input<Variant>('primary');
  readonly size     = input<Size>('md');
  readonly disabled = input(false, { transform: booleanAttribute });
  readonly loading  = input(false, { transform: booleanAttribute });
  readonly block    = input(false, { transform: booleanAttribute });

  protected readonly buttonClass = computed(() => [
    'inline-flex items-center justify-center',
    'disabled:opacity-45 disabled:cursor-not-allowed',
    this.block() ? 'w-full' : '',
    VARIANTS[this.variant()],
    SIZES[this.size()],
  ].filter(Boolean).join(' '));
}
