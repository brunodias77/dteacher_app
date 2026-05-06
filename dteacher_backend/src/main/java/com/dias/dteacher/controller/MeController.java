package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.preference.get.GetPreferencesRequest;
import com.dias.dteacher.usecase.preference.get.GetPreferencesUseCase;
import com.dias.dteacher.usecase.preference.update.UpdatePreferencesRequest;
import com.dias.dteacher.usecase.preference.update.UpdatePreferencesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final GetPreferencesUseCase    getPreferencesUseCase;
    private final UpdatePreferencesUseCase updatePreferencesUseCase;

    @GetMapping("/preferences")
    public ResponseEntity<?> getPreferences(@AuthenticationPrincipal UserDetails userDetails) {
        return getPreferencesUseCase.execute(new GetPreferencesRequest(userDetails.getUsername())).fold(
                Controllers::unprocessable,
                body -> ResponseEntity.ok(body)
        );
    }

    @PutMapping("/preferences")
    public ResponseEntity<?> updatePreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdatePreferencesRequest body) {
        var request = new UpdatePreferencesRequest(
                userDetails.getUsername(),
                body.accent(),
                body.density(),
                body.uppercaseLevel(),
                body.showStreakBar(),
                body.defaultCefr(),
                body.lastActiveTab()
        );
        return updatePreferencesUseCase.execute(request).fold(
                Controllers::unprocessable,
                body2 -> ResponseEntity.ok(body2)
        );
    }
}
