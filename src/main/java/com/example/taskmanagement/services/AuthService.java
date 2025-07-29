package com.example.taskmanagement.services;

import com.example.taskmanagement.exceptions.AuthenticationException;
import com.example.taskmanagement.model.dto.requests.SignInRequestDto;
import com.example.taskmanagement.model.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final CognitoService cognitoService;


    public String signIn(SignInRequestDto request) {
        String loginIdentifier = request.getLoginIdentifier();

        AdminInitiateAuthResponse authResponse = cognitoService.authenticateUser(loginIdentifier, request.getPassword());

        if (authResponse.authenticationResult() != null)
            return authResponse.authenticationResult().idToken();
        if (authResponse.challengeName() == ChallengeNameType.NEW_PASSWORD_REQUIRED)
            throw new AuthenticationException(ErrorCode.PASSWORD_CHANGE_REQUIRED.getDescription(),ErrorCode.PASSWORD_CHANGE_REQUIRED);

        throw new AuthenticationException("Unexpected authentication error");
    }


    public String forcePasswordChange(SignInRequestDto request) {
        String loginIdentifier = request.getLoginIdentifier();

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty())
            throw new AuthenticationException("New password is required for password change");

        // First, authenticate with the current password to get the session
        AdminInitiateAuthResponse authResponse = cognitoService.authenticateUser(loginIdentifier, request.getPassword());

        if (authResponse.challengeName() == ChallengeNameType.NEW_PASSWORD_REQUIRED) {
            AdminRespondToAuthChallengeResponse challengeResponse = cognitoService.changePassword(
                    authResponse, loginIdentifier, request.getNewPassword()
            );

            if (challengeResponse.authenticationResult() != null)
                return challengeResponse.authenticationResult().idToken();

            throw new AuthenticationException("Failed to complete password change");
        } else {
            throw new AuthenticationException("Password change not required for this user");
        }
    }
}