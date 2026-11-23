package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    Optional<Office> findByOfficeNumber(int officeNumber);

    boolean existsByOfficeNumber(int officeNumber);

    List<Office> findByNameContainingIgnoreCase(String name);

    @Query("SELECT o FROM Office o WHERE NOT EXISTS (SELECT d FROM Doctor d WHERE d.office = o) ORDER BY o.officeNumber")
    List<Office> findFreeOffices();

    @Query("SELECT COUNT(o) FROM Office o WHERE NOT EXISTS (SELECT d FROM Doctor d WHERE d.office = o)")
    long countFreeOffices();
}
