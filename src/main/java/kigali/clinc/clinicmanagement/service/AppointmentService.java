package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String saveAppointment(Appointment appointment) {
        if (appointmentRepo.existsByDoctorAndAppointmentDateAndStatusNot(
                appointment.getDoctor(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }
        appointmentRepo.save(appointment);
        return "Appointment saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepo.findById(id);
    }

    public String updateAppointment(UUID id, Appointment updated) {
        return appointmentRepo.findById(id).map(appointment -> {
            appointment.setAppointmentDate(updated.getAppointmentDate());
            appointment.setReason(updated.getReason());
            appointment.setStatus(updated.getStatus());
            appointment.setDoctor(updated.getDoctor());
            appointment.setPatient(updated.getPatient());
            appointmentRepo.save(appointment);
            return "Appointment updated successfully";
        }).orElse("Appointment not found");
    }

    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found";
        }
        appointmentRepo.deleteById(id);
        return "Appointment deleted successfully";
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }
}
