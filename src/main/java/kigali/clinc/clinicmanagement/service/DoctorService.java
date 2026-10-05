package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctorRepo.existsByFirstNameAndLastName(doctor.getFirstName(), doctor.getLastName())) {
            return "Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " already exists";
        }
        doctorRepo.save(doctor);
        return "Doctor saved successfully";
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return doctorRepo.findById(id);
    }

    public String updateDoctor(UUID id, Doctor updated) {
        return doctorRepo.findById(id).map(doctor -> {
            doctor.setFirstName(updated.getFirstName());
            doctor.setLastName(updated.getLastName());
            doctor.setDateOfBirth(updated.getDateOfBirth());
            doctor.setOffice(updated.getOffice());
            doctorRepo.save(doctor);
            return "Doctor updated successfully";
        }).orElse("Doctor not found");
    }

    public List<Doctor> searchByFirstName(String firstName) {
        return doctorRepo.findByFirstNameContainingIgnoreCase(firstName);
    }

    public String deleteDoctor(UUID id) {
        if (!doctorRepo.existsById(id)) {
            return "Doctor not found";
        }
        doctorRepo.deleteById(id);
        return "Doctor deleted successfully";
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepo.findBySpecializationName(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }
}
