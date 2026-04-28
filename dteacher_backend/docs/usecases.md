# Use Cases — btree_english

Ordenados por prioridade de implementação: autenticação e base de dados primeiro,
depois o núcleo de valor (SRS), depois os módulos de geração de conteúdo via IA.

---

## Prioridade 1 — Autenticação e Perfil

### UC-001 - Cadastrar usuário

**Ator principal:** Visitante

**Objetivo:** Criar uma conta para salvar progresso, vocabulário, flashcards e histórico de estudos.

**Resultado esperado:** Usuário criado com perfil inicial, preferências padrão e estatísticas zeradas.

---

### UC-002 - Autenticar usuário

**Ator principal:** Visitante

**Objetivo:** Acessar a conta existente com e-mail e senha para retomar os estudos de onde parou.

**Resultado esperado:** Sessão autenticada com token JWT; dados do usuário carregados.

---

### UC-003 - Fazer logout

**Ator principal:** Usuário autenticado

**Objetivo:** Encerrar a sessão ativa no dispositivo atual.

**Resultado esperado:** Token invalidado; usuário redirecionado para a tela de login.

---

### UC-004 - Salvar preferências de interface

**Ator principal:** Usuário autenticado

**Objetivo:** Personalizar accent color, densidade de layout, uppercase level e visibilidade do streak bar.

**Resultado esperado:** Preferências persistidas no banco e aplicadas imediatamente na interface.

---

## Prioridade 2 — Flashcards (SRS)

### UC-005 - Adicionar flashcard ao deck

**Ator principal:** Usuário autenticado

**Objetivo:** Salvar um par inglês/português como cartão de memorização para revisão futura.

**Resultado esperado:** Flashcard criado com `next_review = agora`, `repetitions = 0` e `source` identificada.

---

### UC-006 - Listar flashcards do deck

**Ator principal:** Usuário autenticado

**Objetivo:** Consultar todos os cartões do deck, incluindo quantos estão pendentes de revisão.

**Resultado esperado:** Lista de flashcards com contagem de pendentes (next_review ≤ agora) e total.

---

### UC-007 - Revisar flashcard (modo clássico)

**Ator principal:** Usuário autenticado

**Objetivo:** Ver a frente do cartão em inglês e revelar manualmente a tradução para avaliar o próprio desempenho.

**Resultado esperado:** Cartão exibido; usuário aguarda ação de revelação antes de avaliar.

---

### UC-008 - Revisar flashcard (modo digitação)

**Ator principal:** Usuário autenticado

**Objetivo:** Digitar a tradução do cartão antes de revelar a resposta correta, forçando recall ativo.

**Resultado esperado:** Resposta do usuário comparada à tradução esperada (normalização de acentos e pontuação); feedback visual de acerto ou erro.

---

### UC-009 - Avaliar flashcard (grading SRS)

**Ator principal:** Usuário autenticado

**Objetivo:** Classificar o resultado da revisão (Errei / Difícil / Bom / Fácil) para que o algoritmo recalcule o próximo intervalo.

**Resultado esperado:** `next_review`, `interval_days`, `ease_factor` e `repetitions` atualizados conforme a avaliação; log diário incrementado.

---

## Prioridade 3 — Gerador de Frases

### UC-010 - Gerar frases a partir de palavras

**Ator principal:** Usuário autenticado

**Objetivo:** Informar até cinco palavras ou expressões e receber frases naturais em inglês geradas pela IA, com tradução.

**Resultado esperado:** Array de até cinco pares `{english, portuguese}` retornados pela IA e exibidos na interface.

---

### UC-011 - Adicionar frase gerada ao deck

**Ator principal:** Usuário autenticado

**Objetivo:** Salvar uma ou todas as frases geradas como flashcards com `source = generator`.

**Resultado esperado:** Flashcard(s) criado(s); indicador visual de "adicionado" na frase correspondente.

---

