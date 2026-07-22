package com.pe.eph.member.repository;

import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.gympackage.entity.GymPackage;
import com.pe.eph.member.entity.Member;
import com.pe.eph.trainer.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberCode(String memberCode);

    boolean existsByPhone(String phone);

    Optional<Member> findByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<Member> findByMemberCode(String memberCode);

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);

    long countByStatus(MemberStatus status);

    long countByTrainer(Trainer trainer);

    Page<Member> findByTrainer(Trainer trainer, Pageable pageable);

    Page<Member> findByTrainerAndFullNameContainingIgnoreCase(Trainer trainer, String fullName, Pageable pageable);

    Page<Member> findByGymPackage(GymPackage gymPackage, Pageable pageable);

    Page<Member> findByFullNameContainingIgnoreCase(String fullName,
                                                    Pageable pageable);

    Page<Member> findByPhoneContaining(String phone,
                                       Pageable pageable);

    List<Member> findByStatusAndExpireDateBetween(MemberStatus status,
                                                  LocalDate startDate,
                                                  LocalDate endDate);

    long countByJoinDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT m.gymPackage.packageName, COUNT(m) FROM Member m WHERE m.status = 'ACTIVE' AND m.gymPackage IS NOT NULL GROUP BY m.gymPackage.packageName")
    List<Object[]> countActiveMembersByPackage();

    List<Member> findTop5ByTrainerOrderByJoinDateDesc(Trainer trainer);

    @Query("SELECT m FROM Member m WHERE " +
            "LOWER(m.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "m.phone LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Member> searchMembers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.trainer = :trainer AND (" +
            "LOWER(m.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "m.phone LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Member> searchMembersByTrainer(@Param("trainer") Trainer trainer, @Param("keyword") String keyword, Pageable pageable);

}