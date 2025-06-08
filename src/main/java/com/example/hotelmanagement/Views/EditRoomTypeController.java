package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.Services.RoomTypeService;
import com.example.hotelmanagement.Models.Room; // Assuming you have a Room model
import com.example.hotelmanagement.Services.RoomService; // Assuming you will have a RoomService
import com.example.hotelmanagement.ViewModels.RoomViewModel; // Assuming you have a RoomViewModel

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn; // For Room Table
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer; // For callback

public class EditRoomTypeController implements Initializable {

    @FXML
    private ImageView roomTypeImageView;
    @FXML
    private MFXTextField roomTypeNameField;
    @FXML
    private MFXTextField basePriceField;
    @FXML
    private TextArea descriptionField; // Changed to TextArea as per FXML
    @FXML
    private MFXTextField maxOccupancyField; // Added for Max Occupancy
    @FXML
    private MFXButton uploadImageButton;
    @FXML
    private MFXButton updateRoomTypeButton;
    @FXML
    private MFXTableView<RoomViewModel> roomsTableView; // Table to display associated rooms

    private RoomTypeService roomTypeService;
    private RoomService roomService; // Service for Room entities
    private Roomtype currentRoomType; // To hold the Roomtype being edited
    private File selectedImageFile; // To store the selected image file
    private Consumer<Boolean> onUpdateCallback; // Callback to notify parent controller

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.roomTypeService = new RoomTypeService();
        this.roomService = new RoomService(); // Initialize RoomService

