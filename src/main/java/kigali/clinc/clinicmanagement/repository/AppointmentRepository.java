package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // B3 - DERIVED: check if doctor has a non-CANCELLED appointment on a date (excluding a specific appointment)
    boolean existsByDoctorAndAppointmentDateAndStatusNotAndIdNot(Doctor doctor, LocalDate date, AppointmentStatus status, UUID id);

    // D1 - DERIVED: doctor's appointments on a date, excluding CANCELLED
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    // D2 - DERIVED: patient's upcoming appointments (today or later), status in list, sorted by date
    List<Appointment> findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
        String ssNumber, LocalDate date, List<AppointmentStatus> statuses);

    // D4 - DERIVED: count appointments by status
    long countByStatus(AppointmentStatus status);

    // D4 - DERIVED: count appointments today
    long countByAppointmentDate(LocalDate date);

    // Bonus workload - JPQL: completed appointments per doctor between two dates
    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d WHERE a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED AND a.appointmentDate BETWEEN :start AND :end GROUP BY d.firstName, d.lastName ORDER BY COUNT(a) DESC")
    List<Object[]> findDoctorWorkload(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
