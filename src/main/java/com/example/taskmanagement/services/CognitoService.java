package com.example.taskmanagement.services;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.List;
import java.util.Optional;

public interface CognitoService {
    AdminInitiateAuthResponse authenticateUser(String loginIdentifier, String password);
    AdminRespondToAuthChallengeResponse changePassword(AdminInitiateAuthResponse authResponse, String loginIdentifier, String newPassword);
    List<UserType> listUsers();
    Optional<UserType> getUserBySub(String sub);
    void deleteUserByUsername(String username);
}