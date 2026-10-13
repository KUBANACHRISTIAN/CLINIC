package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired private PatientService patientService;
    @Autowired private AppointmentService appointmentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> savePatient(@RequestBody Patient patient) {
        String msg = patientService.savePatient(patient);
        return msg.equals("Patient saved successfully")
                ? new ResponseEntity<>(msg, HttpStatus.CREATED)
                : new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getAllPatients() {
        return new ResponseEntity<>(patientService.getAllPatients(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id)
                .<ResponseEntity<?>>map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePatient(@PathVariable UUID id, @RequestBody Patient patient) {
        String msg = patientService.updatePatient(id, patient);
        return msg.equals("Patient updated successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable UUID id) {
        String msg = patientService.deletePatient(id);
        return msg.equals("Patient deleted successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchPatients(@RequestParam String firstName) {
        return new ResponseEntity<>(patientService.searchByFirstName(firstName), HttpStatus.OK);
    }

    @GetMapping(value = "/{ssNumber}/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUpcoming(@PathVariable String ssNumber) {
        if (!patientService.existsBySsNumber(ssNumber))
            return new ResponseEntity<>("That patient with that ssnumber does not exist", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(appointmentService.getUpcoming(ssNumber), HttpStatus.OK);
    }
}
