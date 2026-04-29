package com.dias.dteacher.usecase.preference.update;

public record UpdatePreferencesResponse(
        String accent,
        String density,
        String uppercaseLevel,
        Boolean showStreakBar,
        String defaultCefr,
        String lastActiveTab
) {
}
