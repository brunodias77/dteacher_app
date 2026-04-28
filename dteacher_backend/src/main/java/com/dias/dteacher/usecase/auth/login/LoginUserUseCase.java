package com.dias.dteacher.usecase.auth.login;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.UserError;
import com.dias.dteacher.model.User;
import com.dias.dteacher.repository.UserRepository;
import com.dias.dteacher.service.JwtService;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserUseCase implements UseCase<LoginUserRequest, LoginUserResponse> {

    private final UserRepository     userRepository;
    private final PasswordEncoder    passwordEncoder;
    private final JwtService         jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Either<Notification, LoginUserResponse> execute(LoginUserRequest request) {
        if (request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return Either.left(Notification.create(UserError.INVALID_CREDENTIALS));
        }

        User user = userRepository.findByEmail(request.email().strip().toLowerCase())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return Either.left(Notification.create(UserError.INVALID_CREDENTIALS));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token   = jwtService.generateToken(userDetails);
        long expiresIn = jwtService.getExpirationMs() / 1000;

        return Either.right(new LoginUserResponse(
                token,
                expiresIn,
                new LoginUserResponse.UserInfo(user.getId(), user.getDisplayName(), user.getEmail())
        ));
    }
}
