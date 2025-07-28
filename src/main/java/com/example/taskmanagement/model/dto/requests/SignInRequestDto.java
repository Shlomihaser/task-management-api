package com.example.taskmanagement.model.dto.requests;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequestDto {

    private String username;
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String newPassword; // Optional field for password change

    @AssertTrue(message = "Either username or email is required")
    public boolean isUsernameOrEmailProvided() {
        return (username != null && !username.trim().isEmpty()) ||
                (email != null && !email.trim().isEmpty());
    }

    public String getLoginIdentifier() {
        if (email != null && !email.trim().isEmpty())
            return email;

        if (username != null && !username.trim().isEmpty())
            return username;

        return null;
    }

    public boolean isValid() {
        return (username != null && !username.trim().isEmpty()) ||
                (email != null && !email.trim().isEmpty());
    }
}
