package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Supplier;
import cz.syntaxbro.erpsystem.repositories.SupplierRepository;
import cz.syntaxbro.erpsystem.services.SupplierService;
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
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplier(long id, Supplier updatedSupplier) {
        Supplier savedSupplier = getSupplierById(id);
        mappedSupplier(savedSupplier, updatedSupplier);
        return supplierRepository.save(updatedSupplier);
    }

    private Supplier mappedSupplier(Supplier savedSupplier, Supplier updatedSupplier) {
        savedSupplier.setCompanyName(updatedSupplier.getCompanyName());
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
