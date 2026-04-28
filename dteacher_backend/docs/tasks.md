# Tasks de Implementação — btree_english

---

## P0 — Fundação do Produto

### TASK-001 - UC-001 - Cadastrar usuário

**Feature:** Permitir que um visitante crie uma conta com e-mail e senha.

**Backend:** Criar `POST /api/auth/register`, entidades `User`, `UserPreference` e `StudyStreak`.

**Frontend:** Tela de cadastro com campos nome, e-mail, senha e confirmação de senha; exibir erros de validação inline; redirecionar para dashboard após sucesso.

**Algoritmo:**

1. Receber `name`, `email`, `password` e `passwordConfirmation`.
2. Validar campos obrigatórios, formato do e-mail e força mínima da senha (≥ 8 chars, 1 número).
3. Verificar unicidade do e-mail na tabela `users`; retornar 409 se já existir.
4. Gerar hash bcrypt da senha.
5. Inserir registro em `users`.
6. Inserir preferências padrão em `user_preferences` (accent=lime, density=cozy, etc.).
7. Inserir streak zerado em `study_streaks`.
8. Retornar `201` com payload `{id, email, name}` e JWT de acesso.

**Aceite:** Usuário novo consegue se cadastrar; preferências e streak iniciais são criados automaticamente; e-mail duplicado retorna erro claro.

---

### TASK-002 - UC-002 - Autenticar usuário

**Feature:** Permitir login com e-mail e senha, emitindo JWT.

**Backend:** Criar `POST /api/auth/login`; validar credenciais; emitir JWT com `sub = userId` e expiração configurável.

**Frontend:** Tela de login com e-mail e senha; armazenar token em `localStorage`; interceptor HTTP que injeta `Authorization: Bearer <token>` em todas as requisições autenticadas; redirecionar para dashboard.

**Algoritmo:**

1. Receber `email` e `password`.
2. Buscar usuário pelo e-mail; retornar 401 genérico se não encontrado.
3. Comparar senha com hash bcrypt; retornar 401 genérico se inválida.
4. Gerar JWT assinado com `userId`, `email` e TTL.
5. Retornar `200` com `{accessToken, expiresIn, user: {id, name, email}}`.

**Aceite:** Login com credenciais válidas retorna token; credenciais inválidas retornam 401; token é enviado corretamente pelo interceptor Angular nas chamadas subsequentes.

---

### TASK-003 - UC-003 - Fazer logout

**Feature:** Encerrar a sessão do usuário no cliente.

**Backend:** Endpoint `POST /api/auth/logout` opcional (para invalidação de token em blocklist, se implementado); mínimo aceitável é invalidação apenas no cliente.

**Frontend:** Botão de logout no sidebar; limpar token e dados do usuário do estado da aplicação e do `localStorage`; redirecionar para tela de login.

**Algoritmo:**

1. Usuário aciona logout.
2. Remover token do `localStorage`.
3. Limpar store/signal de usuário autenticado.
4. Navegar para `/login`.

**Aceite:** Após logout, qualquer rota protegida redireciona para login; token não é mais enviado nas requisições.

---

### TASK-004 - UC-004 - Salvar preferências de interface

**Feature:** Persistir accent, density, uppercase level, streak bar e aba ativa por usuário.

**Backend:** Criar `PUT /api/me/preferences`; atualizar registro em `user_preferences`; criar `GET /api/me/preferences` para hidratar o cliente no login.

**Frontend:** Painel "Personalização" (TweaksPanel) atualiza as preferências via debounce (500 ms) ao mudar qualquer opção; ao iniciar a sessão, carregar preferências do backend e aplicar no documento.

**Algoritmo:**

1. No login, chamar `GET /api/me/preferences` e aplicar valores ao DOM (`--accent`, `data-density`, `data-caps`).
2. A cada mudança de preferência no painel, enfileirar `PUT /api/me/preferences` com debounce de 500 ms.
3. Backend valida os valores permitidos (enum check) e atualiza `user_preferences`.

