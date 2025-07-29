package com.example.taskmanagement.services;

import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.List;

public interface AdminService {
    List<UserType> listUsers();
    UserType getUserById(String id);
    void deleteUserById(String id);
}
