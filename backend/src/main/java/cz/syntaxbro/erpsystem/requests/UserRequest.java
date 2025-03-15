package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotEmpty(message = "First Name cannot be empty field")
    private String firstName;

    @NotEmpty(message = "Last Name cannot be empty field")
    private String lastName;

    private boolean isActive;

    private Set<String> roles;
    
    private String avatar;
}
