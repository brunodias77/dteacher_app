# dTeacher — Design System

Referência completa do sistema visual do frontend dTeacher.
Stack: **Angular 20** + **TailwindCSS v4** com variáveis CSS globais.

Fontes: `Inter` (corpo), `JetBrains Mono` (código/labels), `Fraunces` (display/serif).

---

## 1. Configuração TailwindCSS v4

```css
/* styles.css */
@import "tailwindcss";

/* Registra os tokens como variáveis CSS acessíveis globalmente */
@theme {
  --color-bg-0:         #0a0a0b;
  --color-bg-1:         #111113;
  --color-bg-2:         #18181b;
  --color-bg-3:         #1f1f22;

  --color-line:         #26262a;
  --color-line-strong:  #34343a;

  --color-text-hi:      #f5f5f4;
  --color-text-mid:     #a1a1aa;
  --color-text-lo:      #71717a;
  --color-text-dim:     #52525b;

  --color-accent:       #ccf381;
  --color-accent-ink:   #0a0a0b;

  --color-danger:       #f87171;
  --color-warn:         #fbbf24;
  --color-ok:           #86efac;
  --color-info:         #93c5fd;

  --font-sans:    'Inter', ui-sans-serif, system-ui, sans-serif;
  --font-mono:    'JetBrains Mono', ui-monospace, monospace;
  --font-display: 'Fraunces', ui-serif, serif;
}

/* Variáveis derivadas (não registradas no @theme) */
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

Com `@theme`, os tokens ficam disponíveis como classes utilitárias Tailwind:

```html
<!-- bg-bg-0, text-text-hi, border-line, etc. -->
<div class="bg-bg-1 text-text-hi border border-line">...</div>
```

---

## 2. Tokens de cor

**Use sempre os tokens — nunca cores hardcoded.**

### Backgrounds

| Token Tailwind | CSS var | Hex | Uso |
|---|---|---|---|
| `bg-bg-0` | `--color-bg-0` | `#0a0a0b` | Fundo raiz (`html`, `body`) |
| `bg-bg-1` | `--color-bg-1` | `#111113` | Superfícies primárias |
| `bg-bg-2` | `--color-bg-2` | `#18181b` | Superfícies elevadas |
| `bg-bg-3` | `--color-bg-3` | `#1f1f22` | Tooltips, dropdowns |

### Bordas

| Token Tailwind | CSS var | Hex | Uso |
|---|---|---|---|
| `border-line` | `--color-line` | `#26262a` | Borda padrão |
| `border-line-strong` | `--color-line-strong` | `#34343a` | Borda em hover/foco |

### Texto

| Token Tailwind | CSS var | Hex | Uso |
|---|---|---|---|
| `text-text-hi` | `--color-text-hi` | `#f5f5f4` | Títulos, conteúdo principal |
| `text-text-mid` | `--color-text-mid` | `#a1a1aa` | Subtítulos, descrições |
| `text-text-lo` | `--color-text-lo` | `#71717a` | Eyebrows, metadados |
| `text-text-dim` | `--color-text-dim` | `#52525b` | Placeholders, desabilitados |

### Accent

| Token Tailwind | CSS var | Valor | Uso |
|---|---|---|---|
| `bg-accent` / `text-accent` | `--color-accent` | `#ccf381` | Ação principal, seleção |
| `text-accent-ink` | `--color-accent-ink` | `#0a0a0b` | Texto sobre fundo accent |
| — | `--accent-soft` | `rgba(204,243,129,0.10)` | Background tinted sutil |
| — | `--accent-border` | `rgba(204,243,129,0.35)` | Borda tinted |

Para `accent-soft` e `accent-border` use valor arbitrário: `bg-[var(--accent-soft)]`.

### Status

| Token Tailwind | Hex | Uso |
|---|---|---|
| `text-danger` / `border-danger` | `#f87171` | Erros |
| `text-warn` | `#fbbf24` | Avisos |
| `text-ok` | `#86efac` | Sucesso |
| `text-info` | `#93c5fd` | Informação |

### Paleta de accent (temas alternativos)

Aplicado dinamicamente via JS atualizando as CSS vars em `:root`:

