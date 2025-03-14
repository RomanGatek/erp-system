package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
