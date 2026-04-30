import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { PreferenceService } from './core/services/preference.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  private auth  = inject(AuthService);
  private prefs = inject(PreferenceService);

  constructor() {
    if (this.auth.isAuthenticated()) {
      this.prefs.load().pipe(takeUntilDestroyed()).subscribe();
    }
  }
}