| Chave | `color` | `soft` | `border` |
|---|---|---|---|
| `lime` _(padrão)_ | `#ccf381` | `rgba(204,243,129,0.10)` | `rgba(204,243,129,0.35)` |
| `amber` | `#fbbf24` | `rgba(251,191,36,0.10)` | `rgba(251,191,36,0.35)` |
| `cyan` | `#67e8f9` | `rgba(103,232,249,0.10)` | `rgba(103,232,249,0.35)` |
| `magenta` | `#f0abfc` | `rgba(240,171,252,0.12)` | `rgba(240,171,252,0.40)` |
| `white` | `#f5f5f4` | `rgba(245,245,244,0.08)` | `rgba(245,245,244,0.30)` |

---

## 3. Tipografia

### Famílias

| Classe Tailwind | Família | Font features | Uso |
|---|---|---|---|
| `font-sans` | `Inter` | `ss01`, `cv11` | Corpo de texto, UI geral |
| `font-mono` | `JetBrains Mono` | `zero`, `ss02` | Código, labels, tags, números |
| `font-display` | `Fraunces` (serif, italic) | `optical-sizing: auto` | Títulos hero, destaque editorial |

### Escala de tamanhos

| Uso | Tailwind | px equivalente |
|---|---|---|
| Eyebrow / label mono | `text-[10.5px]` – `text-[11px]` | 10.5–11px |
| Tag / chip label | `text-[10.5px]` – `text-[11.5px]` | 10.5–11.5px |
| Body / descrição | `text-[13px]` – `text-sm` | 13–14px |
| Título de card | `text-[20px]` | 20px |
| Título de seção (`SectionHead`) | `text-[22px]` | 22px |
| Título de tela | `text-[32px]` – `text-[38px]` | 32–38px |
| Display hero | `text-[36px]+` | 36px+ |

### Utilitários de texto

| Classe | Efeito |
|---|---|
| `tabular-nums` | Números alinhados (`font-variant-numeric: tabular-nums`) |
| `tracking-tight` | `letter-spacing: -0.025em` |
| `italic font-display` | Destaque editorial em Fraunces |
| `uppercase tracking-[0.12em]` | Eyebrow / label caps (`.caps-sm`) |

---

## 4. Sistema de densidade

Atributo `data-density` no elemento raiz. Use a classe utilitária customizada `den-pad`:

```css
/* styles.css — definida via @layer utilities */
@layer utilities {
  [data-density="compact"] .den-pad { @apply px-4 py-3; }
  [data-density="cozy"]    .den-pad { @apply px-6 py-5; }
  [data-density="roomy"]   .den-pad { @apply px-8 py-7; }
}
```

---

## 5. Componentes base (`@layer components`)

Defina os componentes reutilizáveis em `styles.css` para evitar repetição de classes:

```css
@layer components {

  /* Superfícies */
  .surface   { @apply bg-bg-1 border border-line; }
  .surface-2 { @apply bg-bg-2 border border-line; }

  /* Input */
  .input {
    @apply w-full bg-bg-0 border border-line-strong text-text-hi px-4 py-3.5
           transition-colors outline-none focus:border-accent;
  }

  /* Botão principal */
  .btn-primary {
    @apply inline-flex items-center gap-2 bg-accent text-accent-ink font-bold text-[13px]
           px-[18px] py-3 border border-accent transition-[filter,transform]
           hover:brightness-110 active:translate-y-px
           disabled:opacity-45 disabled:cursor-not-allowed disabled:brightness-100;
  }

  /* Botão secundário */
  .btn-ghost {
    @apply inline-flex items-center gap-2 bg-transparent text-text-hi font-semibold text-[13px]
           px-4 py-[11px] border border-line-strong transition-colors
           hover:border-text-lo;
  }

  /* Chip / toggle */
  .chip-btn {
    @apply inline-flex items-center gap-2 font-mono text-[11.5px] uppercase tracking-[0.12em]
           px-3 py-[9px] border border-line bg-bg-1 text-text-hi
           transition-[border-color,color,background];
  }
  .chip-btn:hover       { @apply border-line-strong; }
  .chip-btn.active      { @apply border-accent bg-accent text-accent-ink; }
  .chip-btn.ghost       { @apply bg-transparent; }

  /* Tag / label estático */
  .tag {
    @apply inline-flex items-center gap-1.5 font-mono text-[10.5px] uppercase tracking-[0.1em]
           px-[7px] py-[3px] border border-line text-text-mid bg-bg-1;
  }
  .tag.accent { @apply border-[var(--accent-border)] text-accent bg-[var(--accent-soft)]; }

  /* Palavra clicável (criar flashcard) */
  .w-hover {
    @apply cursor-pointer px-0.5 -mx-0.5 border-b border-dashed border-transparent
           transition-all duration-[120ms]
           hover:bg-accent hover:text-accent-ink hover:border-accent-ink;
  }

  /* Linha horizontal interna */
  .hl-t { box-shadow: inset 0  1px 0 var(--color-line); }
  .hl-b { box-shadow: inset 0 -1px 0 var(--color-line); }
}
```

