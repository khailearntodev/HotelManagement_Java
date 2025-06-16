package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.ServiceDisplay;
import com.example.hotelmanagement.ViewModels.ServiceManagementViewModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.math.BigDecimal;
import java.net.URL;
import javafx.scene.layout.AnchorPane;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.text.NumberFormat; // Import này
import java.util.Locale;       // Import này

public class ServiceManagementController {

    @FXML private MFXTextField filterTextField;
    @FXML private TableView<ServiceDisplay> serviceTableView;
    @FXML private TableColumn<ServiceDisplay, String> colMaDichVu;
    @FXML private TableColumn<ServiceDisplay, String> colTenDichVu;
    @FXML private TableColumn<ServiceDisplay, BigDecimal> colGiaDichVu; // Kiểu vẫn là BigDecimal
    @FXML private TableColumn<ServiceDisplay, Void> colAction;

    @FXML private ImageView editServiceImageView;
    @FXML private TextField imageLinkEditTextField;
    @FXML private TextField maDichVuEditTextField;
    @FXML private TextField tenDichVuEditTextField;
    @FXML private TextField giaDichVuEditTextField;
    @FXML private Button updateServiceButton;
    @FXML private Button cancelEditButton;
    @FXML private Button findImageEditButton;
    @FXML private ImageView addServiceImageView;
    @FXML private TextField imageLinkAddTextField;
    @FXML private TextField tenDichVuAddTextField;
    @FXML private TextField giaDichVuAddTextField;
    @FXML private Button addServiceButton;
    @FXML private Button cancelAddButton;
    @FXML private Button findImageAddButton;

    @FXML private TabPane serviceTabPane;
    @FXML private AnchorPane rootPane;

    private final ServiceManagementViewModel viewModel = new ServiceManagementViewModel();

    private FilteredList<ServiceDisplay> filteredList;
    private SortedList<ServiceDisplay> sortedList;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    @FXML
    public void initialize() {
        URL cssUrl = getClass().getResource("/CSS/style.css");
        if (cssUrl != null && rootPane != null) {
            rootPane.getStylesheets().add(cssUrl.toExternalForm());
        } else if (rootPane == null) {
            System.err.println("RootPane not initialized for CSS linking.");
        } else {
            System.err.println("CSS file not found: /CSS/style.css");
        }
        currencyFormatter.setMinimumFractionDigits(0);
        viewModel.loadServices();

        filteredList = new FilteredList<>(viewModel.getMasterServiceList(), p -> true);

        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(serviceTableView.comparatorProperty());

        serviceTableView.setItems(sortedList);
        colMaDichVu.setCellValueFactory(cellData -> cellData.getValue().maDichVuProperty().asObject().asString());
        colTenDichVu.setCellValueFactory(cellData -> cellData.getValue().tenDichVuProperty());

        colGiaDichVu.setCellValueFactory(cellData -> cellData.getValue().giaDichVuProperty());
        colGiaDichVu.setCellFactory(column -> new TableCell<ServiceDisplay, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(item));
                }
            }
        });
        setupRowClick();
        setupFilter();

        clearImageView(addServiceImageView);
        clearImageView(editServiceImageView);
    }

    private void setupRowClick() {
        serviceTableView.setOnMouseClicked((MouseEvent event) -> {
            ServiceDisplay selected = serviceTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                serviceTabPane.getSelectionModel().select(1);

                maDichVuEditTextField.setText(String.valueOf(selected.getMaDichVu()));
                tenDichVuEditTextField.setText(selected.getTenDichVu());
                giaDichVuEditTextField.setText(selected.getGiaDichVu().toPlainString()); // Sử dụng toPlainString() để hiển thị số thuần khi chỉnh sửa
                imageLinkEditTextField.setText(selected.getImageLink());
                loadImageFromUrl(selected.getImageLink(), editServiceImageView);
            }
        });
    }

    @FXML
    private void setupFilter() {
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(service -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(service.getMaDichVu()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (service.getTenDichVu().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (service.getGiaDichVu().toPlainString().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
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
            filterTextField.clear();
        } catch (NumberFormatException e) {
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
                    filterTextField.clear(); // Clear filter to show updated list
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
        ServiceDisplay selected = serviceTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            maDichVuEditTextField.setText(String.valueOf(selected.getMaDichVu()));
            tenDichVuEditTextField.setText(selected.getTenDichVu());
            giaDichVuEditTextField.setText(selected.getGiaDichVu().toPlainString());
            imageLinkEditTextField.setText(selected.getImageLink());
            loadImageFromUrl(selected.getImageLink(), editServiceImageView);
        } else {
            clearEditForm();
            clearImageView(editServiceImageView);
        }
        filterTextField.clear();
    }

    private void clearAddForm() {
        tenDichVuAddTextField.clear();
        giaDichVuAddTextField.clear();
        imageLinkAddTextField.clear();
        clearImageView(addServiceImageView);
    }

    private void clearEditForm() {
        maDichVuEditTextField.clear();
        tenDichVuEditTextField.clear();
        giaDichVuEditTextField.clear();
        imageLinkEditTextField.clear();
        clearImageView(editServiceImageView);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadImageFromUrl(String url, ImageView imageView) {
        if (imageView == null) return;

        if (url != null && !url.trim().isEmpty()) {
            try {
                Image image = new Image(url, true);
                image.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        System.err.println("Lỗi khi tải ảnh từ URL: " + url + " - " + image.getException().getMessage());
                        clearImageView(imageView);
                        showAlert(Alert.AlertType.WARNING, "Lỗi tải ảnh", "Không thể tải ảnh từ URL đã cung cấp. Vui lòng kiểm tra lại đường dẫn.");
                    }
                });
                if (image.isError()) {
                    System.err.println("Lỗi ngay khi tạo Image từ URL: " + url + " - " + image.getException().getMessage());
                    clearImageView(imageView);
                    showAlert(Alert.AlertType.WARNING, "Lỗi tải ảnh", "URL hình ảnh không đúng định dạng hoặc không thể truy cập.");
                } else {
                    imageView.setImage(image);
                }

            } catch (IllegalArgumentException e) {
                System.err.println("URL hình ảnh không hợp lệ: " + url + " - " + e.getMessage());
                showAlert(Alert.AlertType.WARNING, "Link ảnh không hợp lệ", "URL hình ảnh không đúng định dạng hoặc không thể truy cập.");
                clearImageView(imageView);
            } catch (Exception e) {
                System.err.println("Lỗi không xác định khi tải ảnh: " + url + " - " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Lỗi tải ảnh", "Đã có lỗi xảy ra khi cố gắng tải ảnh.");
                clearImageView(imageView);
            }
        } else {
            clearImageView(imageView);
        }
    }

    private void clearImageView(ImageView imageView) {
        if (imageView != null) {
            imageView.setImage(null);
        }
    }

    @FXML
    private void onFindImageAdd(ActionEvent event) {
        String imageUrl = imageLinkAddTextField.getText();
        loadImageFromUrl(imageUrl, addServiceImageView);
    }

    @FXML
    private void onFindImageEdit(ActionEvent event) {
        String imageUrl = imageLinkEditTextField.getText();
        loadImageFromUrl(imageUrl, editServiceImageView);
    }
}