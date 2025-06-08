package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "ROLE_PERMISSION")
@IdClass(RolePermission.CompositeKey.class)
public class RolePermission {

    @Id
    @Column(name = "roleID")
    private int roleID;

    @Id
    @Column(name = "permissionID")
    private int permissionID;

    @ManyToOne
    @JoinColumn(name = "roleID", insertable = false, updatable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permissionID", insertable = false, updatable = false)
    private Permission permission;

    public static class CompositeKey implements Serializable {
        private int roleID;
        private int permissionID;

        public CompositeKey() {}

        public CompositeKey(int roleID, int permissionID) {
            this.roleID = roleID;
            this.permissionID = permissionID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CompositeKey)) return false;
            CompositeKey that = (CompositeKey) o;
            return roleID == that.roleID && permissionID == that.permissionID;
        }

        @Override
        public int hashCode() {
            return Objects.hash(roleID, permissionID);
        }
    }
}
