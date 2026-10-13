package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    List<Patient> findByFirstNameContainingIgnoreCase(String firstName);

    // D2 - find patient by ssNumber
    Optional<Patient> findBySsNumber(String ssNumber);
}
