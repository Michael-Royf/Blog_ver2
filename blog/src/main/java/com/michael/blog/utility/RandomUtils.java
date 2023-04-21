package com.michael.blog.utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomUtils {

    public String generateTokenForVerification() {
        return UUID.randomUUID().toString();
    }

    public String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public String generateUserID() {
        return RandomStringUtils.randomNumeric(10);
    }


}
