package cz.syntaxbro.erpsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Role name cannot be empty")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )

    @JsonIgnore // Prevents serialization issues in JSON responses
    @ToString.Exclude // Prevents infinite recursion in logging
    private Set<Permission> permissions = new HashSet<>();

    // Constructor for easy creation of a role with permissions
    public Role(String name, Set<Permission> permissions) {
        this.name = name;
        this.permissions = new HashSet<>();
    }

    // Method for adding permissions to a role
    @Transactional
    public void addPermission(Permission permission) {
        if (this.permissions == null) {
            this.permissions = new HashSet<>();
        }
        this.permissions.add(permission);
    }

    // Method for removing permissions from a role
    @Transactional
    public void removePermission(Permission permission) {
        if (this.permissions != null) {
            this.permissions.remove(permission);
        }
    }
}