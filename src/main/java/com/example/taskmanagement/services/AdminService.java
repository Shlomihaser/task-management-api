package com.example.taskmanagement.services;

import com.example.taskmanagement.model.dto.UserDto;

import java.util.List;

public interface AdminService {
    List<UserDto> listUsers();
    UserDto getUserById(String id);
    void deleteUserById(String id);
}
