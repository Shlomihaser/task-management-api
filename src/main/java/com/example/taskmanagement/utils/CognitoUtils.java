package com.example.taskmanagement.utils;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

@UtilityClass
public class CognitoUtils {

    public static String extractAttributeValue(UserType user, String attributeName) {
        return user.attributes().stream()
                .filter(attr -> attr.name().equals(attributeName))
                .findFirst()
                .map(AttributeType::value)
                .orElse(null);
    }
} 