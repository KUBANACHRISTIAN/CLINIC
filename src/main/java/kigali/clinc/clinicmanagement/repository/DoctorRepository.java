package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%',:name,'%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%',:name,'%')) ORDER BY d.lastName")
    List<Doctor> searchByName(@Param("name") String name);

    boolean existsByOfficeOfficeNumber(int officeNumber);
}