**Aceite:** Preferências escolhidas persistem entre sessões; valores inválidos são rejeitados com 422.

---

## P1 — Flashcards / SRS

### TASK-005 - UC-005 - Adicionar flashcard ao deck

**Feature:** Salvar um par inglês/português como flashcard do usuário autenticado.

**Backend:** Criar `POST /api/flashcards`; aceitar `{english, portuguese, source}`; inserir em `flashcards` com `next_review = now`, `repetitions = 0`, `ease_factor = 2.50`.

**Frontend:** Botão "Adicionar" / "Adicionar todas" nos módulos Generator, Text Study e Vocabulary dispara a criação; feedback visual imediato (botão muda para "Adicionado").

**Algoritmo:**

1. Receber `{english, portuguese, source}`.
2. Validar que `english` e `portuguese` não estão vazios.
3. Inserir flashcard com valores SRS padrão.
4. Retornar `201` com o flashcard criado.

**Aceite:** Flashcard aparece no deck imediatamente após adição; `source` é registrada corretamente.

---

### TASK-006 - UC-006 - Listar flashcards do deck

**Feature:** Retornar todos os flashcards do usuário com contagem de pendentes.

**Backend:** Criar `GET /api/flashcards`; retornar lista com `{cards[], totalDue, total}`; `totalDue` = cards com `next_review ≤ now`.

**Frontend:** FlashcardsScreen exibe contagem de pendentes na aba e no cabeçalho; se deck vazio, exibe estado Empty com CTA para Generator.

**Algoritmo:**

1. Buscar todos os flashcards do usuário autenticado.
2. Calcular `totalDue` filtrando `next_review ≤ now`.
3. Ordenar por `next_review ASC` (cartões mais urgentes primeiro).
4. Retornar payload consolidado.

**Aceite:** Lista retorna corretamente; badge da aba Flashcards mostra o número de pendentes.

---

### TASK-007 - UC-007 - Revisar flashcard (modo clássico)

**Feature:** Exibir o cartão pendente mais urgente com frente em inglês e revelar a tradução sob demanda.

**Backend:** Nenhum endpoint adicional; usa dados de `GET /api/flashcards`.

**Frontend:** FlashcardsScreen no modo "Clássico" — exibe `english`, botão "Mostrar resposta" revela `portuguese`; após revelação, exibir botões de avaliação (UC-009).

**Algoritmo:**

1. Pegar o primeiro card com `next_review ≤ now` da lista ordenada.
2. Renderizar frente do card.
3. Ao clicar "Mostrar resposta", revelar `portuguese` com animação `fadeSlide`.
4. Exibir botões de grading.

**Aceite:** Card correto é exibido; tradução fica oculta até o clique; botões de avaliação aparecem após revelação.

---

### TASK-008 - UC-008 - Revisar flashcard (modo digitação)

**Feature:** Campo para digitar a tradução antes de ver a resposta correta.

**Backend:** Nenhum endpoint adicional.

**Frontend:** Modo "Escrita" no FlashcardsScreen — input de texto; ao submeter (Enter ou botão), comparar resposta digitada com `portuguese` (normalizado: lowercase, sem acentos, sem pontuação); exibir feedback de acerto ou erro com a resposta correta.

**Algoritmo:**

1. Exibir `english` e campo de input.
2. Ao submeter, normalizar ambas as strings (remover acentos via `NFD`, lowercase, strip pontuação).
3. Aceitar variantes separadas por vírgula ou `/` em `portuguese`.
4. Exibir painel de resultado (correto/incorreto) e botões de grading.

**Aceite:** Resposta correta (incluindo variantes) é reconhecida; resposta errada mostra a correta; normalização de acentos funciona.

---

### TASK-009 - UC-009 - Avaliar flashcard (grading SRS)

**Feature:** Recalcular intervalo do cartão com base na avaliação do usuário.

**Backend:** Criar `PATCH /api/flashcards/:id/review`; aceitar `{grade}` onde grade ∈ `{again, hard, good, easy}`; aplicar lógica de intervalo e atualizar `flashcards`; chamar serviço de streak (UC-030).

