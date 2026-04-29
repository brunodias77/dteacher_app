import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Button } from '../../shared/components/button/button.component';

@Component({
  selector: 'app-dashboard',
  imports: [Button],
  templateUrl: './dashboard.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent {
  private auth = inject(AuthService);

  readonly user = this.auth.user;

  logout() {
    this.auth.logout();
  }
}
