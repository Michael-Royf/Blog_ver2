package com.michael.blog.utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.passay.DictionarySubstringRule.ERROR_CODE;

@Component
public class RandomUtils {

    public String generateTokenForVerification() {
        return UUID.randomUUID().toString();
    }

    public String generateUserID() {
        return RandomStringUtils.randomNumeric(10);
    }


    public String generatePassword() {
        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(1);
        List<CharacterRule> rules = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                splCharRule
        );
        return new PasswordGenerator().generatePassword(12, rules);
    }

}

