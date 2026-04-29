import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

@Component({
  selector: 'app-chip-add-button',
  templateUrl: './chip-add-button.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { style: 'display: contents' },
})
export class ChipAddButton {
  added   = input(false);
  loading = input(false);

  addClicked = output<void>();

  onClick() {
    if (this.added() || this.loading()) return;
    this.addClicked.emit();
  }
}
