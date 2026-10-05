package kigali.clinc.clinicmanagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public String saveSpecialization(Specialization specialization) {
        resolveDoctors(specialization);
        specializationRepo.save(specialization);
        return "Specialization saved successfully";
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepo.findAll();
    }

    public Optional<Specialization> getSpecializationById(UUID id) {
        return specializationRepo.findById(id);
    }

    public String updateSpecialization(UUID id, Specialization updated) {
        return specializationRepo.findById(id).map(specialization -> {
            specialization.setName(updated.getName());
            resolveDoctors(updated);
            specialization.setDoctors(updated.getDoctors());
            specializationRepo.save(specialization);
            return "Specialization updated successfully";
        }).orElse("Specialization not found");
    }

    public String deleteSpecialization(UUID id) {
        if (!specializationRepo.existsById(id)) {
            return "Specialization not found";
        }
        specializationRepo.deleteById(id);
        return "Specialization deleted successfully";
    }

    public List<Specialization> getUnusedSpecializations() {
        return specializationRepo.findUnusedSpecializations();
    }

    private void resolveDoctors(Specialization specialization) {
        List<Doctor> doctors = specialization.getDoctors();
        if (doctors == null || doctors.isEmpty()) {
            return;
        }
        List<Doctor> resolved = new ArrayList<>();
        for (Doctor doctor : doctors) {
            doctorRepo.findById(doctor.getId()).ifPresent(resolved::add);
        }
        specialization.setDoctors(resolved);
    }
}
