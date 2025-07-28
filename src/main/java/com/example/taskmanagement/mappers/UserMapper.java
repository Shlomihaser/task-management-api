package com.example.taskmanagement.mappers;

import com.example.taskmanagement.model.dto.UserDto;
import com.example.taskmanagement.utils.CognitoUtils;
import com.example.taskmanagement.utils.DateUtils;
import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

@UtilityClass
public class UserMapper {

    public UserDto mapCognitoUserToDto(UserType user) {
        String id = CognitoUtils.extractAttributeValue(user, "sub");
        return mapCognitoUserToDto(user, id);
    }

    public  UserDto mapCognitoUserToDto(UserType user, String id) {
        String email = CognitoUtils.extractAttributeValue(user, "email");
        String createdAt = DateUtils.format(user.userCreateDate());

        return new UserDto(
                id,
                email,
                user.username(),
                createdAt,
                user.userStatus().toString(),
                String.valueOf(user.enabled())
        );
    }
} 