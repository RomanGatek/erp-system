package cz.syntaxbro.erpsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String companyName;

    @Email
    String email;

    String address;

    LocalDate calendarOrder;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    private List<Product> products;
}
