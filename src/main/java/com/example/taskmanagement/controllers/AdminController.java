package com.example.taskmanagement.controllers;

import com.example.taskmanagement.model.dto.UserDto;
import com.example.taskmanagement.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = adminService.listUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable String id) {
        UserDto user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(
            @PathVariable String id) {
        adminService.deleteUserById(id);
        return ResponseEntity.ok("Deleted user with id : " + id + " successfully!");
    }

} 