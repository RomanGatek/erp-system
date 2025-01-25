package cz.syntaxbro.erpsystem.models.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActive;
    private Set<String> roles;
}