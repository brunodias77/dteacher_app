package com.dias.dteacher.usecase.auth.register;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.UserError;
import com.dias.dteacher.model.StudyStreak;
import com.dias.dteacher.model.User;
import com.dias.dteacher.model.UserPreference;
import com.dias.dteacher.repository.StudyStreakRepository;
import com.dias.dteacher.repository.UserPreferenceRepository;
import com.dias.dteacher.repository.UserRepository;
import com.dias.dteacher.validation.Notification;
import com.dias.dteacher.validator.UserValidator;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase implements UseCase<RegisterUserRequest, RegisterUserResponse> {

    private static final String EMAIL_REGEX  = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    private static final int    PASSWORD_MIN = 8;
    private static final int    PASSWORD_MAX = 255;
    private static final int    EMAIL_MAX    = 255;
    private static final int    NAME_MAX     = 100;

    private final UserRepository           userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final StudyStreakRepository    studyStreakRepository;
    private final PasswordEncoder          passwordEncoder;

    @Override
    @Transactional
    public Either<Notification, RegisterUserResponse> execute(RegisterUserRequest request) {
        Notification notification = Notification.create();

        validateRequest(request, notification);
        if (notification.hasError()) {
            return Either.left(notification);
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            return Either.left(Notification.create(UserError.EMAIL_ALREADY_EXISTS_CONFLICT));
        }

        User user = buildUser(request);
        new UserValidator(user, notification).validate();
        if (notification.hasError()) {
            return Either.left(notification);
        }

        userRepository.save(user);
        userPreferenceRepository.save(UserPreference.builder().user(user).build());
        studyStreakRepository.save(StudyStreak.builder().user(user).build());

        return Either.right(new RegisterUserResponse(user.getId(), user.getEmail(), user.getDisplayName()));
    }

    private void validateRequest(RegisterUserRequest req, Notification notification) {
        String email    = req.email();
        String password = req.password();
        String name     = req.name();

        if (email == null || email.isBlank()) {
            notification.append(UserError.EMAIL_REQUIRED);
        } else if (!email.matches(EMAIL_REGEX)) {
            notification.append(UserError.EMAIL_INVALID);
        } else if (email.length() > EMAIL_MAX) {
            notification.append(UserError.EMAIL_TOO_LONG);
        }

        if (password == null || password.isBlank()) {
            notification.append(UserError.PASSWORD_REQUIRED);
        } else {
            if (password.length() < PASSWORD_MIN) notification.append(UserError.PASSWORD_TOO_SHORT);
            if (password.length() > PASSWORD_MAX) notification.append(UserError.PASSWORD_TOO_LONG);
            if (!password.matches(".*\\d.*"))      notification.append(UserError.PASSWORD_NO_NUMBER);
        }

        if (password != null && !password.equals(req.passwordConfirmation())) {
            notification.append(UserError.PASSWORD_MISMATCH);
        }

        if (name != null && name.length() > NAME_MAX) {
            notification.append(UserError.DISPLAY_NAME_TOO_LONG);
        }
    }

    private User buildUser(RegisterUserRequest req) {
        return User.builder()
                .email(req.email().strip().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.password()))
                .displayName(req.name())
                .build();
    }
}
