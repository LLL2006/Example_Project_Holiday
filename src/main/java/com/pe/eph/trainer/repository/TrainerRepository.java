package com.pe.eph.trainer.repository;

import com.pe.eph.common.enums.UserStatus;
import com.pe.eph.trainer.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    boolean existsByTrainerCode(String trainerCode);

    boolean existsByPhone(String phone);

    Optional<Trainer> findByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<Trainer> findByTrainerCode(String trainerCode);

    Page<Trainer> findByStatus(UserStatus status, Pageable pageable);

    Page<Trainer> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    Page<Trainer> findByPhoneContaining(String phone, Pageable pageable);

    Page<Trainer> findBySpecializationContainingIgnoreCase(String specialization,
                                                           Pageable pageable);

    @Query("SELECT t FROM Trainer t WHERE " +
            "LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "t.phone LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(t.trainerCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Trainer> searchTrainers(@Param("keyword") String keyword, Pageable pageable);

}