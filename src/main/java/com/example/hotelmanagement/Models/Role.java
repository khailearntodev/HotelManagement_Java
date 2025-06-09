package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ROLE")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "RoleName", nullable = false, length = 50)
    private String roleName;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "roleID")
    private Set<com.example.hotelmanagement.Models.Useraccount> useraccounts = new LinkedHashSet<>();

    @Override
    public String toString() {
        return roleName;
    }
}