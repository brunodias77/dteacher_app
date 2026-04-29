import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

const ICON_PATHS: Record<string, string> = {
  sparkles: '<path d="M12 3v4M12 17v4M3 12h4M17 12h4M6 6l2.5 2.5M15.5 15.5L18 18M6 18l2.5-2.5M15.5 8.5L18 6"/>',
  layers:   '<path d="M12 3l9 5-9 5-9-5 9-5z"/><path d="M3 13l9 5 9-5"/>',
  bookmark: '<path d="M6 3h12v18l-6-4-6 4V3z"/>',
  text:     '<path d="M4 6h16M4 12h16M4 18h10"/>',
  brain:    '<path d="M9 4a3 3 0 0 0-3 3 3 3 0 0 0-2 5 3 3 0 0 0 1 4 3 3 0 0 0 4 3 3 3 0 0 0 3-1V5a3 3 0 0 0-3-1z"/><path d="M15 4a3 3 0 0 1 3 3 3 3 0 0 1 2 5 3 3 0 0 1-1 4 3 3 0 0 1-4 3 3 3 0 0 1-3-1V5a3 3 0 0 1 3-1z"/>',
  kbd:      '<rect x="2" y="6" width="20" height="12" rx="1"/><path d="M6 10h.01M10 10h.01M14 10h.01M18 10h.01M7 14h10"/>',
  zap:      '<path d="M13 2L3 14h7l-1 8 10-12h-7l1-8z"/>',
  book:     '<path d="M4 4h7a3 3 0 0 1 3 3v13a2 2 0 0 0-2-2H4V4zM20 4h-7a3 3 0 0 0-3 3v13a2 2 0 0 1 2-2h8V4z"/>',
  flame:    '<path d="M12 3c1 4 5 5 5 10a5 5 0 0 1-10 0c0-3 2-4 2-6 0 0 2 1 3-4z"/>',
  logout:   '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/>',
  rotate:   '<path d="M3 12a9 9 0 1 0 3-6.7M3 4v5h5"/>',
  x:        '<path d="M6 6l12 12M18 6L6 18"/>',
  check:    '<path d="M4 12l5 5L20 6"/>',
  arrow:    '<path d="M5 12h14M13 6l6 6-6 6"/>',
  eye:      '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/>',
  plus:     '<path d="M12 5v14M5 12h14"/>',
  volume:   '<path d="M4 9h4l5-4v14l-5-4H4V9z"/><path d="M17 8a5 5 0 0 1 0 8"/>',
  chat:     '<path d="M21 12a8 8 0 1 0-3 6.2L21 21l-.8-3.2A8 8 0 0 0 21 12z"/>',
  filter:   '<path d="M3 6h18M7 12h10M11 18h2"/>',
  bulb:     '<path d="M9 18h6M10 22h4M12 2a7 7 0 0 0-4 12.7c.7.5 1 1.3 1 2.1V18h6v-1.2c0-.8.3-1.6 1-2.1A7 7 0 0 0 12 2z"/>',
  alert:    '<path d="M12 9v4M12 17h.01M10.3 3.9L1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0z"/>',
  send:     '<path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>',
  trophy:   '<path d="M6 9H3V4h18v5h-3M6 9a6 6 0 0 0 12 0M6 9H3M18 9h3M12 15v4M8 19h8"/>',
};

@Component({
  selector: 'app-icon',
  templateUrl: './icon.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { style: 'display: contents' },
})
export class IconComponent {
  name   = input.required<string>();
  size   = input(16);
  stroke = input(1.5);

  private sanitizer = inject(DomSanitizer);

  readonly innerHtml = computed<SafeHtml>(() =>
    this.sanitizer.bypassSecurityTrustHtml(ICON_PATHS[this.name()] ?? ''),
  );
}
