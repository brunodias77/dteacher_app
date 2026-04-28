# Routing Patterns — Angular 20

## Route Parameters via input()

```typescript
// Route: { path: 'products/:id', component: ProductDetail, resolve: { product: productResolver } }
export class ProductDetail {
  // Both route params AND resolved data arrive as inputs (withComponentInputBinding)
  id      = input.required<string>();
  product = input.required<Product>();
}
```

---

## Functional Resolver

```typescript
// core/resolvers/user.resolver.ts
export const userResolver: ResolveFn<User> = route => {
  const userService = inject(UserService);
  return userService.getById(route.paramMap.get('id')!);
};

// Route usage:
// { path: 'users/:id', component: UserDetail, resolve: { user: userResolver } }
// Component — access via input():
// user = input.required<User>();
```

---

## canMatch — Feature Flags

```typescript
export const featureGuard = (flag: string): CanMatchFn => () => {
  const flags = inject(FeatureFlagService);
  return flags.isEnabled(flag);
};

// Routes
{ path: 'beta', canMatch: [featureGuard('beta-ui')], loadComponent: () => ... }
```

---

## Nested + Tab Layout

```typescript
// Routes
{
  path: 'settings',
  component: SettingsLayout,
  children: [
    { path: '', redirectTo: 'profile', pathMatch: 'full' },
    { path: 'profile',   loadComponent: () => import('./profile.component') },
    { path: 'security',  loadComponent: () => import('./security.component') },
    { path: 'billing',   loadComponent: () => import('./billing.component') },
  ],
}

// SettingsLayout template
@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="flex gap-1 border-b border-gray-200 mb-6">
      @for (tab of tabs; track tab.path) {
        <a
          [routerLink]="tab.path"
          routerLinkActive="border-b-2 border-indigo-600 text-indigo-600"
          [routerLinkActiveOptions]="{ exact: true }"
          class="px-4 py-2 text-sm font-medium text-gray-600 hover:text-gray-900"
        >
          {{ tab.label }}
        </a>
      }
    </nav>
    <router-outlet />
  `,
})
export class SettingsLayout {
  tabs = [
    { path: 'profile',  label: 'Profile'  },
    { path: 'security', label: 'Security' },
    { path: 'billing',  label: 'Billing'  },
  ];
}
```

---

## Programmatic Navigation

```typescript
private router = inject(Router);
private route  = inject(ActivatedRoute);

// Absolute
this.router.navigate(['/products', id]);

// Relative to current route
this.router.navigate(['edit'], { relativeTo: this.route });

// With query params
this.router.navigate(['/search'], { queryParams: { q: term, page: 1 } });

// Replace history entry (no back button)
this.router.navigate(['/dashboard'], { replaceUrl: true });
```
