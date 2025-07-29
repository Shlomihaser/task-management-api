package com.example.taskmanagement.services.impl;

import com.example.taskmanagement.exceptions.UserNotFoundException;
import com.example.taskmanagement.services.AdminService;
import com.example.taskmanagement.services.CognitoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CognitoService cognitoService;

    @Override
    public List<UserType> listUsers() {
        return cognitoService.listUsers();
    }

    @Override
    public UserType getUserById(String id) {
        return cognitoService.getUserBySub(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public void deleteUserById(String id) {
        UserType user = cognitoService.getUserBySub(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        cognitoService.deleteUserByUsername(user.username());
    }
}
