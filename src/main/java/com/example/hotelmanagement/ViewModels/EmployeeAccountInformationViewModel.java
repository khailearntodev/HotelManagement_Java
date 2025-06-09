package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.UserAccountDAO;
import com.example.hotelmanagement.Models.Role;
import com.example.hotelmanagement.Models.Useraccount;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAccountInformationViewModel {

    @Getter
    private final StringProperty username = new SimpleStringProperty();
    @Getter
    private final StringProperty password = new SimpleStringProperty();
    @Getter
    private final StringProperty confirmPassword = new SimpleStringProperty();

    @Getter
    private final ObservableList<String> roleNames = FXCollections.observableArrayList();
    private final Map<String, Role> roleMap = new HashMap<>();

    // Dùng để bind với MFXComboBox<String>
    private final ObjectProperty<String> selectedRoleName = new SimpleObjectProperty<>();

    private Useraccount currentAccount;
    private int employeeId;

    // Phải được gọi từ Controller sau khi lấy role từ DAO
    public void loadRoles(List<Role> roles) {
        roleNames.clear();
        roleMap.clear();
        for (Role role : roles) {
            roleNames.add(role.getRoleName());
            roleMap.put(role.getRoleName(), role);
        }
        System.out.println("Available roles:");
        for (Role role : roles) {
            System.out.println("- " + role.getRoleName());
        }
    }

    public ObjectProperty<String> selectedRoleNameProperty() {
        return selectedRoleName;
    }

    public Role getSelectedRole() {
        return roleMap.get(selectedRoleName.get());
    }

    public void setSelectedRole(String roleName) {
        selectedRoleName.set(roleName);
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        loadAccountByEmployeeId(employeeId);
    }

    private void loadAccountByEmployeeId(int employeeId) {
        UserAccountDAO dao = new UserAccountDAO();
        currentAccount = dao.findByEmployeeId(employeeId);

        if (currentAccount != null) {
            username.set(currentAccount.getUsername());
            password.set(currentAccount.getPassword());
            confirmPassword.set(currentAccount.getPassword());

            Role role = currentAccount.getRoleID();
            if (role != null) {
                selectedRoleName.set(role.getRoleName());
                System.out.println("Role found: " + role.getRoleName() + " (ID: " + role.getId() + ")");
            } else {
                System.out.println("Role is null.");
                selectedRoleName.set(null);
            }
            System.out.println("Selected role name: " + role.getRoleName());
        } else {
            System.out.println("No account found for employeeId: " + employeeId);
            username.set("");
            password.set("");
            confirmPassword.set("");
            selectedRoleName.set(null);
        }

    }

    public boolean isPasswordConfirmed() {
        String pass = password.get();
        String confirm = confirmPassword.get();
        return pass != null && pass.equals(confirm);
    }

    public boolean saveAccount() {
        UserAccountDAO dao = new UserAccountDAO();

        Useraccount byUsername = dao.findByUsername(username.get());
        if (byUsername != null && (currentAccount == null || !byUsername.getId().equals(currentAccount.getId()))) {
            showAlert(AlertType.ERROR, "Lỗi", "Tên đăng nhập đã tồn tại.");
            return false;
        }

        Useraccount byEmployeeId = dao.findByEmployeeId(employeeId);
        if (byEmployeeId != null && currentAccount == null) {
            showAlert(AlertType.ERROR, "Lỗi", "Nhân viên này đã có tài khoản.");
            return false;
        }

        if (currentAccount == null) {
            currentAccount = new Useraccount();
            currentAccount.setIsDeleted(false);
            currentAccount.setEmployeeID(new com.example.hotelmanagement.Models.Employee());
            currentAccount.getEmployeeID().setId(employeeId);
        }

        currentAccount.setUsername(username.get());
        currentAccount.setPassword(password.get());

        Role selected = getSelectedRole();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Lỗi", "Vui lòng chọn Role cho tài khoản.");
            return false;
        }
        currentAccount.setRoleID(selected);

        boolean success;
        if (currentAccount.getId() == null) {
            success = dao.save(currentAccount, employeeId, selected.getId());
        } else {
            success = dao.update(currentAccount);
        }

        if (success) {
            showAlert(AlertType.INFORMATION, "Thành công", "Tài khoản đã được lưu.");
        } else {
            showAlert(AlertType.ERROR, "Lỗi", "Không thể lưu tài khoản.");
        }

        return success;
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
