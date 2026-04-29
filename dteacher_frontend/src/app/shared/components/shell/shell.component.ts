import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { PreferenceService } from '../../../core/services/preference.service';
import { IconComponent } from '../icon/icon.component';

interface NavTab {
  id: string;
  label: string;
  num: string;
  path: string;
  icon: string;
  disabled?: boolean;
}

@Component({
  selector: 'app-shell',
  templateUrl: './shell.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, IconComponent],
})
export class ShellComponent {
  private auth  = inject(AuthService);
  private prefs = inject(PreferenceService);

  readonly user         = this.auth.user;
  readonly showStreakBar = computed(() => this.prefs.prefs().showStreakBar);
  readonly loggingOut   = signal(false);

  readonly TABS: NavTab[] = [
    { id: 'generator',  label: 'Gerar',     num: '01', path: '/generator',  icon: 'sparkles' },
    { id: 'flashcards', label: 'Cartões',   num: '02', path: '/flashcards', icon: 'layers' },
    { id: 'vocabulary', label: 'Vocab',     num: '03', path: '/vocabulary', icon: 'bookmark' },
    { id: 'textStudy',  label: 'Texto',     num: '04', path: '/text-study', icon: 'text' },
    { id: 'tutor',      label: 'Tutor IA',  num: '05', path: '/tutor',      icon: 'brain' },
    { id: 'fillin',     label: 'Completar', num: '06', path: '/fillin',     icon: 'kbd' },
    { id: 'phonetics',  label: 'Fonética',  num: '07', path: '/phonetics',  icon: 'zap' },
    { id: 'immersion',  label: 'Imersão',   num: '08', path: '/immersion',  icon: 'book' },
  ];

  readonly STREAK_DAYS = [0, 1, 2, 3, 4, 5, 6];

  get userInitial(): string {
    const u = this.user();
    return (u?.name || u?.email || '?').charAt(0).toUpperCase();
  }

  logout(): void {
    if (this.loggingOut()) return;
    this.loggingOut.set(true);
    this.auth.logout().subscribe();
  }
}
