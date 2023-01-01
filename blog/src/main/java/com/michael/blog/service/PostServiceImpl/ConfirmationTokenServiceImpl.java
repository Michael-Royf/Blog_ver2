package com.michael.blog.service.PostServiceImpl;

import com.michael.blog.entity.ConfirmationToken;
import com.michael.blog.repository.ConfirmationTokenRepository;
import com.michael.blog.service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }
}
