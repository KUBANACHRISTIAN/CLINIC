package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment) {
        String message = appointmentService.saveAppointment(appointment);
        if (message.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
                .<ResponseEntity<?>>map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                .orElse(new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment) {
        String message = appointmentService.updateAppointment(id, appointment);
        return message.equals("Appointment updated successfully")
                ? new ResponseEntity<>(message, HttpStatus.OK)
                : new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        String message = appointmentService.deleteAppointment(id);
        return message.equals("Appointment deleted successfully")
                ? new ResponseEntity<>(message, HttpStatus.OK)
                : new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    @GetMapping(value = "/between", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getAppointmentsBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(appointmentService.getAppointmentsBetween(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping(value = "/stats/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> getAppointmentStatsByStatus() {
        return new ResponseEntity<>(appointmentService.getAppointmentStatsByStatus(), HttpStatus.OK);
    }

    @PatchMapping(value = "/cancel-day", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cancelDoctorAppointmentsOnDate(@RequestParam UUID doctorId, @RequestParam String date) {
        LocalDate day = LocalDate.parse(date);
        return new ResponseEntity<>(appointmentService.cancelDoctorAppointmentsOnDate(doctorId, day), HttpStatus.OK);
    }
}
