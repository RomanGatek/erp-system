package cz.syntaxbro.erpsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Email
    String email;

    String address;

    @Column(nullable = true)
    LocalDate calendarOrder;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Product> products;
}
