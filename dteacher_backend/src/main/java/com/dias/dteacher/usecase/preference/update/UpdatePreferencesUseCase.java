package com.dias.dteacher.usecase.preference.update;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.PreferenceError;
import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.User;
import com.dias.dteacher.model.UserPreference;
import com.dias.dteacher.repository.UserPreferenceRepository;
import com.dias.dteacher.repository.UserRepository;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdatePreferencesUseCase implements UseCase<UpdatePreferencesRequest, UpdatePreferencesResponse> {

    private static final Set<String> VALID_ACCENTS  = Set.of("lime", "amber", "cyan", "magenta", "white");
    private static final Set<String> VALID_DENSITIES = Set.of("compact", "cozy", "roomy");
    private static final Set<String> VALID_CAPS      = Set.of("labels", "headings", "off");
    private static final Set<String> VALID_CEFR      = Set.of("A1", "A2", "B1", "B2", "C1", "C2");
    private static final Set<String> VALID_TABS      = Set.of(
            "generator", "flashcards", "vocabulary", "textStudy",
            "tutor", "fillin", "phonetics", "immersion"
    );

    private final UserRepository           userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    @Override
    @Transactional
    public Either<Notification, UpdatePreferencesResponse> execute(UpdatePreferencesRequest request) {
        Notification notification = Notification.create();
        validate(request, notification);
        if (notification.hasError()) return Either.left(notification);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> NotFoundException.of("User", request.email()));

        UserPreference pref = userPreferenceRepository.findById(user.getId())
                .orElse(UserPreference.builder().user(user).build());

        if (request.accent()         != null) pref.setAccent(request.accent());
        if (request.density()        != null) pref.setDensity(request.density());
        if (request.uppercaseLevel() != null) pref.setUppercaseLevel(request.uppercaseLevel());
        if (request.showStreakBar()  != null) pref.setShowStreakBar(request.showStreakBar());
        if (request.defaultCefr()   != null) pref.setDefaultCefr(request.defaultCefr());
        if (request.lastActiveTab()  != null) pref.setLastActiveTab(request.lastActiveTab());

        userPreferenceRepository.save(pref);

        return Either.right(new UpdatePreferencesResponse(
                pref.getAccent(),
                pref.getDensity(),
                pref.getUppercaseLevel(),
                pref.getShowStreakBar(),
                pref.getDefaultCefr(),
                pref.getLastActiveTab()
        ));
    }

    private void validate(UpdatePreferencesRequest req, Notification n) {
        if (req.accent()         != null && !VALID_ACCENTS.contains(req.accent()))   n.append(PreferenceError.ACCENT_INVALID);
        if (req.density()        != null && !VALID_DENSITIES.contains(req.density())) n.append(PreferenceError.DENSITY_INVALID);
        if (req.uppercaseLevel() != null && !VALID_CAPS.contains(req.uppercaseLevel())) n.append(PreferenceError.UPPERCASE_LEVEL_INVALID);
        if (req.defaultCefr()   != null && !VALID_CEFR.contains(req.defaultCefr())) n.append(PreferenceError.CEFR_INVALID);
        if (req.lastActiveTab()  != null && !VALID_TABS.contains(req.lastActiveTab())) n.append(PreferenceError.TAB_INVALID);
    }
}
