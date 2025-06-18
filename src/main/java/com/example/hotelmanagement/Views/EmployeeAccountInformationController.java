package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.RoleDAO;
import com.example.hotelmanagement.Models.Role;
import com.example.hotelmanagement.ViewModels.EmployeeAccountInformationViewModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.util.List;

public class EmployeeAccountInformationController {

    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXPasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;

    private final EmployeeAccountInformationViewModel viewModel = new EmployeeAccountInformationViewModel();

    public void loadAccount(int employeeId) {
        RoleDAO roleDAO = new RoleDAO();
        List<Role> roles = roleDAO.getAll();
        viewModel.loadRoles(roles);
        roleComboBox.setItems(viewModel.getRoleNames());
        roleComboBox.valueProperty().bindBidirectional(viewModel.selectedRoleNameProperty());
        Platform.runLater(() -> {
            roleComboBox.setValue(viewModel.selectedRoleNameProperty().getValue());
        });
        viewModel.setEmployeeId(employeeId);
//        System.out.println("Role names in ComboBox: " + roleComboBox.getItems());
//        System.out.println("Selected role name: " + roleComboBox.getValue());

        bindFields();
    }

    private void bindFields() {
        usernameField.textProperty().bindBidirectional(viewModel.getUsername());
        passwordField.textProperty().bindBidirectional(viewModel.getPassword());
        confirmPasswordField.textProperty().bindBidirectional(viewModel.getConfirmPassword());
    }

    @FXML
    private void onSave() {
        if (viewModel.getUsername().get() == null || viewModel.getUsername().get().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập không được để trống.");
            return;
        }
        if (viewModel.getPassword().get() == null || viewModel.getPassword().get().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không được để trống.");
            return;
        }
        if (!viewModel.isPasswordConfirmed()) {
            showAlert(Alert.AlertType.ERROR, "Mật khẩu không khớp", "Vui lòng kiểm tra lại mật khẩu.");
            return;
        }
        if (viewModel.getSelectedRole() == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn vai trò cho tài khoản.");
            return;
        }
        viewModel.saveAccount();
    }

    @FXML
    private void onReset() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
