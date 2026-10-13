package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.service.SpecializationService;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    @Autowired private SpecializationService specializationService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveSpecialization(@RequestBody Specialization specialization) {
        String msg = specializationService.saveSpecialization(specialization);
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return new ResponseEntity<>(specializationService.getAllSpecializations(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSpecializationById(@PathVariable UUID id) {
        return specializationService.getSpecializationById(id)
                .<ResponseEntity<?>>map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElse(new ResponseEntity<>("Specialization not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSpecialization(@PathVariable UUID id, @RequestBody Specialization specialization) {
        String msg = specializationService.updateSpecialization(id, specialization);
        return msg.equals("Specialization updated successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteSpecialization(@PathVariable UUID id) {
        String msg = specializationService.deleteSpecialization(id);
        return msg.equals("Specialization deleted successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/unused", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Specialization>> getUnusedSpecializations() {
        return new ResponseEntity<>(specializationService.getUnusedSpecializations(), HttpStatus.OK);
    }
}
