package com.example.taskmanagement.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String createdAt;
    private String status;
    private String isEnabled;
}
