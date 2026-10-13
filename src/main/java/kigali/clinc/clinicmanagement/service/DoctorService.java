package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
@Transactional
public class DoctorService {

    @Autowired private DoctorRepository doctorRepo;
    @Autowired private SpecializationRepository specializationRepo;
    @Autowired private OfficeRepository officeRepo;
    @Autowired private AppointmentRepository appointmentRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctorRepo.existsByFirstNameAndLastName(doctor.getFirstName(), doctor.getLastName()))
            return "Doctor already exists";
        doctorRepo.save(doctor);
        return "Doctor saved successfully";
    }

    public List<Doctor> getAllDoctors() { return doctorRepo.findAll(); }

    public Optional<Doctor> getDoctorById(UUID id) { return doctorRepo.findById(id); }

    public String updateDoctor(UUID id, Doctor updated) {
        return doctorRepo.findById(id).map(doctor -> {
            doctor.setFirstName(updated.getFirstName());
            doctor.setLastName(updated.getLastName());
            doctor.setDateOfBirth(updated.getDateOfBirth());
            doctorRepo.save(doctor);
            return "Doctor updated successfully";
        }).orElse("The doctor with that id does not exist");
    }

    public String deleteDoctor(UUID id) {
        if (!doctorRepo.existsById(id)) return "The doctor with that id does not exist";
        doctorRepo.deleteById(id);
        return "Doctor deleted successfully";
    }

    // A1 - add specialization to doctor
    public String addSpecialization(UUID doctorId, UUID specializationId) {
        return doctorRepo.findById(doctorId).map(doctor ->
            specializationRepo.findById(specializationId).map(spec -> {
                if (doctor.getSpecializations().contains(spec))
                    return "The doctor already has that specialization";
                doctor.getSpecializations().add(spec);
                doctorRepo.save(doctor);
                return "Specialization added to the doctor";
            }).orElse("That specialization does not exist")
        ).orElse("The doctor with that id does not exist");
    }

    // A2 - remove specialization from doctor
    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        return doctorRepo.findById(doctorId).map(doctor ->
            specializationRepo.findById(specializationId).map(spec -> {
                if (!doctor.getSpecializations().contains(spec))
                    return "The doctor does not have that specialization";
                doctor.getSpecializations().remove(spec);
                doctorRepo.save(doctor);
                return "Specialization removed from the doctor";
            }).orElse("That specialization does not exist")
        ).orElse("The doctor with that id does not exist");
    }

    // A4 - move doctor to another office
    public String moveToOffice(UUID doctorId, int officeNumber) {
        return doctorRepo.findById(doctorId).map(doctor ->
            officeRepo.findByOfficeNumber(officeNumber).map(office -> {
                boolean officeOccupied = doctorRepo.existsByOfficeOfficeNumber(officeNumber);
                boolean isSameOffice = doctor.getOffice() != null && doctor.getOffice().getOfficeNumber() == officeNumber;
                if (officeOccupied && !isSameOffice)
                    return "That office is taken by other doctor";
                doctor.setOffice(office);
                doctorRepo.save(doctor);
                return "Office changed successfully";
            }).orElse("The office with that office number does not exist")
        ).orElse("The doctor with that id does not exist");
    }

    // D1 - doctor's agenda for a day (no CANCELLED)
    public List<Appointment> getAgenda(UUID doctorId, LocalDate date) {
        return appointmentRepo.findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED);
    }

    // D3 - search doctors by name (JPQL)
    public List<Doctor> searchByName(String name) {
        return doctorRepo.searchByName(name);
    }

    // Bonus workload
    public List<Object[]> getWorkload(LocalDate start, LocalDate end) {
        return appointmentRepo.findDoctorWorkload(start, end);
    }
}
