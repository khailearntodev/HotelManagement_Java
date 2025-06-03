package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.ServiceDisplay;
import com.example.hotelmanagement.ViewModels.ServiceManagementViewModel; // Import your ViewModel
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.collections.ObservableList; // Keep this for JavaFX ObservableList
import javafx.scene.control.cell.PropertyValueFactory;
// import java.awt.event.ActionEvent; // This import is for AWT ActionEvent, likely not needed for JavaFX

import javafx.event.ActionEvent; // Correct import for JavaFX ActionEvent

import java.math.BigDecimal;

public class ServiceManagementController {

    @FXML private TextField filterTextField;
    @FXML private TableView<ServiceDisplay> serviceTableView;
    @FXML private TableColumn<ServiceDisplay, String> colMaDichVu;
    @FXML private TableColumn<ServiceDisplay, String> colTenDichVu;
    @FXML private TableColumn<ServiceDisplay, BigDecimal> colGiaDichVu;
    @FXML private TableColumn<ServiceDisplay, Void> colAction;

    @FXML private TextField imageLinkEditTextField;
    @FXML private TextField maDichVuEditTextField;
    @FXML private TextField tenDichVuEditTextField;
    @FXML private TextField giaDichVuEditTextField;
    @FXML private Button updateServiceButton;
    @FXML private Button cancelEditButton;
    @FXML private Button findImageEditButton;

    @FXML private TextField imageLinkAddTextField;
    @FXML private TextField tenDichVuAddTextField;
    @FXML private TextField giaDichVuAddTextField;
    @FXML private Button addServiceButton;
    @FXML private Button cancelAddButton;
    @FXML private Button findImageAddButton;

    @FXML private TabPane serviceTabPane;

    private final ServiceManagementViewModel viewModel = new ServiceManagementViewModel();

    @FXML
    public void initialize() {
        viewModel.loadServices();
        serviceTableView.setItems(viewModel.getServiceList());
        System.out.println(viewModel.getServiceList().size());
        colMaDichVu.setCellValueFactory(cellData -> cellData.getValue().maDichVuProperty().asObject().asString());
        colTenDichVu.setCellValueFactory(cellData -> cellData.getValue().tenDichVuProperty());
        colGiaDichVu.setCellValueFactory(cellData -> cellData.getValue().giaDichVuProperty());



        setupRowClick();
        //setupFilter();
    }

    private void setupRowClick() {
        serviceTableView.setOnMouseClicked((MouseEvent event) -> {
            ServiceDisplay selected = serviceTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                serviceTabPane.getSelectionModel().select(0);
                maDichVuEditTextField.setText(String.valueOf(selected.getMaDichVu()));
                tenDichVuEditTextField.setText(selected.getTenDichVu());
                giaDichVuEditTextField.setText(String.valueOf(selected.getGiaDichVu()));
                imageLinkEditTextField.setText(selected.getImageLink());
            }
        });
    }

    private void setupFilter() {
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // TODO: Implement filtering logic here.
            // You might want to implement a filtered list in the ViewModel or
            // re-filter the data displayed by the TableView.
            // For example, you could iterate through viewModel.getServiceList()
            // and create a new ObservableList for the TableView.
        });
    }

    @FXML
    private void onAddService() {
        String ten = tenDichVuAddTextField.getText();
        String giaText = giaDichVuAddTextField.getText();
        String image = imageLinkAddTextField.getText();

        try {
            BigDecimal gia = new BigDecimal(giaText);
            viewModel.addService(ten, gia, image);
            clearAddForm();
        } catch (NumberFormatException e) {
            // Handle invalid number input for price
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Giá dịch vụ phải là một số hợp lệ.");
        }
    }

    @FXML
    private void onUpdateService() {
        ServiceDisplay selected = serviceTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                String newTen = tenDichVuEditTextField.getText();
                BigDecimal newGia = new BigDecimal(giaDichVuEditTextField.getText());
                String newImage = imageLinkEditTextField.getText();

                selected.setTenDichVu(newTen);
                selected.setGiaDichVu(newGia);
                selected.setImageLink(newImage);

                boolean success = viewModel.updateService(selected);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION,"Thành công", "Cập nhật dịch vụ thành công");
                    viewModel.loadServices(); // reload lại bảng
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật dịch vụ.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Giá dịch vụ phải là số.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Cập nhật", "Vui lòng chọn dịch vụ cần cập nhật.");
        }
    }


    @FXML
    private void onCancelAdd() {
        clearAddForm();
    }

    @FXML
    private void onCancelEdit() {
        clearEditForm();
    }

    private void clearAddForm() {
        tenDichVuAddTextField.clear();
        giaDichVuAddTextField.clear();
        imageLinkAddTextField.clear();
    }

    private void clearEditForm() {
        maDichVuEditTextField.clear();
        tenDichVuEditTextField.clear();
        giaDichVuEditTextField.clear();
        imageLinkEditTextField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void updateService(ActionEvent event) {
        // This method will be triggered by FXML.
        // It's good practice to call onUpdateService from here if you want to reuse the logic.
        onUpdateService();
    }
}