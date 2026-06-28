import { Component, inject } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { HeaderComponent } from './shared/header/header.component';

import { AuthService } from './core/services/auth.service';
import { PreferencesService } from './core/services/preferences.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'fahmak-alena-frontend';

  public router = inject(Router);
  private authService = inject(AuthService);
  private preferencesService = inject(PreferencesService);

  public isAuthRoute = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map((event) => this.checkRoute((event as NavigationEnd).urlAfterRedirects)),
      startWith(this.checkRoute(this.router.url))
    ),
    { initialValue: this.checkRoute(this.router?.url || '/') }
  );

  constructor() {
    this.authService.currentUser$.subscribe((user: any) => {
      if (user) {
        this.preferencesService.loadPreferences();
      }
    });
  }

  private checkRoute(url: string): boolean {
    if (!url) return true;
    const cleanUrl = url.split('?')[0].split('#')[0];
    return cleanUrl === '/' || cleanUrl === '' || cleanUrl.includes('/login') || cleanUrl.includes('/register');
  }
}