---

## 6. Animações

```css
/* styles.css */
@keyframes fadeSlide {
  from { opacity: 0; transform: translateY(6px); }
  to   { opacity: 1; transform: none; }
}

@keyframes pulseDot {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.3; }
}

@keyframes wavebar {
  0%, 100% { transform: scaleY(0.6); }
  50%       { transform: scaleY(1.3); }
}

@layer utilities {
  .animate-fade-slide { animation: fadeSlide 0.28s cubic-bezier(.2,.8,.2,1); }
  .pulse-dot          { animation: pulseDot 1.4s ease-in-out infinite; }
}
```

| Classe | Uso |
|---|---|
| `animate-fade-slide` | Entrada de telas e seções (sempre na div raiz da tela) |
| `pulse-dot` | Indicador "ao vivo" / loading sutil |
| `animate-spin` | Spinner (nativo Tailwind) |
| `.wave b` (keyframe `wavebar`) | Barras de áudio animadas |

---

## 7. Botões

### `.btn-primary` — ação principal

```html
<button class="btn-primary">
  <app-icon name="sparkles" />
  Gerar frases
</button>

<!-- Com loading -->
<button class="btn-primary" [disabled]="loading">
  @if (loading) { <app-spinner /> A gerar }
  @else { <app-icon name="sparkles" /> Gerar frases }
</button>
```

### `.btn-ghost` — ação secundária

```html
<button class="btn-ghost">
  <app-icon name="volume" />
  Ouvir pronúncia
</button>
```

### `.chip-btn` — toggle / ação de lista

```html
<!-- Estado normal -->
<button class="chip-btn">
  <app-icon name="plus" [size]="12" /> Adicionar
</button>

<!-- Estado ativo (após ação) -->
<button class="chip-btn active" disabled>
  <app-icon name="check" [size]="12" /> Adicionado
</button>

<!-- Segmented control -->
<div class="inline-flex border border-line">
  <button class="chip-btn ghost border-0 border-r border-line active">
    <app-icon name="eye" [size]="12" /> Clássico
  </button>
  <button class="chip-btn ghost border-0">
    <app-icon name="kbd" [size]="12" /> Escrita
  </button>
</div>
```

---

## 8. Tags e rótulos

### `.tag`

```html
<!-- Neutro -->
<span class="tag">42 cartões</span>

<!-- Accent (destaque) -->
<span class="tag accent">12 a rever</span>

<!-- Com ícone -->
<span class="tag"><app-icon name="flame" [size]="11" /> 7 dias</span>
```

### `<kbd>`

```css
/* styles.css */
kbd {
  @apply font-mono text-[10px] px-[5px] py-[2px]
         border border-line-strong text-text-mid bg-bg-2;
}
```

```html
<kbd>⌘</kbd><kbd>K</kbd>
```

---

## 9. Superfícies e input

### `.surface` / `.surface-2`

```html
<!-- Card primário -->
<div class="surface den-pad">...</div>

<!-- Card elevado (ex: flashcard) -->
<div class="surface-2 relative overflow-hidden">
  <!-- barra de progresso no topo -->
  <div class="absolute top-0 left-0 h-0.5 bg-accent transition-all"
       [style.width]="progress + '%'"></div>
  ...
</div>
```

### `.input`

```html
<input class="input font-mono text-[14px]" placeholder="travel, airport..." />

<!-- Com hint de teclado à direita -->
<div class="relative flex-1">
  <input class="input pr-28" placeholder="travel, airport..." />
  <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
    <kbd>⏎</kbd>
  </div>
</div>
```

---

## 10. Padrões de layout

### Tela padrão

