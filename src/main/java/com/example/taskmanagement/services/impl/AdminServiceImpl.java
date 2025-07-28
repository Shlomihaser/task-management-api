package com.example.taskmanagement.services.impl;

import com.example.taskmanagement.exceptions.UserNotFoundException;
import com.example.taskmanagement.model.dto.UserDto;
import com.example.taskmanagement.services.AdminService;
import com.example.taskmanagement.services.CognitoService;
import com.example.taskmanagement.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CognitoService cognitoService;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> listUsers() {
        return cognitoService.listUsers().stream()
                .map(user -> userMapper.mapCognitoUserToDto(user))
                .toList();
    }

    @Override
    public UserDto getUserById(String id) {
        UserType user = cognitoService.getUserBySub(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        return userMapper.mapCognitoUserToDto(user, id);
    }

    @Override
    public void deleteUserById(String id) {
        UserType user = cognitoService.getUserBySub(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        cognitoService.deleteUserByUsername(user.username());
    }
}
