# dTeacher — Design System

Referência completa do frontend dTeacher.
Stack: **Angular 20** · **TailwindCSS v4** · **Signals** · **Standalone Components**.

Fontes: `Inter` (corpo) · `JetBrains Mono` (código/labels) · `Fraunces` (display/serif).

---

## 1. Configuração TailwindCSS v4

```css
/* styles.css */
@import "tailwindcss";

@theme {
  --color-bg-0:        #0a0a0b;
  --color-bg-1:        #111113;
  --color-bg-2:        #18181b;
  --color-bg-3:        #1f1f22;

  --color-line:        #26262a;
  --color-line-strong: #34343a;

  --color-text-hi:     #f5f5f4;
  --color-text-mid:    #a1a1aa;
  --color-text-lo:     #71717a;
  --color-text-dim:    #52525b;

  --color-accent:      #ccf381;
  --color-accent-ink:  #0a0a0b;

  --color-danger:      #f87171;
  --color-warn:        #fbbf24;
  --color-ok:          #86efac;
  --color-info:        #93c5fd;

  --font-sans:    'Inter', ui-sans-serif, system-ui, sans-serif;
  --font-mono:    'JetBrains Mono', ui-monospace, monospace;
  --font-display: 'Fraunces', ui-serif, serif;
}

:root {
  --accent-soft:   rgba(204, 243, 129, 0.10);
  --accent-border: rgba(204, 243, 129, 0.35);
}

html, body {
  background: var(--color-bg-0);
  color: var(--color-text-hi);
  font-family: var(--font-sans);
  font-feature-settings: "ss01", "cv11";
  -webkit-font-smoothing: antialiased;
}
```

Com `@theme`, os tokens ficam disponíveis como classes Tailwind: `bg-bg-0`, `text-text-hi`, `border-line`.

---

## 2. Tokens de cor

**Use sempre os tokens — nunca cores hardcoded.**

### Backgrounds

| Token Tailwind   | Hex        | Uso                                |
|------------------|------------|------------------------------------|
| `bg-bg-0`        | `#0a0a0b`  | Fundo raiz (`html`, `body`)        |
| `bg-bg-1`        | `#111113`  | Superfícies primárias              |
| `bg-bg-2`        | `#18181b`  | Superfícies elevadas               |
| `bg-bg-3`        | `#1f1f22`  | Tooltips, dropdowns                |

### Bordas

| Token Tailwind        | Hex        | Uso                      |
|-----------------------|------------|--------------------------|
| `border-line`         | `#26262a`  | Borda padrão             |
| `border-line-strong`  | `#34343a`  | Borda em hover/foco      |

### Texto

| Token Tailwind   | Hex        | Uso                              |
|------------------|------------|----------------------------------|
| `text-text-hi`   | `#f5f5f4`  | Títulos, conteúdo principal      |
| `text-text-mid`  | `#a1a1aa`  | Subtítulos, descrições           |
| `text-text-lo`   | `#71717a`  | Eyebrows, metadados              |
| `text-text-dim`  | `#52525b`  | Placeholders, desabilitados      |

### Accent

| Token Tailwind        | Valor                        | Uso                      |
|-----------------------|------------------------------|--------------------------|
| `bg-accent` / `text-accent` | `#ccf381`            | Ação principal, seleção  |
| `text-accent-ink`     | `#0a0a0b`                    | Texto sobre fundo accent |
| `bg-[var(--accent-soft)]`  | `rgba(204,243,129,0.10)` | Background tinted sutil  |
| `border-[var(--accent-border)]` | `rgba(204,243,129,0.35)` | Borda tinted        |

### Status

| Token Tailwind  | Hex        | Uso         |
|-----------------|------------|-------------|
| `text-danger`   | `#f87171`  | Erros       |
| `text-warn`     | `#fbbf24`  | Avisos      |
| `text-ok`       | `#86efac`  | Sucesso     |
| `text-info`     | `#93c5fd`  | Informação  |

### Paleta de accent (temas)

Aplicado via `PreferenceService` atualizando CSS vars em `:root`:

| Chave         | `--color-accent` | `--accent-soft`              | `--accent-border`            |
|---------------|------------------|------------------------------|------------------------------|
| `lime` (padrão) | `#ccf381`      | `rgba(204,243,129,0.10)`     | `rgba(204,243,129,0.35)`     |
| `amber`       | `#fbbf24`        | `rgba(251,191,36,0.10)`      | `rgba(251,191,36,0.35)`      |
| `cyan`        | `#67e8f9`        | `rgba(103,232,249,0.10)`     | `rgba(103,232,249,0.35)`     |
| `magenta`     | `#f0abfc`        | `rgba(240,171,252,0.12)`     | `rgba(240,171,252,0.40)`     |
| `white`       | `#f5f5f4`        | `rgba(245,245,244,0.08)`     | `rgba(245,245,244,0.30)`     |

---

## 3. Tipografia

| Classe Tailwind  | Família         | Uso                              |
|------------------|-----------------|----------------------------------|
| `font-sans`      | `Inter`         | Corpo de texto, UI geral         |
| `font-mono`      | `JetBrains Mono`| Código, labels, tags, números    |
| `font-display`   | `Fraunces`      | Títulos hero, destaque editorial |

### Escala de tamanhos

| Uso                     | Tailwind              |
|-------------------------|-----------------------|
| Eyebrow / label mono    | `text-[10.5px]`–`text-[11px]` |
| Tag / chip label        | `text-[10.5px]`–`text-[11.5px]` |
| Body / descrição        | `text-[13px]`–`text-sm` |
| Título de card          | `text-[20px]`         |
| Título de seção         | `text-[22px]`         |
| Título de tela          | `text-[32px]`–`text-[38px]` |
| Display hero            | `text-[36px]+`        |

---

## 4. Sistema de densidade

Atributo `data-density` no elemento raiz, controlado por `PreferenceService`:

```css
@layer utilities {
  [data-density="compact"] .den-pad { @apply px-4 py-3; }