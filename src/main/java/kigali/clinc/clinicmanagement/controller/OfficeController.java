package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;
@CrossOrigin(origins = "http://localhost:5173") 
@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    @Autowired private OfficeService officeService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveOffice(@RequestBody Office office) {
        String msg = officeService.saveOffice(office);
        return msg.equals("Office saved successfully")
                ? new ResponseEntity<>(msg, HttpStatus.CREATED)
                : new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getAllOffices() {
        return new ResponseEntity<>(officeService.getAllOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {
        return officeService.getOfficeById(id)
                .<ResponseEntity<?>>map(o -> new ResponseEntity<>(o, HttpStatus.OK))
                .orElse(new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateOffice(@PathVariable UUID id, @RequestBody Office office) {
        String msg = officeService.updateOffice(id, office);
        return msg.equals("Office updated successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteOffice(@PathVariable UUID id) {
        String msg = officeService.deleteOffice(id);
        return msg.equals("Office deleted successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchOffices(@RequestParam String name) {
        return new ResponseEntity<>(officeService.searchByName(name), HttpStatus.OK);
    }

    @GetMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getFreeOffices() {
        return new ResponseEntity<>(officeService.getFreeOffices(), HttpStatus.OK);
    }
}
