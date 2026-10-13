package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired private PrescriptionRepository prescriptionRepo;
    @Autowired private AppointmentRepository appointmentRepo;

    // C1 - write a prescription (only for COMPLETED appointments)
    public String savePrescription(UUID appointmentId, Prescription prescription) {
        return appointmentRepo.findById(appointmentId).map(appointment -> {
            if (appointment.getStatus() != AppointmentStatus.COMPLETED)
                return "Prescriptions are only for COMPLETED appointments";
            prescription.setIssuedDate(LocalDate.now());
            prescription.setAppointment(appointment);
            prescriptionRepo.save(prescription);
            return "Prescription saved successfully";
        }).orElse("The appointment with that id does not exist");
    }

    // C2 - check appointment exists (for controller 404)
    public String checkAppointmentExists(UUID appointmentId) {
        if (!appointmentRepo.existsById(appointmentId))
            return "The appointment with that id does not exist";
        return null;
    }

    // C2 - prescriptions of an appointment (DERIVED)
    public List<Prescription> getByAppointment(UUID appointmentId) {
        return prescriptionRepo.findByAppointmentId(appointmentId);
    }

    // C3 - prescription history of a patient (JPQL)
    public List<Prescription> getByPatient(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumber(ssNumber);
    }

    // C4 - update prescription
    public String updatePrescription(UUID id, Prescription updated) {
        return prescriptionRepo.findById(id).map(p -> {
            p.setMedicineName(updated.getMedicineName());
            p.setDosage(updated.getDosage());
            p.setDurationDays(updated.getDurationDays());
            prescriptionRepo.save(p);
            return "Prescription updated successfully";
        }).orElse("The prescription with that id does not exist");
    }

    // C4 - delete prescription
    public String deletePrescription(UUID id) {
        if (!prescriptionRepo.existsById(id)) return "The prescription with that id does not exist";
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    // Bonus - top medicines
    public List<Object[]> getTopMedicines() { return prescriptionRepo.findTopMedicines(); }

    // D4 - total prescriptions count
    public long countAll() { return prescriptionRepo.count(); }
}
