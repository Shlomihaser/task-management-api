package com.example.taskmanagement.services;

import com.example.taskmanagement.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CognitoService cognitoService;

    public List<UserType> listUsers() {
        return cognitoService.listUsers();
    }

    public UserType getUserById(String id) {
        return cognitoService.getUserBySub(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    public void deleteUserById(String id) {
        UserType user = cognitoService.getUserBySub(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        cognitoService.deleteUserByUsername(user.username());
    }
} 