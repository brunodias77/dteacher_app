package com.dias.dteacher.validator;

import com.dias.dteacher.error.UserError;
import com.dias.dteacher.model.User;
import com.dias.dteacher.validation.Notification;
import com.dias.dteacher.validation.Validator;

public class UserValidator extends Validator {

    private static final int EMAIL_MAX_LENGTH        = 255;
    private static final int PASSWORD_MIN_LENGTH     = 8;
    private static final int PASSWORD_MAX_LENGTH     = 255;
    private static final int DISPLAY_NAME_MAX_LENGTH = 100;
    private static final String EMAIL_REGEX          = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final User user;

    public UserValidator(final User user, final Notification notification) {
        super(notification);
        this.user = user;
    }

    @Override
    public void validate() {
        validateEmail();
        validatePassword();
        validateDisplayName();
    }

    private void validateEmail() {
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            validationHandler().append(UserError.EMAIL_REQUIRED);
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            validationHandler().append(UserError.EMAIL_INVALID);
        }
        if (email.length() > EMAIL_MAX_LENGTH) {
            validationHandler().append(UserError.EMAIL_TOO_LONG);
        }
    }

    private void validatePassword() {
        String password = user.getPasswordHash();
        if (password == null || password.isBlank()) {
            validationHandler().append(UserError.PASSWORD_REQUIRED);
            return;
        }
        if (password.length() < PASSWORD_MIN_LENGTH) {
            validationHandler().append(UserError.PASSWORD_TOO_SHORT);
        }
        if (password.length() > PASSWORD_MAX_LENGTH) {
            validationHandler().append(UserError.PASSWORD_TOO_LONG);
        }
    }

    private void validateDisplayName() {
        String displayName = user.getDisplayName();
        if (displayName != null && displayName.length() > DISPLAY_NAME_MAX_LENGTH) {
            validationHandler().append(UserError.DISPLAY_NAME_TOO_LONG);
        }
    }
}
