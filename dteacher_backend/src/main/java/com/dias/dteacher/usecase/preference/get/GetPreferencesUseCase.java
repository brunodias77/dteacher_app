package com.dias.dteacher.usecase.preference.get;

import com.dias.dteacher.contract.UseCase;
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

@Service
@RequiredArgsConstructor
public class GetPreferencesUseCase implements UseCase<GetPreferencesRequest, GetPreferencesResponse> {

    private final UserRepository           userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    @Override
    @Transactional(readOnly = true)
    public Either<Notification, GetPreferencesResponse> execute(GetPreferencesRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> NotFoundException.of("User", request.email()));

        UserPreference pref = userPreferenceRepository.findById(user.getId())
                .orElseGet(() -> UserPreference.builder().user(user).build());

        return Either.right(new GetPreferencesResponse(
                pref.getAccent(),
                pref.getDensity(),
                pref.getUppercaseLevel(),
                pref.getShowStreakBar(),
                pref.getDefaultCefr(),
                pref.getLastActiveTab()
        ));
    }
}
