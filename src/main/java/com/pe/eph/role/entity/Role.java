package com.pe.eph.role.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pe.eph.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "role")
    @Builder.Default
    @JsonIgnore
    private List<User> users = new ArrayList<>();

}