**Frontend:** Quatro botões de avaliação após revelação da resposta; cada botão exibe o intervalo correspondente; ao clicar, remover card da fila de pendentes e avançar para o próximo.

**Algoritmo:**

1. Receber `grade`.
2. Calcular novo `interval_days` e `next_review`:
   - `again` → `interval_days = 0`; `next_review = now + 1 min`
   - `hard`  → `interval_days = max(1, interval_days)`; `next_review = now + 10 min`
   - `good`  → `interval_days = max(1, interval_days) × 1`; `next_review = now + interval_days dias`
   - `easy`  → `interval_days = max(1, interval_days) × ease_factor`; `next_review = now + interval_days dias`
   - Regra para card novo (`interval_days = 0`): `good = 1 dia`, `easy = 4 dias` (valores fixos do protótipo).
3. Atualizar `ease_factor` (+0.15 easy, −0.15 hard, −0.20 again; mínimo 1.30).
4. Incrementar `repetitions`; persistir novo `interval_days`.
5. Persistir atualizações.
6. Incrementar `daily_study_log.cards_reviewed`.

**Aceite:** Após avaliação, card não reaparece antes do novo `next_review`; `repetitions` é incrementado; log diário é atualizado.

---

## P2 — Gerador de Frases

### TASK-010 - UC-010 - Gerar frases a partir de palavras

**Feature:** Receber palavras-chave e retornar até cinco frases naturais em inglês com tradução, via IA.

**Backend:** Criar `POST /api/ai/generate-sentences`; aceitar `{words: string}`; chamar Gemini com schema estruturado; retornar `{sentences: [{english, portuguese}]}`.

**Frontend:** GeneratorScreen — input de texto, sugestões de tópico como chips, botão "Gerar frases"; exibir skeleton loader durante chamada; renderizar resultados com ClickableSentence e AudioPill.

**Algoritmo:**

1. Receber `words` (máx. 200 chars).
2. Montar prompt de sistema com instrução de professor de inglês para brasileiros adultos.
3. Chamar Gemini com `responseSchema` para array de `{english, portuguese}`.
4. Retornar array de até cinco itens.
5. Retornar 502 com mensagem amigável em caso de falha da IA.

**Aceite:** Frases geradas são naturais e utilizam as palavras fornecidas; falha da IA retorna erro tratado; campo vazio é bloqueado no frontend.

---

### TASK-011 - UC-011 - Adicionar frase gerada ao deck

**Feature:** Salvar uma ou todas as frases do Generator como flashcards com `source = generator`.

**Backend:** Reutiliza `POST /api/flashcards` do TASK-005.

**Frontend:** Botão "Adicionar" por frase e "Adicionar todas"; ao adicionar, botão muda para "Adicionado" e fica desabilitado; estado de adição não se perde ao rolar a página.

**Algoritmo:**

1. Ao clicar "Adicionar", chamar `POST /api/flashcards` com `{english, portuguese, source: 'generator'}`.
2. Marcar índice como adicionado no estado local.
3. "Adicionar todas" itera sobre todas as frases ainda não adicionadas.

**Aceite:** Flashcard aparece no deck; botão muda de estado; adicionar duas vezes a mesma frase cria dois cards (comportamento intencional para SRS).

---

## P3 — Vocabulário

### TASK-012 - UC-012 - Adicionar palavra ao vocabulário via IA

**Feature:** Buscar tradução, exemplo e tradução do exemplo para uma palavra, salvar no vocabulário e criar flashcard automaticamente.

**Backend:** Criar `POST /api/vocabulary`; aceitar `{word}`; chamar Gemini para enriquecer a palavra; inserir em `vocabulary_words`; chamar `POST /api/flashcards` internamente com `source = vocabulary`; retornar palavra enriquecida.

**Frontend:** Campo "Adicionar nova palavra" na VocabularyScreen; spinner durante chamada; ao concluir, palavra aparece no topo da lista.

**Algoritmo:**

