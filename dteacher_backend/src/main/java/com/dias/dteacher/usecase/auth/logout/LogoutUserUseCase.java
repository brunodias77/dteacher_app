package com.dias.dteacher.usecase.auth.logout;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import org.springframework.stereotype.Service;

@Service
public class LogoutUserUseCase implements UseCase<LogoutUserRequest, LogoutUserResponse> {

    @Override
    public Either<Notification, LogoutUserResponse> execute(LogoutUserRequest request) {
        return Either.right(new LogoutUserResponse());
    }
}
