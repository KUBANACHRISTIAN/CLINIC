package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    // C2 - DERIVED: prescriptions of an appointment
    List<Prescription> findByAppointmentId(UUID appointmentId);

    // C3 - JPQL: all prescriptions of a patient across appointments, newest first
    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.ssNumber = :ssNumber ORDER BY p.issuedDate DESC")
    List<Prescription> findByPatientSsNumber(@Param("ssNumber") String ssNumber);

    // C4 - DERIVED: check if appointment has prescriptions
    boolean existsByAppointmentId(UUID appointmentId);

    // Bonus: top medicines by prescription count
    @Query("SELECT p.medicineName, COUNT(p) FROM Prescription p GROUP BY p.medicineName ORDER BY COUNT(p) DESC")
    List<Object[]> findTopMedicines();
}