1. Receber `word`.
2. Chamar Gemini: `{word, translation, example, exampleTranslation}`.
3. Verificar unicidade `(user_id, lower(word))` — retornar 409 se duplicada.
4. Inserir em `vocabulary_words`.
5. Inserir flashcard com `english = word`, `portuguese = translation`, `source = vocabulary`.
6. Incrementar `daily_study_log.words_added`.
7. Retornar `201` com a palavra enriquecida.

**Aceite:** Palavra salva com tradução e exemplo corretos; flashcard criado automaticamente; palavra duplicada retorna erro amigável.

---

### TASK-013 - UC-013 - Adicionar palavra ao vocabulário ao clicar no texto

**Feature:** Clicar em qualquer palavra de uma frase exibida abre modal de confirmação para adicioná-la ao vocabulário.

**Backend:** Reutiliza `POST /api/vocabulary` do TASK-012.

**Frontend:** Componente `ClickableSentence` — cada palavra é um span clicável; ao clicar, emitir evento com a palavra limpa (sem pontuação); App exibe modal de confirmação; ao confirmar, chamar TASK-012; spinner no botão durante chamada.

**Algoritmo:**

1. Usuário clica em palavra no Generator, Text Study ou Immersion.
2. App captura palavra sem pontuação (`replace(/[.,!?;:()"'—]/g, '')`).
3. Modal exibe: `"<palavra>" — Adicionar ao vocabulário e criar flashcard?`
4. Ao confirmar, chamar fluxo do TASK-012.
5. Fechar modal após sucesso.

**Aceite:** Modal abre com a palavra correta (sem pontuação); adição funciona; modal fecha após sucesso; erro de IA exibe mensagem no modal.

---

### TASK-014 - UC-014 - Listar e filtrar vocabulário

**Feature:** Retornar o vocabulário do usuário com suporte a filtragem por texto.

**Backend:** Criar `GET /api/vocabulary?q=<termo>`; filtrar por `word ILIKE` ou `translation ILIKE`; ordenar por `created_at DESC`.

**Frontend:** VocabularyScreen — grid de cards com palavra, tradução, exemplo e exemplo traduzido (blurred); input de filtro com debounce de 300 ms disparando nova chamada; exibir total de palavras no heading.

**Algoritmo:**

1. Receber parâmetro `q` (opcional).
2. Se `q` presente, aplicar `WHERE lower(word) LIKE lower('%q%') OR lower(translation) LIKE lower('%q%')`.
3. Retornar lista paginada com `{words[], total}`.

**Aceite:** Filtro retorna resultados relevantes; lista vazia exibe estado Empty; total no heading é preciso.

---

## P4 — Análise de Texto

### TASK-015 - UC-015 - Analisar texto em inglês

**Feature:** Receber texto em inglês e retornar análise linha a linha com visão geral, notas e flashcards prontos.

**Backend:** Criar `POST /api/ai/analyze-text`; aceitar `{text: string}`; chamar Gemini com schema `{overview, analysis[], ankiCards[]}`; retornar resultado sem persistir.

**Frontend:** TextStudyScreen — textarea para o texto; exibir contagem de chars e palavras em tempo real; botão "Analisar texto"; renderizar `overview`, `analysis` com `ClickableSentence` e notas, e grid de `ankiCards`.

**Algoritmo:**

1. Receber `text` (máx. 3000 chars).
2. Montar prompt de análise linha a linha com instrução de professor.
3. Chamar Gemini com schema estruturado.
4. Retornar `{overview, analysis[], ankiCards[]}`.

**Aceite:** Texto colado é analisado corretamente; notas gramaticais são exibidas por frase; falha da IA retorna erro tratado.

---

### TASK-016 - UC-016 - Salvar análise de texto

**Feature:** Persistir uma análise gerada para consulta posterior.

**Backend:** Criar `POST /api/text-analyses`; aceitar `{originalText, overview, analysisData, ankiCards}`; inserir em `text_analyses`; retornar `201` com `id`.

**Frontend:** Botão "Salvar análise" exibido após resultado gerado; confirmação visual ao salvar.

**Algoritmo:**

