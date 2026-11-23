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


    boolean existsByDoctorAndAppointmentDateAndStatusNotAndIdNot(Doctor doctor, LocalDate date, AppointmentStatus status, UUID id);


    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    
    List<Appointment> findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
        String ssNumber, LocalDate date, List<AppointmentStatus> statuses);

    long countByStatus(AppointmentStatus status);

    
    long countByAppointmentDate(LocalDate date);

    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d WHERE a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED AND a.appointmentDate BETWEEN :start AND :end GROUP BY d.firstName, d.lastName ORDER BY COUNT(a) DESC")
    List<Object[]> findDoctorWorkload(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
