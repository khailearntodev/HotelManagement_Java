package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.ServiceDisplay;
import com.example.hotelmanagement.ViewModels.ServiceManagementViewModel; // Import your ViewModel
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    // Khai báo ImageView cho tab Sửa
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
    @FXML private Button findImageAddButton; // Đã có sẵn

    @FXML private TabPane serviceTabPane;

    private final ServiceManagementViewModel viewModel = new ServiceManagementViewModel();

    @FXML
    public void initialize() {
        viewModel.loadServices();
        serviceTableView.setItems(viewModel.getServiceList());
        colMaDichVu.setCellValueFactory(cellData -> cellData.getValue().maDichVuProperty().asObject().asString());
        colTenDichVu.setCellValueFactory(cellData -> cellData.getValue().tenDichVuProperty());
        colGiaDichVu.setCellValueFactory(cellData -> cellData.getValue().giaDichVuProperty());

        setupRowClick();
        //setupFilter();

        // Đặt ảnh mặc định hoặc xóa ảnh khi khởi tạo (tùy chọn)
        clearImageView(addServiceImageView);
        clearImageView(editServiceImageView);
    }

    private void setupRowClick() {
        serviceTableView.setOnMouseClicked((MouseEvent event) -> {
            ServiceDisplay selected = serviceTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                serviceTabPane.getSelectionModel().select(0); // Chuyển sang tab "Sửa dịch vụ"
                maDichVuEditTextField.setText(String.valueOf(selected.getMaDichVu()));
                tenDichVuEditTextField.setText(selected.getTenDichVu());
                giaDichVuEditTextField.setText(String.valueOf(selected.getGiaDichVu()));
                imageLinkEditTextField.setText(selected.getImageLink());
                // Tải ảnh khi chọn một hàng
                loadImageFromUrl(selected.getImageLink(), editServiceImageView);
            }
        });
    }

    private void setupFilter() {
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // TODO: Implement filtering logic here.
        });
    }

    @FXML
    private void onAddService() {
        String ten = tenDichVuAddTextField.getText();
        String giaText = giaDichVuAddTextField.getText();
        String image = imageLinkAddTextField.getText(); // Lấy link ảnh

        try {
            BigDecimal gia = new BigDecimal(giaText);
            viewModel.addService(ten, gia, image); // Truyền link ảnh vào ViewModel
            clearAddForm();
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
                String newImage = imageLinkEditTextField.getText(); // Lấy link ảnh mới

                selected.setTenDichVu(newTen);
                selected.setGiaDichVu(newGia);
                selected.setImageLink(newImage); // Cập nhật link ảnh cho đối tượng

                boolean success = viewModel.updateService(selected); // ViewModel cần xử lý việc cập nhật link ảnh

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION,"Thành công", "Cập nhật dịch vụ thành công");
                    viewModel.loadServices();
                    // Không cần clearEditForm() ở đây nếu muốn giữ lại thông tin sau khi cập nhật thành công
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
        ServiceDisplay selected = serviceTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadImageFromUrl(selected.getImageLink(), editServiceImageView);
        } else {
            clearImageView(editServiceImageView);
        }
    }

    private void clearAddForm() {
        tenDichVuAddTextField.clear();
        giaDichVuAddTextField.clear();
        imageLinkAddTextField.clear();
        clearImageView(addServiceImageView); // Xóa ảnh trong ImageView
    }

    private void clearEditForm() {
        // Không xóa maDichVuEditTextField vì nó disable và chứa mã của dịch vụ đang chọn
        // maDichVuEditTextField.clear();
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

    // Phương thức tải ảnh từ URL và hiển thị lên ImageView
    private void loadImageFromUrl(String url, ImageView imageView) {
        if (imageView == null) return;

        if (url != null && !url.trim().isEmpty()) {
            try {
                // Tham số thứ 2 'true' để tải ảnh ở chế độ nền (background loading)
                // Tham số thứ 3 và 4 là requestedWidth và requestedHeight (0 để giữ kích thước gốc)
                // Tham số thứ 5 'true' để giữ tỉ lệ, 'false' để bỏ qua
                // Tham số thứ 6 'true' để làm mịn ảnh khi co giãn
                Image image = new Image(url, true);

                image.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) { // Nếu có lỗi khi tải ảnh
                        System.err.println("Lỗi khi tải ảnh từ URL: " + url + " - " + image.getException().getMessage());

                        clearImageView(imageView);
                    }
                });

                if (image.isError()) {
                    System.err.println("Lỗi ngay khi tạo Image từ URL: " + url + " - " + image.getException().getMessage());
                    clearImageView(imageView);
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

    // Phương thức updateService bạn đã có, không cần thay đổi tên gọi từ FXML
    // @FXML
    // private void updateService(ActionEvent event) {
    //     onUpdateService();
    // }
}