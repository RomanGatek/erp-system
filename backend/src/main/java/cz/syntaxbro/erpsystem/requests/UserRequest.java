package cz.syntaxbro.erpsystem.partials;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPartial {
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "First Name cannot be empty field")
    private String firstName;

    @NotEmpty(message = "Last Name cannot be empty field")
    private String lastName;

    private boolean isActive;

    private Set<String> roles;
    
    private String avatar;
}
