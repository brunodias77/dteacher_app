package com.dias.dteacher.error;

import com.dias.dteacher.validation.Error;

public final class GeneratorError {

    private GeneratorError() {}

    public static final Error WORDS_REQUIRED = new Error("Informe pelo menos uma palavra ou expressão");
    public static final Error WORDS_TOO_LONG = new Error("O campo de palavras deve ter no máximo 200 caracteres");
    public static final Error AI_UNAVAILABLE = new Error("O serviço de geração de frases está temporariamente indisponível. Tente novamente.");

    public static final Error WORD_REQUIRED = new Error("Informe a palavra a ser traduzida");
    public static final Error WORD_TOO_LONG = new Error("A palavra deve ter no máximo 100 caracteres");
}
