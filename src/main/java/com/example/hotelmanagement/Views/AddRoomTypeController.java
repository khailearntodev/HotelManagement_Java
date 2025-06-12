package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.Services.RoomTypeService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class AddRoomTypeController implements Initializable {

    @FXML
    private ImageView roomTypeImageView;
    @FXML
    private MFXTextField roomTypeNameField;
    @FXML
    private MFXTextField basePriceField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private MFXTextField maxOccupancyField; // Added fx:id for max occupancy field
    @FXML
    private MFXButton uploadImageButton;
    @FXML
    private MFXButton addRoomTypeButton;

    private RoomTypeService roomTypeService;
    private File selectedImageFile; // To store the selected image file
    private Consumer<Boolean> onAddCallback; // Callback to notify parent controller

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize your RoomTypeService
        this.roomTypeService = new RoomTypeService();
        setupNumericInputValidation();
    }

    private void setupNumericInputValidation() {
        // TextFormatter for basePriceField (allows digits and one decimal point)
        Pattern decimalPattern = Pattern.compile("\\d*\\.?\\d*");
        UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
            if (decimalPattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        basePriceField.setTextFormatter(new TextFormatter<>(decimalFilter));

        // TextFormatter for maxOccupancyField (allows only digits)
        Pattern integerPattern = Pattern.compile("\\d*");
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            if (integerPattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        maxOccupancyField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    public void setOnAddCallback(Consumer<Boolean> onAddCallback) {
        this.onAddCallback = onAddCallback;
    }

    /**
     * Handles the image upload button click.
     * Opens a file chooser dialog to select an image.
     */
    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh loại phòng");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) uploadImageButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                roomTypeImageView.setImage(image);
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Lỗi tải hình", "Không thể tải hình ảnh đã chọn. Vui lòng thử lại.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the "Thêm loại phòng" button click.
     * Validates input, creates a Roomtype object, and saves it via the service.
     */
    @FXML
    private void handleAddRoomType(ActionEvent actionEvent) {
        // 1. Validate Input
        String typeName = roomTypeNameField.getText();
        String basePriceText = basePriceField.getText();
        String description = descriptionField.getText();
        String maxOccupancyText = maxOccupancyField.getText(); // Get text from new field

        if (typeName.isEmpty() || basePriceText.isEmpty() || maxOccupancyText.isEmpty()) {
            showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ tất cả các trường, với đơn giá là số thực và số khách tối đa là số nguyên.");
            return;
        }

        BigDecimal basePrice;
        try {
            basePrice = new BigDecimal(basePriceText);
            if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(AlertType.WARNING, "Lỗi nhập liệu", "Đơn giá phải là một số dương.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Lỗi nhập liệu", "Đơn giá phải là một số hợp lệ.");
            return;
        }

        int maxOccupancy;
        try {
            maxOccupancy = Integer.parseInt(maxOccupancyText);
            if (maxOccupancy <= 0) {
                showAlert(AlertType.WARNING, "Lỗi nhập liệu", "Số khách tối đa phải là một số nguyên dương.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Lỗi nhập liệu", "Số khách tối đa phải là một số nguyên hợp lệ.");
            return;
        }


        // 2. Convert Image to Base64 (if selected)
        String base64Image = null;
        if (selectedImageFile != null) {
            try (FileInputStream fileInputStream = new FileInputStream(selectedImageFile)) {
                byte[] imageData = fileInputStream.readAllBytes();
                base64Image = Base64.getEncoder().encodeToString(imageData);
            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Lỗi hình ảnh", "Không thể đọc hình ảnh. Vui lòng thử lại.");
                e.printStackTrace();
                return;
            }
        }

        // 3. Create Roomtype Object
        Roomtype newRoomType = new Roomtype();
        newRoomType.setTypeName(typeName);
        newRoomType.setBasePrice(basePrice);
        newRoomType.setDescription(description);
        newRoomType.setMaxOccupancy(maxOccupancy);
        newRoomType.setImage(base64Image); // This will be null if no image was selected
        newRoomType.setIsDeleted(false); // Default to not deleted

        // 4. Save using RoomTypeService
        try {
            boolean success = roomTypeService.addRoomType(newRoomType);
            if (success) {
                showAlert(AlertType.INFORMATION, "Thành công", "Đã thêm loại phòng mới thành công!");
                clearFields(); // Clear the form after successful addition
                if (onAddCallback != null) {
                    onAddCallback.accept(true); // Notify parent of success
                }
                // Close the current window (dialog)
                Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm loại phòng. Vui lòng kiểm tra nhật ký lỗi.");
                if (onAddCallback != null) {
                    onAddCallback.accept(false); // Notify parent of success
                }
            }
        } catch (RuntimeException e) {
            showAlert(AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Đã xảy ra lỗi khi lưu loại phòng: " + e.getMessage());
            e.printStackTrace();
            if (onAddCallback != null) {
                onAddCallback.accept(false); // Notify parent of success
            }
        }
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        roomTypeNameField.clear();
        basePriceField.clear();
        descriptionField.clear();
        maxOccupancyField.clear();
        roomTypeImageView.setImage(null);
        selectedImageFile = null;
    }

    /**
     * Displays an alert dialog to the user.
     * @param alertType The type of alert (INFORMATION, WARNING, ERROR).
     * @param title The title of the alert dialog.
     * @param message The message content of the alert dialog.
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
