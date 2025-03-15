package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Supplier;
import cz.syntaxbro.erpsystem.repositories.SupplierRepository;
import cz.syntaxbro.erpsystem.services.SupplierService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Supplier getSupplierById(long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    @Override
    public Supplier getSupplierByName(String name) {
        return supplierRepository.findByCompanyName("name").orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    @Override
    public List<Supplier> getSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        validateIfCompanyNameExists(supplier.getName());
        validateIfEmailExists(supplier.getEmail());
        return supplierRepository.save(supplier);
    }

    private void validateIfCompanyNameExists(String companyName) {
        if(supplierRepository.findByCompanyName(companyName).isPresent()) {
            throw new EntityExistsException("Supplier with name "+companyName+" already exists");
        }
    }

    private void validateIfEmailExists(String email) {
        if(supplierRepository.findByEmail(email).isPresent()) {
            throw new EntityExistsException("Supplier with email "+email+" already exists");
        }
    }

    @Override
    public Supplier updateSupplier(long id, Supplier updatedSupplier) {
        validateIfCompanyNameExists(updatedSupplier.getName());
        validateIfEmailExists(updatedSupplier.getEmail());
        return supplierRepository.save(mappedSupplier(id, updatedSupplier));
    }

    private Supplier mappedSupplier(long id, Supplier updatedSupplier) {
        Supplier savedSupplier = getSupplierById(id);
        savedSupplier.setName(updatedSupplier.getName());
        savedSupplier.setAddress(updatedSupplier.getAddress());
        savedSupplier.setEmail(updatedSupplier.getEmail());
        savedSupplier.setCalendarOrder(updatedSupplier.getCalendarOrder());
        return savedSupplier;
    }

    @Override
    public void deleteSupplier(long id) {
        Supplier savedSupplier = getSupplierById(id);
        supplierRepository.delete(savedSupplier);
    }
}