## Prioridade 4 — Vocabulário

### UC-012 - Adicionar palavra ao vocabulário via IA

**Ator principal:** Usuário autenticado

**Objetivo:** Digitar uma palavra ou expressão em inglês e obter tradução, exemplo de uso e criar automaticamente um flashcard.

**Resultado esperado:** Entrada salva em `vocabulary_words`; flashcard criado em `flashcards` com `source = vocabulary`.

---

### UC-013 - Adicionar palavra ao vocabulário ao clicar no texto

**Ator principal:** Usuário autenticado

**Objetivo:** Clicar em qualquer palavra de uma frase exibida (Generator, Text Study, Immersion) para iniciar a adição ao vocabulário.

**Resultado esperado:** Modal de confirmação exibido com a palavra selecionada; ao confirmar, fluxo idêntico ao UC-012.

---

### UC-014 - Listar e filtrar vocabulário

**Ator principal:** Usuário autenticado

**Objetivo:** Visualizar todas as palavras salvas e filtrá-las por termo (palavra ou tradução).

**Resultado esperado:** Lista filtrada em tempo real com palavra, tradução, exemplo e data de adição.

---

## Prioridade 5 — Análise de Texto

### UC-015 - Analisar texto em inglês

**Ator principal:** Usuário autenticado

**Objetivo:** Colar um texto em inglês e receber: visão geral em português, análise linha a linha com notas gramaticais e cartões prontos para o deck.

**Resultado esperado:** Objeto `{overview, analysis[], ankiCards[]}` retornado pela IA e renderizado na tela.

---

### UC-016 - Salvar análise de texto

**Ator principal:** Usuário autenticado

**Objetivo:** Persistir uma análise gerada para consulta posterior sem precisar chamar a IA novamente.

**Resultado esperado:** Registro criado em `text_analyses` com `original_text`, `overview`, `analysis_data` e `anki_cards`.

---

### UC-017 - Adicionar flashcards da análise ao deck

**Ator principal:** Usuário autenticado

**Objetivo:** Salvar um ou todos os `ankiCards` extraídos pela análise como flashcards com `source = text_study`.

**Resultado esperado:** Flashcard(s) criado(s); botão "Adicionar todos" marca os itens como adicionados.

---

## Prioridade 6 — Tutor IA

### UC-018 - Iniciar sessão de chat de prática

**Ator principal:** Usuário autenticado

**Objetivo:** Começar uma nova conversa em inglês com o personagem "Alex" no nível CEFR selecionado.

**Resultado esperado:** `chat_session` criada com `mode = chat` e nível escolhido; primeira mensagem da IA registrada.

---

### UC-019 - Enviar mensagem no chat de prática

**Ator principal:** Usuário autenticado

**Objetivo:** Escrever uma mensagem em inglês, receber resposta da IA no nível configurado e feedback sobre o inglês usado.

**Resultado esperado:** Mensagem do usuário e resposta da IA (`{text, translation, feedback}`) salvas em `chat_messages`.

---

### UC-020 - Analisar e corrigir frase antes de enviar

**Ator principal:** Usuário autenticado

**Objetivo:** Acionar a revisão da frase digitada antes de enviá-la para obter sugestão de correção gramatical.

**Resultado esperado:** `{correctedText, explanation}` exibidos em painel sobreposto; usuário pode aplicar a correção ou ignorar.

---

### UC-021 - Obter sugestões de resposta no chat

**Ator principal:** Usuário autenticado

**Objetivo:** Solicitar três opções de resposta adequadas ao contexto da conversa e ao nível CEFR configurado.

**Resultado esperado:** Array de três strings exibido como chips clicáveis; ao clicar, a sugestão preenche o campo de texto.

---

### UC-022 - Fazer pergunta ao professor IA

**Ator principal:** Usuário autenticado

**Objetivo:** Tirar dúvidas sobre gramática, vocabulário ou pronúncia em português, recebendo explicações didáticas com exemplos.