1. Receber payload com análise completa.
2. Inserir em `text_analyses` vinculado ao `user_id`.
3. Retornar `{id, createdAt}`.

**Aceite:** Análise salva é recuperável via listagem; `JSONB` armazena corretamente os arrays de análise e cards.

---

### TASK-017 - UC-017 - Adicionar flashcards da análise ao deck

**Feature:** Salvar um ou todos os `ankiCards` da análise como flashcards com `source = text_study`.

**Backend:** Reutiliza `POST /api/flashcards` do TASK-005.

**Frontend:** Botão "+" por card e "Adicionar todos" no grid de ankiCards; estado de adição por índice; "Adicionar todos" desabilita cada botão individual após execução.

**Algoritmo:**

1. Para cada card selecionado, chamar `POST /api/flashcards` com `{english: front, portuguese: back, source: 'text_study'}`.
2. Marcar card como adicionado no estado local.

**Aceite:** Flashcards criados aparecem no deck com `source = text_study`; botões mudam de estado ao adicionar.

---

## P5 — Tutor IA

### TASK-018 - UC-018 - Iniciar sessão de chat de prática

**Feature:** Criar uma nova sessão de conversa em inglês com nível CEFR configurável.

**Backend:** Criar `POST /api/chat/sessions`; aceitar `{mode: 'chat', cefrLevel}`; inserir em `chat_sessions`; inserir primeira mensagem da IA ("Hello! Ready to practice some English today?") em `chat_messages`; retornar `{sessionId, messages[]}`.

**Frontend:** Botão "Nova Conversa" na TutorScreen modo chat; selector de nível CEFR (A1–C2); ao criar sessão, exibir primeira mensagem da IA.

**Algoritmo:**

1. Receber `{mode: 'chat', cefrLevel}`.
2. Inserir `chat_session`.
3. Inserir mensagem de boas-vindas da IA.
4. Retornar sessão com histórico inicial.

**Aceite:** Sessão criada com nível correto; mensagem de boas-vindas exibida; sessão anterior não é sobrescrita.

---

### TASK-019 - UC-019 - Enviar mensagem no chat de prática

**Feature:** Enviar mensagem em inglês, receber resposta da IA no nível configurado e feedback sobre o inglês usado.

**Backend:** Criar `POST /api/chat/sessions/:id/messages`; aceitar `{content}`; reconstruir histórico; chamar Gemini com prompt de amigo/Alex no nível CEFR; persistir mensagem do usuário e resposta da IA; retornar `{aiMessage: {text, translation, feedback}}`.

**Frontend:** Input de texto + botão enviar; mensagem do usuário aparece imediatamente (optimistic update); indicador de digitação (três pontos pulsando) enquanto aguarda IA; ao receber, exibir resposta com tradução colapsável e balão de feedback.

**Algoritmo:**

1. Exibir mensagem do usuário imediatamente na UI.
2. Buscar histórico completo da sessão.
3. Montar prompt com histórico + instrução de nível.
4. Chamar Gemini: `{text, translation, feedback}`.
5. Persistir ambas as mensagens.
6. Retornar resposta da IA.

**Aceite:** Resposta chega com tradução e feedback; histórico mantém contexto entre mensagens; falha da IA exibe mensagem de erro no chat.

---

### TASK-020 - UC-020 - Analisar e corrigir frase antes de enviar

**Feature:** Corrigir gramaticalmente a frase digitada antes do envio, com explicação didática.

**Backend:** Criar `POST /api/ai/correct-sentence`; aceitar `{text, cefrLevel}`; chamar Gemini: `{correctedText, explanation}`; retornar resultado sem persistir.

**Frontend:** Botão de olho (👁) no input do chat de prática; painel sobreposto exibe `correctedText` e `explanation`; botão "Aplicar correção" substitui o texto no input; botão X fecha o painel.

**Algoritmo:**

1. Receber `{text, cefrLevel}`.
2. Chamar Gemini com prompt de correção para o nível informado.
3. Retornar `{correctedText, explanation}`.

