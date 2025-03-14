package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Supplier;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SupplierService {

    Supplier getSupplierById(long id);

    Supplier getSupplierByName(String name);

    List<Supplier> getSuppliers();

    Supplier createSupplier(Supplier supplier);

    Supplier updateSupplier(long id, Supplier supplier);

    void deleteSupplier(long id);

}
