package com.dias.dteacher.usecase.preference.get;

public record GetPreferencesResponse(
        String accent,
        String density,
        String uppercaseLevel,
        Boolean showStreakBar,
        String defaultCefr,
        String lastActiveTab
) {
}
