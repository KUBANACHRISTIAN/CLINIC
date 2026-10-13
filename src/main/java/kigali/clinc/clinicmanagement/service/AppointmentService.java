package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private PrescriptionRepository prescriptionRepo;

    // B1 - safe booking: past date blocked, status always SCHEDULED
    public String saveAppointment(Appointment appointment) {
        if (appointment.getAppointmentDate().isBefore(LocalDate.now()))
            return "Appointment date cannot be in the past";
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment saved successfully";
    }

    public List<Appointment> getAllAppointments() { return appointmentRepo.findAll(); }

    public Optional<Appointment> getAppointmentById(UUID id) { return appointmentRepo.findById(id); }

    // General update — does NOT change status (status only via B2)
    public String updateAppointment(UUID id, Appointment updated) {
        if (updated.getAppointmentDate() != null && updated.getAppointmentDate().isBefore(LocalDate.now()))
            return "Appointment date cannot be in the past";
        return appointmentRepo.findById(id).map(a -> {
            a.setAppointmentDate(updated.getAppointmentDate());
            a.setReason(updated.getReason());
            a.setDoctor(updated.getDoctor());
            a.setPatient(updated.getPatient());
            appointmentRepo.save(a);
            return "Appointment updated successfully";
        }).orElse("The appointment with that id does not exist");
    }

    // C4 - delete blocked if prescriptions exist
    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) return "The appointment with that id does not exist";
        if (prescriptionRepo.existsByAppointmentId(id))
            return "That appointment has prescriptions, delete them first";
        appointmentRepo.deleteById(id);
        return "Appointment deleted successfully";
    }

    // B2 - status flow enforcement
    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        return appointmentRepo.findById(id).map(a -> {
            AppointmentStatus current = a.getStatus();
            boolean allowed = (current == AppointmentStatus.SCHEDULED &&
                                (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED))
                           || (current == AppointmentStatus.CONFIRMED &&
                                (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED));
            if (!allowed)
                return "Cannot change status from " + current + " to " + newStatus;
            a.setStatus(newStatus);
            appointmentRepo.save(a);
            return "Appointment status updated successfully";
        }).orElse("The appointment with that id does not exist");
    }

    // B3 - reschedule with all business rules
    public String reschedule(UUID id, LocalDate newDate) {
        return appointmentRepo.findById(id).map(a -> {
            if (a.getStatus() == AppointmentStatus.COMPLETED)
                return "A COMPLETED appointment cannot be rescheduled";
            if (a.getStatus() == AppointmentStatus.CANCELLED)
                return "A CANCELLED appointment cannot be rescheduled";
            if (newDate.isBefore(LocalDate.now()))
                return "Appointment date cannot be in the past";
            if (appointmentRepo.existsByDoctorAndAppointmentDateAndStatusNotAndIdNot(
                    a.getDoctor(), newDate, AppointmentStatus.CANCELLED, id))
                return "Doctor is already booked on that date";
            a.setAppointmentDate(newDate);
            a.setStatus(AppointmentStatus.SCHEDULED);
            appointmentRepo.save(a);
            return "Appointment rescheduled successfully";
        }).orElse("The appointment with that id does not exist");
    }

    // D2 - upcoming appointments of a patient
    public List<Appointment> getUpcoming(String ssNumber) {
        return appointmentRepo
            .findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                ssNumber, LocalDate.now(), List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
    }
}
