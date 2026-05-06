Analisei o projeto completo. Aqui está o resumo antes da recomendação:

**O projeto dTeacher** é um tutor de inglês para brasileiros adultos com Spring Boot 4 + Ollama local. O modelo atual é `gemma3:4b` (~2.5 GB). As tarefas que o Ollama executa são bem simples: gerar frases, traduzir palavras, enriquecer vocabulário e analisar textos — nenhuma delas exige um modelo grande.

---

Pesquisei a biblioteca do Ollama agora. Com base nas suas necessidades (PT-BR ↔ EN, instrução em JSON, prompts simples), estas são as melhores opções leves:

**🥇 Recomendação principal: `gemma3:1b`**
É o drop-in replacement perfeito — mesma família do `gemma3:4b` que você já usa, mas ocupa ~815 MB em vez de ~2.5 GB. Segue JSON bem, tem bom suporte bilíngue e roda suave mesmo sem GPU.

**🥈 Alternativa: `qwen2.5:1.5b`**
O Qwen2.5 tem excelente suporte multilíngue nativo (PT-BR e EN) e é muito bom em seguir schemas JSON estruturados. Ocupa ~1 GB. Vale testar se o gemma3:1b der respostas ruins.

**🥉 Terceira opção: `llama3.2:1b`**
O menor modelo do Meta (630 MB), mas o suporte a PT-BR é inferior às duas opções acima — não recomendo para esse app.

---

Para trocar, basta mudar duas linhas no seu projeto:

Em `docker/docker-compose.yml`, troque o comando do `ollama-pull`:
```yaml
# Antes
command: [..., "{\"name\":\"gemma3:4b\"}", ...]

# Depois
command: [..., "{\"name\":\"gemma3:1b\"}", ...]
```

Em `application.yaml`:
```yaml
ollama:
  model: ${OLLAMA_MODEL:gemma3:1b}
```

O `OllamaService.java` não precisa de nenhuma alteração — o código já usa a variável `${ollama.model}`.

---

**Minha indicação final é o `gemma3:1b`.** Ele vai funcionar muito bem para gerar frases, traduzir vocabulário e analisar textos simples. Se a qualidade das respostas cair para alguma tarefa específica (o analisador de texto é o mais exigente), suba para o `qwen2.5:3b` que ainda é bem mais leve que o `gemma3:4b` atual.