**Aceite:** Correção exibida não bloqueia o envio; usuário pode ignorar a sugestão; frase já correta recebe elogio no `explanation`.

---

### TASK-021 - UC-021 - Obter sugestões de resposta no chat

**Feature:** Gerar três opções de resposta adequadas ao contexto da conversa.

**Backend:** Criar `POST /api/ai/suggest-replies`; aceitar `{sessionId}`; buscar histórico; chamar Gemini retornando array de três strings.

**Frontend:** Botão de lâmpada (💡) no input; chips clicáveis aparecem acima do input; clicar em um chip preenche o input sem enviar; chips somem ao enviar mensagem.

**Algoritmo:**

1. Receber `sessionId`.
2. Buscar as últimas N mensagens da sessão (máx. 10 para otimizar tokens).
3. Chamar Gemini com instrução de sugestão de resposta para o nível CEFR da sessão.
4. Retornar `{suggestions: [string, string, string]}`.

**Aceite:** Três sugestões exibidas como chips; clique preenche o input; novo pedido descarta sugestões anteriores.

---

### TASK-022 - UC-022 - Fazer pergunta ao professor IA

**Feature:** Tirar dúvidas de inglês em português, recebendo respostas didáticas com exemplos.

**Backend:** Criar `POST /api/chat/sessions`; aceitar `{mode: 'teacher'}`; e reutilizar `POST /api/chat/sessions/:id/messages` com prompt de professor (responde em PT-BR, sem Markdown).

**Frontend:** Aba "Dúvidas" na TutorScreen; temas rápidos como atalhos clicáveis; histórico com avatares distintos (V = você, P = professor).

**Algoritmo:**

1. Criar sessão com `mode = teacher` se não existir.
2. Ao enviar pergunta, chamar endpoint de mensagem com prompt de professor.
3. Gemini responde como professor didático em PT-BR.
4. Persistir e retornar resposta.

**Aceite:** Respostas chegam em PT-BR; exemplos em inglês são claramente identificados; histórico preserva contexto da conversa.

---

### TASK-023 - UC-023 - Retomar sessão de chat

**Feature:** Carregar histórico de uma sessão anterior para continuar a conversa.

**Backend:** Criar `GET /api/chat/sessions` para listar sessões do usuário; criar `GET /api/chat/sessions/:id/messages` para carregar mensagens de uma sessão.

**Frontend:** Ao entrar na TutorScreen, verificar se existe sessão ativa; se sim, carregar automaticamente a mais recente de cada modo; exibir opção de "Nova conversa" para descartar e iniciar do zero.

**Algoritmo:**

1. `GET /api/chat/sessions?mode=chat&limit=1` retorna a sessão mais recente de prática.
2. `GET /api/chat/sessions/:id/messages` retorna todas as mensagens ordenadas por `created_at`.
3. Frontend hidrata o estado `chatMessages` com o histórico.

**Aceite:** Histórico carregado corretamente ao reabrir a tela; "Nova conversa" limpa o estado e cria nova sessão.

---

## P6 — Fill-in-the-blank

### TASK-024 - UC-024 - Gerar exercícios fill-in-the-blank

**Feature:** Gerar exatamente dez exercícios de preencher lacunas a partir de palavras-alvo e nível CEFR.

**Backend:** Criar `POST /api/ai/fill-in`; aceitar `{words: string[], cefrLevel}`; chamar Gemini com schema de array de dez itens `{sentence, answer, translation, hint, options[]}`; retornar resultado.

**Frontend:** FillInScreen — textarea para palavras, selector de nível, toggles de modo (digitação/múltipla escolha); botão "Gerar exercícios"; ao gerar, exibir primeiro exercício imediatamente.

**Algoritmo:**

1. Receber `{words[], cefrLevel}`.
2. Montar prompt instruindo exatamente dez exercícios com as palavras-alvo como respostas.
3. Chamar Gemini com schema estruturado.
4. Embaralhar `options` de cada exercício antes de retornar.
5. Retornar array de dez exercícios.

