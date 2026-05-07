package com.dias.dteacher.error;

import com.dias.dteacher.validation.Error;

public final class ChatError {

    private ChatError() {}

    public static final Error CEFR_LEVEL_REQUIRED = new Error("Nível CEFR é obrigatório");
    public static final Error CEFR_LEVEL_INVALID  = new Error("Nível CEFR inválido. Use: A1, A2, B1, B2, C1 ou C2");
    public static final Error CONTENT_REQUIRED    = new Error("Mensagem não pode estar vazia");
}
