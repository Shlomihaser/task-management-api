package com.example.taskmanagement.services.impl;


import com.example.taskmanagement.exceptions.InternalErrorException;

import com.example.taskmanagement.services.CognitoService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
@RequiredArgsConstructor
public class CognitoServiceImpl implements CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.user-pool-id}")
    private String userPoolId;
    @Value("${aws.cognito.client-id}")
    private String clientId;
    @Value("${aws.cognito.client-secret}")
    private String clientSecret;

    @Override
    public AdminInitiateAuthResponse authenticateUser(String loginIdentifier, String password) {
        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .authParameters(Map.of(
                        "USERNAME", loginIdentifier,
                        "PASSWORD", password,
                        "SECRET_HASH", calculateSecretHash(loginIdentifier)
                ))
                .build();

        return cognitoClient.adminInitiateAuth(authRequest);
    }

    @Override
    public AdminRespondToAuthChallengeResponse changePassword(AdminInitiateAuthResponse authResponse, String loginIdentifier, String newPassword) {
        Map<String, String> challengeResponses = new HashMap<>();
        challengeResponses.put("USERNAME", loginIdentifier);
        challengeResponses.put("NEW_PASSWORD", newPassword);
        challengeResponses.put("SECRET_HASH", calculateSecretHash(loginIdentifier));

        AdminRespondToAuthChallengeRequest challengeRequest = AdminRespondToAuthChallengeRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .challengeResponses(challengeResponses)
                .session(authResponse.session())
                .build();

        return cognitoClient.adminRespondToAuthChallenge(challengeRequest);
    }

    private String calculateSecretHash(String username) {
        try {
            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new InternalErrorException("Failed to calculate secret hash",e);
        }
    }

    @Override
    public List<UserType> listUsers() {
        try {
            ListUsersRequest request = ListUsersRequest.builder()
                    .userPoolId(userPoolId)
                    .build();

            ListUsersResponse response = cognitoClient.listUsers(request);
            return response.users();
        } catch (Exception e) {
            throw new InternalErrorException("Failed to list users", e);
        }
    }

    @Override
    public Optional<UserType> getUserBySub(String sub) {
        ListUsersRequest request = ListUsersRequest.builder()
                .userPoolId(userPoolId)
                .filter(String.format("sub = \"%s\"", sub))
                .limit(1)
                .build();

        ListUsersResponse response = cognitoClient.listUsers(request);

        if (response.users().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(response.users().get(0));
    }

    public void deleteUserByUsername(String username) {
        AdminDeleteUserRequest request = AdminDeleteUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .build();

        cognitoClient.adminDeleteUser(request);
    }
}