```html
<div class="animate-fade-slide space-y-10">

  <!-- Cabeçalho de tela -->
  <div>
    <div class="font-mono text-[11px] uppercase tracking-[0.12em] text-text-lo mb-2">
      01 · Nome da Tela
    </div>
    <h1 class="text-[38px] font-semibold leading-[1.05] tracking-tight">
      Título com
      <span class="font-display italic text-accent">destaque</span>
    </h1>
    <p class="text-text-mid text-[14px] mt-3 max-w-[52ch]">
      Descrição da tela.
    </p>
  </div>

  <!-- Conteúdo -->
</div>
```

### Card de item de lista

```html
<div class="surface den-pad group hover:border-line-strong transition-colors">
  <div class="flex items-start gap-5">

    <!-- Número indexador -->
    <div class="font-mono text-[11px] text-text-dim pt-1 tabular-nums">
      {{ (index + 1) | number:'2.0' }}
    </div>

    <!-- Conteúdo -->
    <div class="flex-1 min-w-0 space-y-2">
      <div class="text-[20px] leading-snug text-text-hi font-medium">
        {{ item.english }}
      </div>
      <div class="text-[13px] text-text-mid blur-sm hover:blur-none transition-all duration-300 cursor-help"
           title="Ver tradução">
        {{ item.portuguese }}
      </div>
    </div>

    <!-- Ações -->
    <div class="flex items-center gap-2 flex-shrink-0">
      <app-audio-pill ... />
      <button class="chip-btn" [class.active]="added">
        @if (added) { <app-icon name="check" [size]="12" /> Adicionado }
        @else { <app-icon name="plus" [size]="12" /> Adicionar }
      </button>
    </div>

  </div>
</div>
```

### Cabeçalho de seção (`SectionHead`)

```html
<div class="flex items-end justify-between gap-4 mb-5">
  <div>
    <div class="font-mono text-[11px] uppercase tracking-[0.12em] text-text-lo mb-2">
      Eyebrow
    </div>
    <h2 class="text-[22px] font-semibold text-text-hi tracking-tight">Título</h2>
  </div>
  <div><!-- slot direito: Tags, botões --></div>
</div>
```

### Barra de progresso segmentada

```html
<div class="flex gap-0.5 h-1">
  @for (seg of segments; track $index) {
    <span class="flex-1 transition-colors"
          [class]="$index < filled ? 'bg-accent' : 'bg-line'">
    </span>
  }
</div>
```

### Brand mark (logo)

```html
<div class="w-7 h-7 grid grid-cols-2 grid-rows-2 gap-0.5 bg-bg-0">
  <i class="block bg-accent"></i>
  <i class="block bg-text-hi"></i>
  <i class="block bg-text-hi"></i>
  <i class="block bg-text-hi"></i>
</div>
```

### Estado vazio (`Empty`)

```html
<div class="surface p-10 flex flex-col items-center text-center">
  <pre class="font-mono text-[11px] leading-[1.2] text-text-dim mb-6">·  ·  ·  ·  ·  ·  ·
·  ·        ·  ·  ·
·  ·  ( )   ·  ·  ·
·  ·        ·  ·  ·
·  ·  ·  ·  ·  ·  ·</pre>

  <div class="text-text-lo mb-4"><!-- ícone --></div>

  <div class="font-display text-[26px] text-text-hi leading-tight mb-2">
    Título do estado vazio
  </div>
  <p class="text-text-mid text-[13px] max-w-sm">
    Descrição auxiliar.
  </p>
  <div class="mt-6">
    <button class="btn-primary">CTA</button>
  </div>
</div>
```

---

## 11. Componente de áudio (`AudioPill`)

```html
<div class="inline-flex items-stretch border border-line-strong bg-bg-1">

  <!-- Play / pause -->
  <button (click)="onPlay()" [disabled]="loading"
          class="flex items-center gap-2 px-3 py-2 border-r border-line
                 hover:bg-bg-2 disabled:opacity-50">
    @if (loading) { <app-spinner [size]="14" /> }
    @else if (playing) { <app-wave /> }
    @else { <app-icon name="play" [size]="14" /> }
  </button>

  <!-- Reduzir velocidade -->
  <button (click)="onSpeed(-1)"
          class="px-2 text-text-lo hover:text-text-hi hover:bg-bg-2 border-r border-line">
    <app-icon name="minus" [size]="12" />
  </button>

  <!-- Display de velocidade -->
  <span class="px-2 py-2 font-mono text-[11px] text-text-mid tabular-nums w-[42px] text-center select-none">
    {{ speed }}×
  </span>

  <!-- Aumentar velocidade -->
  <button (click)="onSpeed(1)"
          class="px-2 text-text-lo hover:text-text-hi hover:bg-bg-2 border-l border-line">
    <app-icon name="plus" [size]="12" />
  </button>

</div>
```