        // Setup columns for roomsTableView
        setupRoomsTable();
    }

    /**
     * Sets the Roomtype data to be displayed and edited in the form.
     * This method should be called by the parent controller (e.g., ManageRoomTypeController)
     * when opening this window.
     * @param roomType The Roomtype entity to load into the form.
     */
    public void setRoomTypeData(Roomtype roomType) {
        if (roomType == null) {
            clearFields();
            return;
        }
        this.currentRoomType = roomType;

        // Populate fields with existing data
        roomTypeNameField.setText(roomType.getTypeName());
        basePriceField.setText(roomType.getBasePrice() != null ? roomType.getBasePrice().toPlainString() : "");
        descriptionField.setText(roomType.getDescription());
        maxOccupancyField.setText(roomType.getMaxOccupancy() != null ? String.valueOf(roomType.getMaxOccupancy()) : "");

        // Load image if available (assuming Base64 string)
        String base64Image = roomType.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
                Image image = new Image(new ByteArrayInputStream(decodedBytes));
                roomTypeImageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid Base64 image string for Roomtype ID " + roomType.getId() + ": " + e.getMessage());
                roomTypeImageView.setImage(null);
            }
        } else {
            roomTypeImageView.setImage(null);
        }

        // Load associated rooms
        loadRoomsForRoomType(roomType.getId());
    }

    /**
     * Sets a callback function to be executed when the room type is successfully updated.
     * @param onUpdateCallback A Consumer that accepts a boolean (true for success, false for failure).
     */
    public void setOnUpdateCallback(Consumer<Boolean> onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;
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
     * Handles the "Lưu thay đổi" (Save Changes) button click.
     * Validates input, updates the current Roomtype object, and saves it via the service.
     */
    @FXML
    private void handleUpdateRoomType() {
        if (currentRoomType == null) {
            showAlert(AlertType.ERROR, "Lỗi", "Không có loại phòng nào được chọn để cập nhật.");
            return;
        }

        // 1. Validate Input
        String typeName = roomTypeNameField.getText();
        String basePriceText = basePriceField.getText();
        String description = descriptionField.getText();
        String maxOccupancyText = maxOccupancyField.getText();

        if (typeName.isEmpty() || basePriceText.isEmpty() || description.isEmpty() || maxOccupancyText.isEmpty()) {
            showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ tất cả các trường.");
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

        // 2. Convert Image to Base64 (if a new image was selected)
        String base64Image = currentRoomType.getImage(); // Keep existing image if no new one selected
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

        // 3. Update currentRoomType Object
        currentRoomType.setTypeName(typeName);
        currentRoomType.setBasePrice(basePrice);
        currentRoomType.setDescription(description);
        currentRoomType.setMaxOccupancy(maxOccupancy);
        currentRoomType.setImage(base64Image);

        // 4. Save (Update) using RoomTypeService
        try {
            boolean updated = roomTypeService.updateRoomType(currentRoomType);
            if (updated) {
                showAlert(AlertType.INFORMATION, "Thành công", "Đã cập nhật loại phòng thành công!");
                if (onUpdateCallback != null) {
                    onUpdateCallback.accept(true); // Notify parent of success
                }
                // Optionally close the window after successful update
                ((Stage) updateRoomTypeButton.getScene().getWindow()).close();
            } else {
                showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật loại phòng. Vui lòng kiểm tra nhật ký lỗi.");
                if (onUpdateCallback != null) {
                    onUpdateCallback.accept(false); // Notify parent of failure
                }
            }
        } catch (RuntimeException e) {
            showAlert(AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Đã xảy ra lỗi khi cập nhật loại phòng: " + e.getMessage());
            e.printStackTrace();
            if (onUpdateCallback != null) {
                onUpdateCallback.accept(false); // Notify parent of failure
            }
        }
    }

    /**
     * Sets up the columns for the roomsTableView.
     * You will need to define your RoomViewModel and Room properties.
     */
    private void setupRoomsTable() {
        MFXTableColumn<RoomViewModel> roomNumberCol = new MFXTableColumn<>("Số phòng", true);
        MFXTableColumn<RoomViewModel> statusCol = new MFXTableColumn<>("Trạng thái", true);
        MFXTableColumn<RoomViewModel> cleaningStatusCol = new MFXTableColumn<>("Trạng thái dọn dẹp", true);
        MFXTableColumn<RoomViewModel> actionsCol = new MFXTableColumn<>("Thao tác", true); // New Actions column

        roomNumberCol.setRowCellFactory(room -> new MFXTableRowCell<RoomViewModel, Object>(RoomViewModel::getRoomNumber));
        roomNumberCol.setPrefWidth(100); // Adjust width as needed
        statusCol.setRowCellFactory(room -> new MFXTableRowCell<RoomViewModel, Object>(RoomViewModel::getDisplayStatus));
        statusCol.setPrefWidth(120);
        cleaningStatusCol.setRowCellFactory(room -> new MFXTableRowCell<RoomViewModel, Object>(RoomViewModel::getDisplayCleaningStatus));
        cleaningStatusCol.setPrefWidth(150);
        actionsCol.setRowCellFactory(roomViewModel -> new MFXTableRowCell<RoomViewModel, Object>(RoomViewModel::getClass) {
            @Override
            public void update(RoomViewModel item) {
                super.update(item);
                if (item != null) {
                    // Create an HBox to hold the buttons
                    HBox buttonsContainer = new HBox(2); // 5px spacing between buttons
                    buttonsContainer.setAlignment(Pos.CENTER); // Center buttons in the cell

                    MFXButton editButton = new MFXButton("Sửa");
                    editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;"); // Blue color
                    editButton.setPrefWidth(50);
                    editButton.setOnAction(event -> handleEditRoom(item)); // Pass the RoomViewModel to the handler

                    MFXButton deleteButton = new MFXButton("Xóa");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;"); // Red color
                    deleteButton.setPrefWidth(50);
                    deleteButton.setOnAction(event -> handleDeleteRoom(item)); // Pass the RoomViewModel to the handler

                    buttonsContainer.getChildren().addAll(editButton, deleteButton);
                    setGraphic(buttonsContainer); // Set the HBox as the graphic for the cell
                } else {
                    setGraphic(null); // Clear graphic if item is null
                }
            }
        });
        actionsCol.setPrefWidth(150); // Adjust width to fit buttons

        roomsTableView.getTableColumns().addAll(roomNumberCol, statusCol, cleaningStatusCol, actionsCol);
    }

    private void handleDeleteRoom(RoomViewModel item) {
    }

    private void handleEditRoom(RoomViewModel item) {
    }

    /**
     * Loads associated Room entities for the given Roomtype ID and populates the roomsTableView.
     * This method assumes you have a RoomService with a method to find rooms by room type ID.
     */
    private void loadRoomsForRoomType(Integer roomTypeId) {
        if (roomTypeId == null) {
            roomsTableView.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            // Assuming RoomService has a method like findRoomsByRoomTypeId
            // You will need to implement this method in your RoomDAO and RoomService
            List<Room> rooms = roomService.findRoomsByRoomTypeId(roomTypeId);
            List<RoomViewModel> roomViewModels = rooms.stream()
                    .map(RoomViewModel::new)
                    .toList();
            roomsTableView.setItems(FXCollections.observableArrayList(roomViewModels));
            System.out.println("Loaded " + roomViewModels.size() + " rooms for RoomType ID: " + roomTypeId);
        } catch (Exception e) {
            System.err.println("Error loading rooms for RoomType ID " + roomTypeId + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi tải phòng", "Không thể tải danh sách phòng liên quan.");
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
        roomsTableView.setItems(FXCollections.observableArrayList());
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