**Resultado esperado:** `chat_session` criada com `mode = teacher`; resposta salva em `chat_messages`.

---

### UC-023 - Retomar sessão de chat

**Ator principal:** Usuário autenticado

**Objetivo:** Carregar o histórico de uma sessão de chat anterior para continuar de onde parou.

**Resultado esperado:** Mensagens da sessão carregadas em ordem cronológica; interface pronta para nova interação.

---

## Prioridade 7 — Fill-in-the-blank

### UC-024 - Gerar exercícios fill-in-the-blank

**Ator principal:** Usuário autenticado

**Objetivo:** Informar palavras-alvo e nível CEFR para receber exatamente dez frases com lacunas, cada uma com quatro alternativas e uma dica.

**Resultado esperado:** Array de dez exercícios `{sentence, answer, translation, hint, options[]}` retornado pela IA.

---

### UC-025 - Responder exercício fill-in

**Ator principal:** Usuário autenticado

**Objetivo:** Digitar a palavra ou escolher entre as alternativas para preencher a lacuna, verificar a resposta, opcionalmente guardar a frase como flashcard e avançar ao próximo exercício.

**Resultado esperado:** Resultado do exercício (correto/errado) registrado; hint exibido após verificação; botão "Guardar no deck" disponível para criar flashcard com a frase (contendo `___`) como frente e a tradução como verso; estado local atualizado para o próximo item.

---

### UC-026 - Finalizar sessão fill-in e ver resultado

**Ator principal:** Usuário autenticado

**Objetivo:** Concluir todos os dez exercícios e visualizar o placar final (acertos / total).

**Resultado esperado:** Sessão salva em `fillin_sessions` com `total`, `correct`, `cefr_level` e `words_input`; placar exibido na tela.

---

## Prioridade 8 — Fonética

### UC-027 - Transcrever frase para pronúncia figurada

**Ator principal:** Usuário autenticado

**Objetivo:** Digitar uma frase em inglês e receber a pronúncia figurada usando sons do português, com dicas de articulação para as palavras mais difíceis.

**Resultado esperado:** Objeto `{original, phonetic, translation, words[]}` retornado pela IA e exibido com botão de áudio.

---

## Prioridade 9 — Imersão (Comprehensible Input)

### UC-028 - Gerar história para imersão

**Ator principal:** Usuário autenticado

**Objetivo:** Informar um tema e nível CEFR para receber uma história curta calibrada para o nível, com tradução parágrafo a parágrafo e quiz de compreensão.

**Resultado esperado:** Objeto `{title, paragraphs[], quiz[]}` retornado pela IA; parágrafos com tradução ocultável.

---

### UC-029 - Responder quiz de compreensão

**Ator principal:** Usuário autenticado

**Objetivo:** Selecionar as respostas das três perguntas do quiz e corrigir ao finalizar para verificar a compreensão do texto.

**Resultado esperado:** Acertos contabilizados e exibidos; opções corretas destacadas em verde, incorretas em vermelho.

---

## Prioridade 10 — Streak e Estatísticas

### UC-030 - Registrar atividade diária

**Ator principal:** Sistema (acionado após UC-009, UC-012, UC-015, UC-024)

**Objetivo:** Atualizar o log diário e recalcular o streak do usuário sempre que ele realizar uma atividade de estudo.

**Resultado esperado:** `daily_study_log` incrementado; `study_streaks.current_streak` atualizado (incrementa se atividade no dia seguinte, reinicia se intervalo > 1 dia, mantém se já estudou hoje).

---

### UC-031 - Visualizar estatísticas de estudo

**Ator principal:** Usuário autenticado

**Objetivo:** Consultar o streak atual, streak máximo, total de dias estudados, cartões revisados hoje e palavras adicionadas esta semana.

**Resultado esperado:** Dados consolidados de `study_streaks` e `daily_study_log` retornados em um único endpoint.
