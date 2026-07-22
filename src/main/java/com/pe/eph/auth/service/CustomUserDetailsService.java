package com.pe.eph.auth.service;

import com.pe.eph.common.enums.UserStatus;
import com.pe.eph.user.entity.User;
import com.pe.eph.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByPhone(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản.")));

        String roleName = user.getRole() == null ? "USER" : user.getRole().getRoleName();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(user.getStatus() != UserStatus.ACTIVE)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + roleName)))
                .build();
    }
}
