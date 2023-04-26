package com.michael.blog.validation;

import com.michael.blog.payload.request.PasswordChangeRequest;
import com.michael.blog.payload.request.UserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserRequest) {
            UserRequest userRequest = (UserRequest) obj;
            return userRequest.getPassword().equals(userRequest.getMatchingPassword());
        } else if (obj instanceof PasswordChangeRequest) {
            PasswordChangeRequest passwordChangeRequest = (PasswordChangeRequest) obj;
            return passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getMatchingPassword());
        } else
            return false;
    }
}
