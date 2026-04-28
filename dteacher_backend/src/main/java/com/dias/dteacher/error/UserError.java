package com.dias.dteacher.error;

import com.dias.dteacher.validation.Error;

public final class UserError {

    private UserError() {}

    // --- email ---
    public static final Error EMAIL_REQUIRED       = new Error("E-mail é obrigatório");
    public static final Error EMAIL_INVALID        = new Error("E-mail deve ser um endereço válido");
    public static final Error EMAIL_TOO_LONG       = new Error("E-mail deve ter no máximo 255 caracteres");
    public static final Error EMAIL_ALREADY_EXISTS = new Error("E-mail já está em uso");

    // --- password ---
    public static final Error PASSWORD_REQUIRED    = new Error("Senha é obrigatória");
    public static final Error PASSWORD_TOO_SHORT   = new Error("Senha deve ter no mínimo 8 caracteres");
    public static final Error PASSWORD_TOO_LONG    = new Error("Senha deve ter no máximo 255 caracteres");

    // --- display name ---
    public static final Error DISPLAY_NAME_TOO_LONG = new Error("Nome de exibição deve ter no máximo 100 caracteres");

    // --- lookup / auth ---
    public static final Error NOT_FOUND            = new Error("Usuário não encontrado");
    public static final Error INVALID_CREDENTIALS  = new Error("E-mail ou senha inválidos");
    public static final Error EMAIL_ALREADY_EXISTS_CONFLICT = new Error("E-mail já cadastrado");
    public static final Error PASSWORD_NO_NUMBER   = new Error("Senha deve conter pelo menos 1 número");
    public static final Error PASSWORD_MISMATCH    = new Error("Confirmação de senha não confere");
}
