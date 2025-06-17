package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.ServiceDisplay;
import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.ViewModels.InvoiceDetailViewModel;
import com.example.hotelmanagement.ViewModels.ServiceManagementViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import java.util.List;
import java.util.Locale;       // Import này

public class ServiceManagementController {

    @FXML private MFXTextField filterTextField;
    @FXML private TableView<ServiceDisplay> serviceTableView;
    @FXML private TableColumn<ServiceDisplay, String> colMaDichVu;
    @FXML private TableColumn<ServiceDisplay, String> colTenDichVu;
    @FXML private TableColumn<ServiceDisplay, BigDecimal> colGiaDichVu;
    @FXML private TableColumn<ServiceDisplay, Void> colXoaDichVu;

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
        colXoaDichVu.setCellFactory(param -> new TableCell<>() {
            private final MFXButton deleteButton = new MFXButton("Xóa");

            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setOnAction(event -> {
                    ServiceDisplay selectedService = getTableView().getItems().get(getIndex());
                    if (selectedService != null) {
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmationAlert.setTitle("Xác nhận xóa");
                        confirmationAlert.setHeaderText(null);
                        confirmationAlert.setContentText("Bạn có chắc chắn muốn xóa dịch vụ: " + selectedService.getTenDichVu() + " không?");

                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                boolean success = viewModel.deleteService(selectedService);
                                if (success) {
                                    viewModel.getMasterServiceList().remove(selectedService);
                                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa dịch vụ thành công.");
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Xóa dịch vụ thất bại.");
                                }
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
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
                serviceTabPane.getSelectionModel().select(0);

                maDichVuEditTextField.setText(String.valueOf(selected.getMaDichVu()));
                tenDichVuEditTextField.setText(selected.getTenDichVu());
                giaDichVuEditTextField.setText(selected.getGiaDichVu().toPlainString());
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
        String ten = tenDichVuAddTextField.getText().trim();
        String giaText = giaDichVuAddTextField.getText().trim();
        String image = imageLinkAddTextField.getText().trim();

        if (ten.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Tên dịch vụ không được để trống.");
            return;
        }

        try {
            BigDecimal gia = new BigDecimal(giaText);
            if (gia.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Giá dịch vụ không được âm.");
                return;
            }

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
            String newTen = tenDichVuEditTextField.getText().trim();
            String giaText = giaDichVuEditTextField.getText().trim();

            if (newTen.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Tên dịch vụ không được để trống.");
                return;
            }

            try {
                BigDecimal newGia = new BigDecimal(giaText);
                if (newGia.compareTo(BigDecimal.ZERO) < 0) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Giá dịch vụ không được âm.");
                    return;
                }

                String newImage = imageLinkEditTextField.getText().trim();

                selected.setTenDichVu(newTen);
                selected.setGiaDichVu(newGia);
                selected.setImageLink(newImage);

                boolean success = viewModel.updateService(selected);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật dịch vụ thành công");
                    filterTextField.clear();
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