**Aceite:** Exatamente dez exercícios gerados; cada um tem quatro opções embaralhadas; a resposta correta está sempre nas opções.

---

### TASK-025 - UC-025 - Responder exercício fill-in

**Feature:** Verificar resposta digitada ou alternativa selecionada e exibir feedback imediato.

**Backend:** Sem endpoint; lógica de validação no frontend.

**Frontend:** Modo digitação: input inline dentro da frase + botão "Verificar resposta"; modo múltipla escolha: quatro botões A/B/C/D; ao verificar, exibir painel de resultado (correto/incorreto) com resposta correta, hint e botão "Guardar no deck"; botão "Próxima" avança para o exercício seguinte; botão "Reiniciar" no header limpa toda a sessão.

**Algoritmo:**

1. Normalizar input do usuário e `answer` (lowercase, trim).
2. Comparar strings normalizadas.
3. Registrar resultado `{correct, userAnswer}` no array de resultados local.
4. Exibir painel de feedback com cor adequada (verde/vermelho) e hint sempre visível após verificação.
5. Exibir botão "Guardar no deck" — ao clicar, chamar `POST /api/flashcards` com `{english: ex.sentence, portuguese: ex.translation, source: 'fill_in'}` (a `sentence` contém `___` que serve como pista visual no card).
6. Habilitar botão "Próxima" ou "Ver resultado" se for o último.

**Aceite:** Resposta correta reconhecida independente de maiúsculas/minúsculas; feedback visual claro; hint exibido após verificação; "Guardar no deck" cria flashcard com a frase completa (incluindo `___`) como frente e a tradução como verso.

---

### TASK-026 - UC-026 - Finalizar sessão fill-in e ver resultado

**Feature:** Exibir placar final e persistir a sessão para histórico de progresso.

**Backend:** Criar `POST /api/fillin-sessions`; aceitar `{cefrLevel, wordsInput, total, correct}`; inserir em `fillin_sessions`.

**Frontend:** Ao concluir o décimo exercício, exibir tela de resultado com placar `X de 10`; botão "Recomeçar" volta ao formulário de geração; botão "Ver deck" navega para Flashcards.

**Algoritmo:**

1. Ao clicar "Ver resultado", exibir placar calculado do estado local.
2. Chamar `POST /api/fillin-sessions` em background com os dados da sessão.
3. Exibir placar e opções de navegação.

**Aceite:** Placar exibido corretamente; sessão persistida no banco; botão "Recomeçar" limpa todo o estado do exercício.

---

## P7 — Fonética

### TASK-027 - UC-027 - Transcrever frase para pronúncia figurada

**Feature:** Gerar pronúncia figurada em português brasileiro para uma frase em inglês, com dicas de articulação.

**Backend:** Criar `POST /api/ai/phonetics`; aceitar `{text: string}`; chamar Gemini com schema `{original, phonetic, translation, words[]}`; retornar resultado.

**Frontend:** PhoneticsScreen — textarea para a frase, botão "Transcrever"; exibir transcrição destacada em `font-mono accent`, frase original, tradução e grid de palavras difíceis com dica de articulação; botão de áudio via SpeechSynthesis.

**Algoritmo:**

1. Receber `text` (máx. 500 chars).
2. Montar prompt instruindo pronúncia figurada com sons do PT-BR (sem IPA).
3. Chamar Gemini com schema estruturado.
4. Retornar `{original, phonetic, translation, words[{word, phonetic, tip}]}`.

**Aceite:** Transcrição usa sons do português (ex: "ai lâv iu"); máximo seis palavras no detalhe; botão de áudio reproduz frase original corretamente.

---

## P8 — Imersão (Comprehensible Input)

### TASK-028 - UC-028 - Gerar história para imersão

**Feature:** Gerar história curta calibrada por nível CEFR com tradução por parágrafo e quiz de compreensão.

**Backend:** Criar `POST /api/ai/immersion`; aceitar `{topic: string, cefrLevel}`; chamar Gemini com schema `{title, paragraphs[], quiz[]}`; retornar resultado.

