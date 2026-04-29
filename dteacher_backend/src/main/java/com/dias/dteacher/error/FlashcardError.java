package com.dias.dteacher.error;

import com.dias.dteacher.validation.Error;

public final class FlashcardError {

    private FlashcardError() {}

    public static final Error ENGLISH_REQUIRED    = new Error("Texto em inglês é obrigatório");
    public static final Error PORTUGUESE_REQUIRED = new Error("Tradução em português é obrigatória");
    public static final Error SOURCE_TOO_LONG     = new Error("Fonte deve ter no máximo 30 caracteres");
}
