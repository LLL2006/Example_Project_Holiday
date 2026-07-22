package com.pe.eph.member.controller;

import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.member.dto.request.AssignTrainerRequest;
import com.pe.eph.member.dto.request.CreateMemberRequest;
import com.pe.eph.member.dto.request.UpdateMemberPackageRequest;
import com.pe.eph.member.dto.request.UpdateMemberRequest;
import com.pe.eph.member.dto.response.MemberResponse;
import com.pe.eph.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public Page<MemberResponse> getAllMembers(
            @RequestParam(required = false, defaultValue = "false") boolean all,
            Pageable pageable,
            org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"))) {
            String phone = authentication.getName();
            return memberService.getMembersByTrainerPhone(phone, pageable);
        }
        return memberService.getAllMembers(pageable);
    }

    @GetMapping("/{id}")
    public MemberResponse getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    @PostMapping
    public MemberResponse createMember(
            @Valid @RequestBody CreateMemberRequest request) {

        return memberService.createMember(request);
    }

    @PutMapping("/{id}")
    public MemberResponse updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request) {

        return memberService.updateMember(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivateMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }

    @GetMapping("/search")
    public Page<MemberResponse> searchMembers(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "false") boolean all,
            Pageable pageable,
            org.springframework.security.core.Authentication authentication) {

        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"))) {
            String phone = authentication.getName();
            return memberService.searchMembersByTrainerPhone(keyword, phone, pageable);
        }
        return memberService.searchMembers(keyword, pageable);
    }

    @PutMapping("/{id}/trainer")
    public MemberResponse assignTrainer(
            @PathVariable Long id,
            @Valid @RequestBody AssignTrainerRequest request) {

        return memberService.assignTrainer(id, request);
    }

    @PutMapping("/{id}/package")
    public MemberResponse changePackage(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberPackageRequest request) {

        return memberService.changePackage(id, request);
    }

    @PutMapping("/{id}/stop-trainer")
    public MemberResponse stopTrainer(@PathVariable Long id) {
        return memberService.stopTrainer(id);
    }

    @GetMapping("/me")
    public MemberResponse getCurrentMember(Authentication authentication) {
        if (authentication == null) {
            throw new ResourceNotFoundException("Chưa đăng nhập");
        }
        String phone = authentication.getName();
        return memberService.getMemberByPhone(phone);
    }

}