package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.CustomerManagement_CustomerDisplay;
import com.example.hotelmanagement.ViewModels.CustomerDetailViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerDetailController {

    @FXML
    private TextField txtHoTen;

    @FXML
    private DatePicker dpNgaySinh;

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
        cbLoaiKhach.getItems().clear();
        cbLoaiKhach.getItems().add(viewModel.loaiKhachProperty().get());
        cbLoaiKhach.getSelectionModel().selectFirst();

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
        if (!phone.matches("\\d+")) {
            showAlert("Số điện thoại không hợp lệ.");
            return false;
        }

        String cccd = txtCCCD.getText().trim();
        if (!cccd.matches("\\d{9,12}")) {
            showAlert("CMND/CCCD không hợp lệ. Vui lòng nhập tối đa 12 số.");
            return false;
        }

        String hoTen = txtHoTen.getText().trim();
        if (hoTen.isBlank()) {
            showAlert("Họ tên không được để trống.");
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
