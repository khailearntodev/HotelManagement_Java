package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.CustomerManagement_CustomerDisplay;
import com.example.hotelmanagement.ViewModels.CustomerDetailViewModel;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;

public class CustomerDetailController {

    @FXML
    private TextField txtHoTen;

    @FXML
    private MFXDatePicker dpNgaySinh;

    @FXML
    private ComboBox<String> cbLoaiKhach;

    @FXML
    private TextField txtTuoi;

    @FXML
    private RadioButton radioNam;

    @FXML
    private RadioButton radioNu;

    @FXML
    private TextField txtCCCD;

    @FXML
    private TextField txtDiaChi;

    @FXML
    private TextField txtSDT;

    @FXML
    private TextField txtTrangThai;

    @FXML
    private TextField txtSoLan;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    private CustomerDetailViewModel viewModel;

    private ToggleGroup genderGroup;

    @Setter
    private Runnable onSaveSuccess;

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        radioNam.setToggleGroup(genderGroup);
        radioNu.setToggleGroup(genderGroup);

        disableEditing(true);

        btnEdit.setOnAction(e -> disableEditing(false));

        btnCancel.setOnAction(e -> {
            if (viewModel != null) {
                disableEditing(true);
                closeWindow();
            }
        });

        btnSave.setOnAction(e -> {
            if (viewModel != null) {
                if (!isInputValid()) return;
                boolean success = viewModel.save();
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Lưu dữ liệu thành công!");
                    alert.showAndWait();

                    if (onSaveSuccess != null) {
                        onSaveSuccess.run();
                    }
                    disableEditing(true);
                    closeWindow();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Lưu dữ liệu thất bại!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

    }

    private void closeWindow() {

        btnSave.getScene().getWindow().hide();
    }
    public void setCustomer(CustomerManagement_CustomerDisplay dto) {
        this.viewModel = new CustomerDetailViewModel(dto);
        bindFields();
    }

    public void loadCustomer(CustomerManagement_CustomerDisplay dto) {
        this.viewModel = new CustomerDetailViewModel(dto);
        bindFields();
    }
    private void bindFields() {
        if (viewModel == null) return;

        txtHoTen.textProperty().bindBidirectional(viewModel.hoTenProperty());
        dpNgaySinh.valueProperty().bindBidirectional(viewModel.ngaySinhProperty());
        cbLoaiKhach.setItems(viewModel.getCustomerTypeList());
        cbLoaiKhach.valueProperty().bindBidirectional(viewModel.loaiKhachProperty());

        txtTuoi.textProperty().bind(viewModel.tuoiProperty());

        genderGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == radioNam) {
                viewModel.gioiTinhProperty().set(true);
            } else if (newToggle == radioNu) {
                viewModel.gioiTinhProperty().set(false);
            }
        });

        viewModel.gioiTinhProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                genderGroup.selectToggle(radioNam);
            } else {
                genderGroup.selectToggle(radioNu);
            }
        });

        if (viewModel.gioiTinhProperty().get()) {
            genderGroup.selectToggle(radioNam);
        } else {
            genderGroup.selectToggle(radioNu);
        }

        txtCCCD.textProperty().bindBidirectional(viewModel.cccdProperty());
        txtDiaChi.textProperty().bindBidirectional(viewModel.diaChiProperty());
        txtSDT.textProperty().bindBidirectional(viewModel.sdtProperty());
        txtTrangThai.textProperty().bind(viewModel.trangThaiProperty());
        txtSoLan.textProperty().bind(viewModel.soLanDatProperty());
    }

    private void disableEditing(boolean disable) {
        txtHoTen.setEditable(!disable);
        dpNgaySinh.setDisable(disable);
        cbLoaiKhach.setDisable(disable);
        txtTuoi.setEditable(false);
        radioNam.setDisable(disable);
        radioNu.setDisable(disable);
        txtCCCD.setEditable(!disable);
        txtDiaChi.setEditable(!disable);
        txtSDT.setEditable(!disable);
        txtTrangThai.setEditable(false);
        txtSoLan.setEditable(false);
        btnSave.setDisable(disable);
        //btnCancel.setDisable(disable);
    }

    private boolean isInputValid() {
        String phone = txtSDT.getText().trim();
        if (!phone.matches("\\d{10,20}")) {
            showAlert("Số điện thoại phải là số và có từ 10 đến 20 chữ số.");
            return false;
        }

        String cccd = txtCCCD.getText().trim();
        if (!cccd.matches("\\d{9,12}")) {
            showAlert("CMND/CCCD không hợp lệ. Vui lòng nhập từ 9 - 12 số.");
            return false;
        }

        if (dpNgaySinh.getValue() == null) {
            showAlert("Vui lòng chọn ngày sinh.");
            return false;
        }
        if (dpNgaySinh.getValue().isAfter(java.time.LocalDate.now())) {
            showAlert("Ngày sinh không được lớn hơn ngày hiện tại.");
            return false;
        }
        if (dpNgaySinh.getValue().isBefore(java.time.LocalDate.of(1900, 1, 1))) {
            showAlert("Ngày sinh không hợp lệ (quá xa trong quá khứ).");
            return false;
        }

        String hoTen = txtHoTen.getText().trim();
        if (hoTen.isBlank()) {
            showAlert("Họ tên không được để trống.");
            return false;
        }

        if (genderGroup.getSelectedToggle() == null) {
            showAlert("Vui lòng chọn giới tính.");
            return false;
        }

        if (dpNgaySinh.getValue() == null) {
            showAlert("Vui lòng chọn ngày sinh.");
            return false;
        }

        if (genderGroup.getSelectedToggle() == null) {
            showAlert("Vui lòng chọn giới tính.");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