---

## 12. Padrões de feedback

### Erro inline

```html
<div class="mt-3 text-[13px] text-danger flex items-center gap-2">
  <app-icon name="alert" [size]="14" /> Mensagem de erro
</div>
```

### Resposta correta / incorreta

```html
<!-- Correto -->
<div class="border border-[var(--accent-border)] bg-[var(--accent-soft)] p-4">
  <div class="font-mono text-[10px] uppercase tracking-[0.1em] text-text-lo mb-1">
    A sua resposta
  </div>
  <div class="font-medium text-accent">{{ resposta }}</div>
</div>

<!-- Incorreto -->
<div class="border border-[rgba(248,113,113,0.3)] bg-[rgba(248,113,113,0.05)] p-4">
  <div class="font-mono text-[10px] uppercase tracking-[0.1em] text-text-lo mb-1">
    A sua resposta
  </div>
  <div class="font-medium text-danger">{{ resposta }}</div>
</div>
```

### Tradução oculta (blur reveal)

```html
<span class="blur-sm hover:blur-none transition-all duration-300 cursor-help"
      title="Ver tradução">
  {{ traducao }}
</span>
```

---

## 13. Tabs de navegação

| `id` | Label | Ícone | Número |
|---|---|---|---|
| `generator` | Gerar | `sparkles` | `01` |
| `flashcards` | Cartões | `layers` | `02` |
| `vocabulary` | Vocab | `bookmark` | `03` |
| `textStudy` | Texto | `text` | `04` |
| `tutor` | Tutor IA | `brain` | `05` |
| `fillin` | Completar | `kbd` | `06` |
| `phonetics` | Fonética | `zap` | `07` |
| `immersion` | Imersão | `book` | `08` |

---

## 14. Ícones disponíveis (`<app-icon name="" [size]="16" [stroke]="1.5" />`)

SVG inline — sem biblioteca externa.

| Nome | Uso |
|---|---|
| `sparkles` | Gerador / IA |
| `layers` | Flashcards / deck |
| `bookmark` | Vocabulário |
| `text` | Texto |
| `brain` | Tutor IA |
| `kbd` | Fill-in / teclado |
| `zap` | Fonética |
| `book` | Imersão |
| `play` / `pause` | Áudio |
| `volume` | Pronúncia |
| `plus` / `minus` | Adicionar / remover |
| `check` / `check2` | Confirmação |
| `x` | Fechar / remover |
| `arrow` | Navegação |
| `rotate` | Reiniciar |
| `send` | Enviar |
| `loader` | Loading |
| `flame` | Streak |
| `settings` | Configurações |
| `bulb` | Dica |
| `eye` | Visualizar |
| `alert` | Alerta |
| `home` | Início |
| `menu` | Menu |
| `command` | Atalho |
| `trophy` | Conquista |
| `circle` | Fallback |

---

## 15. Checklist de implementação

Ao criar uma nova tela ou componente:

- [ ] Div raiz usa `animate-fade-slide`
- [ ] Eyebrow segue `font-mono text-[11px] uppercase tracking-[0.12em] text-text-lo`
- [ ] Usa tokens Tailwind (`text-text-hi`, `bg-bg-1`, `border-line`) — sem cores hardcoded
- [ ] `accent-soft` e `accent-border` usados via `bg-[var(--accent-soft)]` (valor arbitrário)
- [ ] Cards usam `surface den-pad` com `hover:border-line-strong transition-colors`
- [ ] Estados vazios usam o padrão `Empty` com ASCII art + `font-display`
- [ ] Textos longos usam `[style.textWrap]="'pretty'"` ou `'balance'`
- [ ] Números de índice usam `tabular-nums` e padding com zero (`padStart(2, "0")`)
- [ ] Botão com loading mostra `<app-spinner />` e recebe `[disabled]="loading"`
- [ ] Reprodução de áudio usa `<app-audio-pill />`
