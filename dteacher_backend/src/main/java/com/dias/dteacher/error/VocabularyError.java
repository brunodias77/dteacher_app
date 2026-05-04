package com.dias.dteacher.error;

import com.dias.dteacher.validation.Error;

public final class VocabularyError {

    private VocabularyError() {}

    public static final Error WORD_REQUIRED    = new Error("Palavra é obrigatória");
    public static final Error WORD_TOO_LONG    = new Error("Palavra deve ter no máximo 150 caracteres");
    public static final Error WORD_DUPLICATE   = new Error("Palavra já existe no seu vocabulário");
    public static final Error AI_UNAVAILABLE   = new Error("Serviço de IA temporariamente indisponível. Tente novamente.");
}
