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
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired private DoctorService doctorService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveDoctor(@RequestBody Doctor doctor) {
        String msg = doctorService.saveDoctor(doctor);
        return msg.equals("Doctor saved successfully")
                ? new ResponseEntity<>(msg, HttpStatus.CREATED)
                : new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .<ResponseEntity<?>>map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                .orElse(new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor) {
        String msg = doctorService.updateDoctor(id, doctor);
        return msg.equals("Doctor updated successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable UUID id) {
        String msg = doctorService.deleteDoctor(id);
        return msg.equals("Doctor deleted successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorService.addSpecialization(doctorId, specializationId);
        if (msg.equals("Specialization added to the doctor")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("The doctor already has that specialization")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorService.removeSpecialization(doctorId, specializationId);
        if (msg.equals("Specialization removed from the doctor")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("The doctor does not have that specialization")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @PatchMapping(value = "/{doctorId}/office")
    public ResponseEntity<String> moveToOffice(@PathVariable UUID doctorId, @RequestParam int officeNumber) {
        String msg = doctorService.moveToOffice(doctorId, officeNumber);
        if (msg.equals("Office changed successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("That office is taken by other doctor")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{doctorId}/agenda", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAgenda(@PathVariable UUID doctorId, @RequestParam String date) {
        if (!doctorService.getDoctorById(doctorId).isPresent())
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        List<Appointment> agenda = doctorService.getAgenda(doctorId, LocalDate.parse(date));
        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam String name) {
        return new ResponseEntity<>(doctorService.searchByName(name), HttpStatus.OK);
    }

    @GetMapping(value = "/workload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> getWorkload(@RequestParam String start, @RequestParam String end) {
        return new ResponseEntity<>(doctorService.getWorkload(LocalDate.parse(start), LocalDate.parse(end)), HttpStatus.OK);
    }
}
