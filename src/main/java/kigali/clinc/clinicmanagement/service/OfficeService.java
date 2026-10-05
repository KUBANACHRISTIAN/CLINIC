package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    public String saveOffice(Office office) {
        if (officeRepo.existsByOfficeNumber(office.getOfficeNumber())) {
            return "Office with number " + office.getOfficeNumber() + " already exists";
        }
        officeRepo.save(office);
        return "Office saved successfully";
    }

    public List<Office> getAllOffices() {
        return officeRepo.findAll();
    }

    public Optional<Office> getOfficeById(UUID id) {
        return officeRepo.findById(id);
    }

    public String updateOffice(UUID id, Office updated) {
        return officeRepo.findById(id).map(office -> {
            office.setName(updated.getName());
            office.setOfficeNumber(updated.getOfficeNumber());
            officeRepo.save(office);
            return "Office updated successfully";
        }).orElse("Office not found");
    }

    public List<Office> searchByName(String name) {
        return officeRepo.findByNameContainingIgnoreCase(name);
    }

    public String deleteOffice(UUID id) {
        if (!officeRepo.existsById(id)) {
            return "Office not found";
        }
        officeRepo.deleteById(id);
        return "Office deleted successfully";
    }
}
