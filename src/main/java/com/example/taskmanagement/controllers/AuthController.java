package com.example.taskmanagement.controllers;

import com.example.taskmanagement.model.dto.requests.SignInRequestDto;
import com.example.taskmanagement.model.dto.response.SignInResponse;
import com.example.taskmanagement.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signin(@Valid @RequestBody SignInRequestDto signInRequestDto) {
        String idToken = authService.signIn(signInRequestDto);
        SignInResponse signInResponse = new SignInResponse(idToken);
        return ResponseEntity.ok(signInResponse);
    }

    @PostMapping("/force-password-change")
    public ResponseEntity<SignInResponse> forcePasswordChange(@Valid @RequestBody SignInRequestDto signInRequestDto){
        String idToken = authService.forcePasswordChange(signInRequestDto);
        SignInResponse signInResponse = new SignInResponse(idToken);
        return ResponseEntity.ok(signInResponse);
    }

}