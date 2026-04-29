package com.dias.dteacher.error;

import com.dias.dteacher.validation.Error;

public final class PreferenceError {

    private PreferenceError() {}

    public static final Error ACCENT_INVALID         = new Error("Accent inválido. Valores aceitos: lime, amber, cyan, magenta, white");
    public static final Error DENSITY_INVALID        = new Error("Density inválida. Valores aceitos: compact, cozy, roomy");
    public static final Error UPPERCASE_LEVEL_INVALID = new Error("Uppercase level inválido. Valores aceitos: labels, headings, off");
    public static final Error CEFR_INVALID           = new Error("Nível CEFR inválido. Valores aceitos: A1, A2, B1, B2, C1, C2");
    public static final Error TAB_INVALID            = new Error("Aba inválida. Valores aceitos: generator, flashcards, vocabulary, textStudy, tutor, fillin, phonetics, immersion");
}
