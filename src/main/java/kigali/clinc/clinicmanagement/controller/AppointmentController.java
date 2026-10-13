package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired private AppointmentService appointmentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveAppointment(@RequestBody Appointment appointment) {
        String msg = appointmentService.saveAppointment(appointment);
        if (msg.equals("Appointment saved successfully")) return new ResponseEntity<>(msg, HttpStatus.CREATED);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
                .<ResponseEntity<?>>map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                .orElse(new ResponseEntity<>("The appointment with that id does not exist", HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment) {
        String msg = appointmentService.updateAppointment(id, appointment);
        return msg.equals("Appointment updated successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable UUID id) {
        String msg = appointmentService.deleteAppointment(id);
        if (msg.equals("Appointment deleted successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("That appointment has prescriptions, delete them first")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @PatchMapping(value = "/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam AppointmentStatus status) {
        String msg = appointmentService.updateStatus(id, status);
        if (msg.equals("Appointment status updated successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("The appointment with that id does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = "/{id}/reschedule")
    public ResponseEntity<String> reschedule(@PathVariable UUID id, @RequestParam String newDate) {
        String msg = appointmentService.reschedule(id, LocalDate.parse(newDate));
        if (msg.equals("Appointment rescheduled successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("The appointment with that id does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.equals("Doctor is already booked on that date")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }
}
