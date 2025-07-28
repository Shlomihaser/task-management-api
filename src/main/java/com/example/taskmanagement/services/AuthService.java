package com.example.taskmanagement.services;


import com.example.taskmanagement.model.dto.requests.SignInRequestDto;

public interface AuthService {
    String signIn(SignInRequestDto request);
    String forcePasswordChange(SignInRequestDto request);
}
