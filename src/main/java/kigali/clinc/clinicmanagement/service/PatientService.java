package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    public String savePatient(Patient patient) {
        if (patientRepo.existsByFirstNameAndLastName(patient.getFirstName(), patient.getLastName())) {
            return "Patient " + patient.getFirstName() + " " + patient.getLastName() + " already exists";
        }
        patientRepo.save(patient);
        return "Patient saved successfully";
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patientRepo.findById(id);
    }

    public String updatePatient(UUID id, Patient updated) {
        return patientRepo.findById(id).map(patient -> {
            patient.setFirstName(updated.getFirstName());
            patient.setLastName(updated.getLastName());
            patient.setDateOfBirth(updated.getDateOfBirth());
            patientRepo.save(patient);
            return "Patient updated successfully";
        }).orElse("Patient not found");
    }

    public List<Patient> searchByFirstName(String firstName) {
        return patientRepo.findByFirstNameContainingIgnoreCase(firstName);
    }

    public String deletePatient(UUID id) {
        if (!patientRepo.existsById(id)) {
            return "Patient not found";
        }
        patientRepo.deleteById(id);
        return "Patient deleted successfully";
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public List<Patient> getPatientsOfDoctor(UUID doctorId) {
        return patientRepo.findPatientsByDoctorId(doctorId);
    }
}
