package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.RoleDAO;
import com.example.hotelmanagement.DAO.RolePermissionDAO;
import com.example.hotelmanagement.DAO.UserAccountDAO;
import com.example.hotelmanagement.Models.Role;
import com.example.hotelmanagement.ViewModels.ConfigViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ConfigController {

    @FXML
    private MFXTextField roleNameField;

    @FXML
    private MFXButton btnAddRole;

    @FXML
    private TableView<Role> rolesTable;

    @FXML
    private TableColumn<Role, String> roleNameColumn;

    @FXML
    private TableColumn<Role, Void> deleteColumn;

    @FXML
    private MFXComboBox<String> roleSelector;

    @FXML
    private VBox viewsCheckboxContainer;

    @FXML
    private MFXButton btnSavePermissions;

    private final ConfigViewModel viewModel = new ConfigViewModel();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    private final RoleDAO roleDAO = new RoleDAO();

    @FXML
    public void initialize() {
        // Bind field
        roleNameField.textProperty().bindBidirectional(viewModel.newRoleNameProperty());

        // Load dữ liệu mẫu (hoặc từ DB)
        loadInitialRoles();

        // Hiển thị danh sách role trong bảng
        setupRolesTable();

        // Sự kiện thêm quyền mới
        btnAddRole.setOnAction(e -> {
            String newRoleName = roleNameField.getText();
            if (newRoleName == null || newRoleName.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Tên quyền không được để trống.");
                return;
            }

            String normalizedNew = normalize(newRoleName);

            boolean isDuplicate = viewModel.getRoles().stream()
                    .map(role -> normalize(role.getRoleName()))
                    .anyMatch(normalizedExisting -> normalizedExisting.equals(normalizedNew));

            if (isDuplicate) {
                showAlert(Alert.AlertType.WARNING, "Quyền tương tự đã tồn tại. Vui lòng nhập quyền có ý nghĩa khác với các quyền đã có!");
                return;
            }

            viewModel.addRole();
            refreshRoleTable();
            refreshRoleSelector();

            refreshRoleTable();
            refreshRoleSelector();
        });

        // Hiển thị dropdown chọn quyền
        roleSelector.setItems(viewModel.getRoleNames());
        roleSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.selectRole(newVal);
            renderViewCheckboxes();
        });

        // Nút lưu phân quyền
        btnSavePermissions.setOnAction(e -> {
            savePermissionsToDb();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Phân quyền đã được lưu.", ButtonType.OK);
            alert.showAndWait();
        });

    }

    private void loadInitialRoles() {

        viewModel.loadRoles(FXCollections.observableArrayList(roleDAO.getAll()));
        refreshRoleSelector();
    }

    private void savePermissionsToDb() {
        String roleName = viewModel.selectedRoleNameProperty().get();
        if (roleName == null) {
            System.out.println("Chưa chọn Role nào để lưu permissions.");
            return;
        }

        int roleId = getRoleIdByName(roleName);
        if (roleId == -1) {
            System.out.println("Lỗi: Không tìm thấy roleId tương ứng với roleName: " + roleName);
            return;
        }

        Set<String> selectedViews = new HashSet<>();
        for (String viewName : viewModel.getAllAvailableViews()) {
            if (viewModel.hasViewAccess(viewName)) {
                selectedViews.add(viewName);
            }
        }
        rolePermissionDAO.saveRolePermissions(roleId, selectedViews);
    }


    private int getRoleIdByName(String roleName) {
        return viewModel.getRoles().stream()
                .filter(r -> r.getRoleName().equals(roleName))
                .map(Role::getId)
                .findFirst().orElse(-1);
    }

    private void setupRolesTable() {
        // Cột tên quyền
        roleNameColumn = new TableColumn<>("Tên quyền");
        roleNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRoleName()));
        roleNameColumn.setPrefWidth(280);

        // Cột xóa
        deleteColumn = new TableColumn<>("Xóa");
        deleteColumn.setPrefWidth(100);
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("X");
            private final StackPane centeredPane = new StackPane(deleteButton);
            {

                deleteButton.setOnAction(event -> {
                    Role role = getTableView().getItems().get(getIndex());
                    rolesTable.getSelectionModel().select(role);
                    Role selectedRole = rolesTable.getSelectionModel().getSelectedItem();
                    if (selectedRole == null) {
                        showAlert(Alert.AlertType.WARNING, "Chưa chọn vai trò nào để xóa.");
                        return;
                    }

                    UserAccountDAO userAccountDAO = new UserAccountDAO();
                    boolean isRoleInUse = userAccountDAO.isRoleInUse(selectedRole.getId());

                    if (isRoleInUse) {
                        showAlert(Alert.AlertType.WARNING, "Vai trò đang được sử dụng bởi tài khoản người dùng, không thể xóa.");
                        return;
                    }

                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Xác nhận");
                    confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa vai trò này?");
                    confirmAlert.setContentText("Vai trò: " + selectedRole.getRoleName());

                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
                            boolean deletedPermissions = rolePermissionDAO.deleteByRoleId(selectedRole.getId());

                            if (!deletedPermissions) {
                                showAlert(Alert.AlertType.ERROR, "Không thể xóa quyền liên quan.");
                                return;
                            }

                            RoleDAO roleDAO = new RoleDAO();
                            boolean deletedRole = roleDAO.softDelete(selectedRole.getId());

                            if (deletedRole) {
                                showAlert(Alert.AlertType.INFORMATION, "Xóa vai trò thành công.");

                                loadInitialRoles();
                                refreshRoleTable();
                                refreshRoleSelector();
                                viewModel.selectedRoleNameProperty().set(null);
                                viewsCheckboxContainer.getChildren().clear();

                            } else {
                                showAlert(Alert.AlertType.ERROR, "Xóa vai trò thất bại.");
                            }

                        }
                    });
                });


                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                centeredPane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(centeredPane);
                }
            }
        });


        rolesTable.getColumns().setAll(roleNameColumn, deleteColumn);
        rolesTable.setItems(viewModel.getRoles());
    }

    private String normalize(String input) {
        if (input == null) return "";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""); // bỏ dấu
        return normalized.toLowerCase().replaceAll("\\s+", ""); // thường hóa + bỏ khoảng trắng
    }


    private void refreshRoleTable() {
        rolesTable.setItems(null);
        rolesTable.setItems(viewModel.getRoles());
    }

    private void refreshRoleSelector() {
        roleSelector.setItems(viewModel.getRoleNames());
    }

    private void renderViewCheckboxes() {
        viewsCheckboxContainer.getChildren().clear();

        for (String view : viewModel.getAllAvailableViews()) {
            CheckBox checkBox = new CheckBox(view);
            checkBox.setSelected(viewModel.hasViewAccess(view));

            checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) ->
                    viewModel.setViewAccess(view, isNowSelected)
            );

            viewsCheckboxContainer.getChildren().add(checkBox);
        }
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
