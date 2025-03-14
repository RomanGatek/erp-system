package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("select s from Supplier s where s.companyName = :companyName")
    Optional<Supplier> findByCompanyName(@Param("companyName") String companyName);
}
