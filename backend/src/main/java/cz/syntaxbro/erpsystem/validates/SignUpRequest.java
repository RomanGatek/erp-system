package cz.syntaxbro.erpsystem.validates;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "Username is required")
    @Size(min =5, max = 50, message = "Username muset be between 5 and 50 characters")
    private String username;

    @NotBlank
    @Size(min = 10, message = "Password must be at least 10 characters")
    private String password;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
}