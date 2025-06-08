package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.PermissionDAO;
import com.example.hotelmanagement.DAO.RoleDAO;
import com.example.hotelmanagement.Models.Permission;
import com.example.hotelmanagement.Models.Role;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigViewModel {
    private final RoleDAO roleDAO = new RoleDAO();
    private final PermissionDAO permissionDAO = new PermissionDAO();

    @Getter
    private final ObservableList<Role> roles = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<String> roleNames = FXCollections.observableArrayList();

    private final StringProperty newRoleName = new SimpleStringProperty();
    private final StringProperty selectedRoleName = new SimpleStringProperty();

    private final Map<String, Set<String>> roleToViewsMap = new HashMap<>();

    @Getter
    private final ObservableList<String> allAvailableViews = FXCollections.observableArrayList();

    public ConfigViewModel() {
        loadPermissions();
    }

    public void loadPermissions() {
        List<Permission> permissions = permissionDAO.getAllPermissions();
        List<String> permissionNames = permissions.stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toList());
        allAvailableViews.setAll(permissionNames);
    }


    public void loadRoles(List<Role> loadedRoles) {
        roles.setAll(loadedRoles);
        roleNames.setAll(loadedRoles.stream().map(Role::getRoleName).toList());
    }

    public void addRole() {
        String name = newRoleName.get().trim();
        if (!name.isEmpty() && !roleNames.contains(name)) {
            Role newRole = new Role();
            newRole.setRoleName(name);

            boolean saved = roleDAO.save(newRole);
            if (saved) {
                roles.add(newRole);
                roleNames.add(name);
                roleToViewsMap.put(name, new HashSet<>());
                newRoleName.set("");
            } else {
                System.err.println("Lưu role thất bại: " + name);
            }
        }
    }


    public void deleteRole(String roleName) {
        roles.removeIf(r -> r.getRoleName().equals(roleName));
        roleNames.remove(roleName);
        roleToViewsMap.remove(roleName);
        if (roleName.equals(selectedRoleName.get())) {
            selectedRoleName.set(null);
        }
    }

    public void selectRole(String roleName) {
        selectedRoleName.set(roleName);
    }

    public void setViewAccess(String viewName, boolean hasAccess) {
        if (selectedRoleName.get() == null) return;

        Set<String> views = roleToViewsMap.computeIfAbsent(selectedRoleName.get(), k -> new HashSet<>());
        if (hasAccess) {
            views.add(viewName);
        } else {
            views.remove(viewName);
        }
    }

    public boolean hasViewAccess(String viewName) {
        if (selectedRoleName.get() == null) return false;
        return roleToViewsMap.getOrDefault(selectedRoleName.get(), Collections.emptySet()).contains(viewName);
    }


    public StringProperty newRoleNameProperty() {
        return newRoleName;
    }

    public StringProperty selectedRoleNameProperty() {
        return selectedRoleName;
    }
}
