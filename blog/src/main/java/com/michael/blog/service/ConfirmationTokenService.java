package com.michael.blog.service;

import com.michael.blog.entity.ConfirmationToken;

public interface ConfirmationTokenService {

    void saveConfirmationToken(ConfirmationToken token);
}
