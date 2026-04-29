import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/generator', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(m => m.RegisterComponent),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/components/shell/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
      },
      {
        path: 'generator',
        loadComponent: () =>
          import('./features/generator/generator.component').then(m => m.GeneratorComponent),
      },
      {
        path: 'flashcards',
        loadComponent: () =>
          import('./features/flashcards/flashcards.component').then(m => m.FlashcardsComponent),
      },
      {
        path: 'vocabulary',
        loadComponent: () =>
          import('./features/vocabulary/vocabulary.component').then(m => m.VocabularyComponent),
      },
      {
        path: 'text-study',
        loadComponent: () =>
          import('./features/text-study/text-study.component').then(m => m.TextStudyComponent),
      },
      {
        path: 'tutor',
        loadComponent: () =>
          import('./features/tutor/tutor.component').then(m => m.TutorComponent),
      },
      {
        path: 'fillin',
        loadComponent: () =>
          import('./features/fillin/fillin.component').then(m => m.FillinComponent),
      },
      {
        path: 'phonetics',
        loadComponent: () =>
          import('./features/phonetics/phonetics.component').then(m => m.PhoneticsComponent),
      },
      {
        path: 'immersion',
        loadComponent: () =>
          import('./features/immersion/immersion.component').then(m => m.ImmersionComponent),
      },
    ],
  },
];
