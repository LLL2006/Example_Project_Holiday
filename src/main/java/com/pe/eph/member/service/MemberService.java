package com.pe.eph.member.service;

import com.pe.eph.member.dto.request.AssignTrainerRequest;
import com.pe.eph.member.dto.request.CreateMemberRequest;
import com.pe.eph.member.dto.request.UpdateMemberPackageRequest;
import com.pe.eph.member.dto.request.UpdateMemberRequest;
import com.pe.eph.member.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    Page<MemberResponse> getAllMembers(Pageable pageable);

    MemberResponse getMemberById(Long id);

    MemberResponse createMember(CreateMemberRequest request);

    MemberResponse updateMember(Long id, UpdateMemberRequest request);

    void deleteMember(Long id);

    MemberResponse assignTrainer(Long memberId,
                                 AssignTrainerRequest request);

    MemberResponse stopTrainer(Long memberId);

    MemberResponse changePackage(Long memberId,
                                 UpdateMemberPackageRequest request);

    Page<MemberResponse> searchMembers(String keyword,
                                       Pageable pageable);

    MemberResponse getMemberByPhone(String phone);

    Page<MemberResponse> getMembersByTrainerPhone(String phone, Pageable pageable);

    Page<MemberResponse> searchMembersByTrainerPhone(String keyword, String phone, Pageable pageable);
}