**Frontend:** ImmersionScreen — input de tema (opcional), selector de nível, botão "Gerar história"; exibir título, parágrafos com palavras clicáveis para vocabulário e botão "Revelar tradução / Esconder tradução" por parágrafo (toggle individual); seção de quiz com três perguntas de múltipla escolha.

**Algoritmo:**

1. Receber `{topic, cefrLevel}` (topic usa padrão se vazio).
2. Montar prompt baseado no método Comprehensible Input de Krashen.
3. Chamar Gemini com schema estruturado (2–4 parágrafos, exatamente 3 quiz).
4. Retornar resultado.

**Aceite:** História coerente com vocabulário adequado ao nível; três perguntas de quiz geradas; parágrafos com tradução correta.

---

### TASK-029 - UC-029 - Responder quiz de compreensão

**Feature:** Selecionar respostas do quiz e ver correção com placar.

**Backend:** Sem endpoint; lógica no frontend.

**Frontend:** Três perguntas com opções como botões; ao selecionar, opção fica destacada; botão "Corrigir respostas" (habilitado após responder todas) exibe resultado com opção correta em verde e incorreta em vermelho; placar `X de 3`; botão "Gerar outra história".

**Algoritmo:**

1. Rastrear `quizAnswers: {[questionIndex]: selectedOption}` no estado local.
2. Habilitar "Corrigir" somente quando `Object.keys(quizAnswers).length === 3`.
3. Ao corrigir, comparar cada `quizAnswers[i]` com `quiz[i].answer`.
4. Aplicar classes de cor nos botões e exibir placar.

**Aceite:** Impossível corrigir sem responder todas; cores de feedback corretas; placar preciso.

---

## P9 — Streak e Estatísticas

### TASK-030 - UC-030 - Registrar atividade diária

**Feature:** Atualizar o log diário e recalcular o streak do usuário a cada ação de estudo.

**Backend:** Criar serviço interno `StudyLogService.record(userId)`; chamado pelos serviços de flashcard (TASK-009), vocabulário (TASK-012), análise de texto (TASK-015) e fill-in (TASK-026); lógica de streak:
- Se `last_study_date = ontem` → incrementar `current_streak`.
- Se `last_study_date = hoje` → manter `current_streak`.
- Se `last_study_date < ontem` → resetar `current_streak = 1`.
- Atualizar `longest_streak` se `current_streak > longest_streak`.

**Frontend:** Sidebar exibe streak atualizado; barra de dias da semana reflete `daily_study_log` dos últimos sete dias.

**Algoritmo:**

1. Inserir ou incrementar `daily_study_log(user_id, today)` com `ON CONFLICT DO UPDATE`.
2. Buscar `study_streaks` do usuário.
3. Aplicar lógica de streak descrita acima.
4. Atualizar `study_streaks` com novos valores.

**Aceite:** Streak incrementa somente em dias consecutivos; reset ocorre corretamente após dia sem atividade; `longest_streak` nunca diminui.

---

### TASK-031 - UC-031 - Visualizar estatísticas de estudo

**Feature:** Retornar em um único endpoint todos os dados de progresso do usuário para o sidebar e dashboard.

**Backend:** Criar `GET /api/me/stats`; retornar `{currentStreak, longestStreak, totalDays, cardsReviewedToday, wordsAddedThisWeek, dailyLog[last7days]}`.

**Frontend:** Sidebar exibe `currentStreak` com ícone de chama e barra de sete dias; ao abrir o app, chamar `GET /api/me/stats` e hidratar o estado global de estatísticas.

**Algoritmo:**

1. Buscar `study_streaks` do usuário.
2. Buscar `daily_study_log` dos últimos sete dias.
3. Calcular `cardsReviewedToday` a partir do log do dia atual.
4. Calcular `wordsAddedThisWeek` somando `words_added` dos últimos sete dias.
5. Retornar payload consolidado.

**Aceite:** Endpoint retorna todos os campos em uma única chamada; sidebar reflete corretamente os valores; `dailyLog` tem exatamente sete entradas (preencher com zeros os dias sem atividade).
