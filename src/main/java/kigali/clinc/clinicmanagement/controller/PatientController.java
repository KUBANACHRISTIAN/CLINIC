package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.DoctorService;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePatient(@RequestBody Patient patient) {
        String message = patientService.savePatient(patient);
        return message.equals("Patient saved successfully")
                ? new ResponseEntity<>(message, HttpStatus.CREATED)
                : new ResponseEntity<>(message, HttpStatus.CONFLICT);
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

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePatient(@PathVariable UUID id, @RequestBody Patient patient) {
        String message = patientService.updatePatient(id, patient);
        return message.equals("Patient updated successfully")
                ? new ResponseEntity<>(message, HttpStatus.OK)
                : new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchPatients(@RequestParam String firstName) {
        return new ResponseEntity<>(patientService.searchByFirstName(firstName), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePatient(@PathVariable UUID id) {
        String message = patientService.deletePatient(id);
        return message.equals("Patient deleted successfully")
                ? new ResponseEntity<>(message, HttpStatus.OK)
                : new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/by-last-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getPatientsByLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(patientService.getPatientsByLastName(lastName), HttpStatus.OK);
    }
}
