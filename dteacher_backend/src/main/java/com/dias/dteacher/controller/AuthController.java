package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.auth.login.LoginUserRequest;
import com.dias.dteacher.usecase.auth.login.LoginUserUseCase;
import com.dias.dteacher.usecase.auth.register.RegisterUserRequest;
import com.dias.dteacher.usecase.auth.register.RegisterUserUseCase;
import com.dias.dteacher.validation.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase    loginUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {
        return registerUserUseCase.execute(request).fold(
                this::handleError,
                body -> ResponseEntity.status(HttpStatus.CREATED).body(body)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserRequest request) {
        return loginUserUseCase.execute(request).fold(
                n -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(n),
                body -> ResponseEntity.ok(body)
        );
    }

    private ResponseEntity<Notification> handleError(Notification notification) {
        boolean isConflict = notification.getErrors().stream()
                .anyMatch(e -> e.message().equals("E-mail já cadastrado"));

        HttpStatus status = isConflict ? HttpStatus.CONFLICT : HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(notification);
    }
}
