import {
  ChangeDetectionStrategy,
  Component,
  booleanAttribute,
  computed,
  forwardRef,
  input,
  signal,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

type InputType = 'text' | 'email' | 'password' | 'number' | 'search' | 'tel';

@Component({
  selector: 'app-input',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { style: 'display: block' },
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => Input),
      multi: true,
    },
  ],
  template: `
    @if (label()) {
      <label
        [for]="id()"
        class="font-mono text-[10.5px] uppercase tracking-[0.1em] text-text-lo block mb-1.5"
      >
        {{ label() }}
        @if (hint()) {
          <span class="text-text-dim normal-case tracking-normal ml-1">{{ hint() }}</span>
        }
      </label>
    }

    <div class="relative">
      <input
        [id]="id()"
        [type]="effectiveType()"
        [placeholder]="placeholder()"
        [value]="_value()"
        [disabled]="_disabled()"
        [attr.autocomplete]="autocomplete()"
        [attr.aria-invalid]="!!error()"
        [attr.aria-describedby]="error() ? id() + '-error' : null"
        [class]="inputClass()"
        (input)="onInput($event)"
        (blur)="onBlur()"
      />

      @if (showPasswordToggle() && type() === 'password') {
        <button
          type="button"
          (click)="togglePassword()"
          class="absolute inset-y-0 right-0 flex items-center px-3 text-text-dim hover:text-text-mid transition-colors"
          [attr.aria-label]="_showPassword() ? 'Ocultar senha' : 'Mostrar senha'"
          tabindex="-1"
        >
          @if (_showPassword()) {
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
              <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
              <line x1="1" y1="1" x2="23" y2="23"/>
            </svg>
          } @else {
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
          }
        </button>
      }
    </div>

    @if (error()) {
      <p [id]="id() + '-error'" class="mt-1.5 text-[12px] text-danger" role="alert">
        {{ error() }}
      </p>
    }
  `,
})
export class Input implements ControlValueAccessor {
  readonly id                 = input('');
  readonly label              = input('');
  readonly hint               = input('');
  readonly type               = input<InputType>('text');
  readonly placeholder        = input('');
  readonly autocomplete       = input('off');
  readonly error              = input<string | null>(null);
  readonly showPasswordToggle = input(false, { transform: booleanAttribute });

  protected readonly _value        = signal('');
  protected readonly _disabled     = signal(false);
  protected readonly _showPassword = signal(false);

  protected readonly effectiveType = computed(() =>
    this.type() === 'password' && this._showPassword() ? 'text' : this.type(),
  );

  protected readonly inputClass = computed(() => [
    'w-full bg-bg-0 border text-text-hi px-4 py-3.5 outline-none transition-colors',
    'disabled:opacity-45 disabled:cursor-not-allowed',
    'placeholder:text-text-dim',
    this.showPasswordToggle() && this.type() === 'password' ? 'pr-11' : '',
    this.error()
      ? 'border-danger focus:border-danger'
      : 'border-line-strong focus:border-accent',
  ].filter(Boolean).join(' '));

  private _onChange:  (v: string) => void = () => {};
  private _onTouched: () => void          = () => {};

  writeValue(value: string): void              { this._value.set(value ?? ''); }
  registerOnChange(fn: (v: string) => void)    { this._onChange = fn; }
  registerOnTouched(fn: () => void)            { this._onTouched = fn; }
  setDisabledState(disabled: boolean): void    { this._disabled.set(disabled); }

  protected onInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this._value.set(value);
    this._onChange(value);
  }

  protected onBlur(): void          { this._onTouched(); }
  protected togglePassword(): void  { this._showPassword.update(v => !v); }
}
