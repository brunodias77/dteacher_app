package com.dias.dteacher.error;

import com.dias.dteacher.validation.Error;

public final class TextAnalysisError {

    private TextAnalysisError() {}

    public static final Error TEXT_REQUIRED  = new Error("Texto é obrigatório");
    public static final Error TEXT_TOO_LONG  = new Error("Texto deve ter no máximo 3000 caracteres");
    public static final Error AI_UNAVAILABLE = new Error("Serviço de IA temporariamente indisponível. Tente novamente.");
}
