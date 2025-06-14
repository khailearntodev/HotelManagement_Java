package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Services.RoomService;
import com.example.hotelmanagement.ViewModels.RoomViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class UpdateRoomController implements Initializable {
    @FXML
    public MFXTextField roomNumberField;
    @FXML
    public MFXTextField roomTypeField;
    @FXML
    public MFXButton statusButton;
    @FXML
    public MFXButton cleaningStatusButton;
    @FXML
    public TextArea roomNoteField;
    @FXML
    public MFXButton editButton;
    public RoomService roomService;
    private Room currentRoom; // The Room entity being edited
    private Consumer<Boolean> onUpdateCallback; // Callback for parent controller



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roomService = new RoomService();
    }
    public void handleSetRoomData(RoomViewModel roomViewModel){
        if (roomViewModel == null) {
            clearFields();
            return;
        }
        this.currentRoom = roomService.getRoomById(roomViewModel.getId());
        roomNumberField.setText(String.valueOf(roomViewModel.getRoomNumber()));
        roomTypeField.setText(roomViewModel.getRoomTypeName());
        statusButton.setText(roomViewModel.getDisplayStatus());
        cleaningStatusButton.setText(roomViewModel.getDisplayCleaningStatus());
        roomNoteField.setText(roomViewModel.getNote());
    }
    public void setOnUpdateCallback(Consumer<Boolean> onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;
    }
    @FXML
    public void handleEditRoomClick(ActionEvent actionEvent) {
        if (currentRoom == null) {
            showAlert(AlertType.ERROR, "Lỗi", "Không có phòng nào được chọn để cập nhật.");
            return;
        }

        // 1. Validate Input
        String roomNumberText = roomNumberField.getText();
        String roomNote = roomNoteField.getText();

        if (roomNumberText.isEmpty()) {
            showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập số phòng.");
            return;
        }

        Integer roomNumber;
        try {
            roomNumber = Integer.parseInt(roomNumberText);
            if (roomNumber <= 0) {
                showAlert(AlertType.WARNING, "Lỗi nhập liệu", "Số phòng phải là một số nguyên dương.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Lỗi nhập liệu", "Số phòng phải là một số nguyên hợp lệ.");
            return;
        }

        // Convert button text status back to integer
        Integer status = getStatusFromDisplayText(statusButton.getText());
        Integer cleaningStatus = getCleaningStatusFromDisplayText(cleaningStatusButton.getText());

        if (status == null || cleaningStatus == null) {
            showAlert(AlertType.ERROR, "Lỗi trạng thái", "Trạng thái hoặc trạng thái dọn dẹp không hợp lệ.");
            return;
        }

        // 2. Update currentRoom Object
        currentRoom.setRoomNumber(roomNumber);
        currentRoom.setNote(roomNote.isEmpty() ? null : roomNote); // Set null if empty
        currentRoom.setStatus(status);
        currentRoom.setCleaningStatus(cleaningStatus);
        // Do NOT update roomTypeID or isDeleted here, as they are typically not changed in a basic room edit.

        // 3. Save (Update) using RoomService
        try {
            boolean saved = roomService.update(currentRoom); // save() method handles both persist and merge
            if (saved) {
                showAlert(AlertType.INFORMATION, "Thành công", "Đã cập nhật phòng thành công!");
                if (onUpdateCallback != null) {
                    onUpdateCallback.accept(true); // Notify parent of success
                }
                // Close the current window (dialog)
                Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật phòng. Vui lòng kiểm tra nhật ký lỗi.");
                if (onUpdateCallback != null) {
                    onUpdateCallback.accept(false); // Notify parent of failure
                }
            }
        } catch (RuntimeException e) {
            showAlert(AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Đã xảy ra lỗi khi lưu phòng: " + e.getMessage());
            e.printStackTrace();
            if (onUpdateCallback != null) {
                onUpdateCallback.accept(false); // Notify parent of failure
            }
        }
    }

    /**
     * Handles cycling the room status when the status button is clicked.
     * @param event ActionEvent
     */
    @FXML
    private void handleStatusButtonClick(ActionEvent event) {
        Integer currentStatus = getStatusFromDisplayText(statusButton.getText());
        int nextStatus = (currentStatus != null && currentStatus < 3) ? currentStatus + 1 : 1; // Cycle 1 -> 2 -> 3 -> 1
        statusButton.setText(getDisplayStatus(nextStatus));
    }

    /**
     * Handles cycling the cleaning status when the cleaning status button is clicked.
     * @param event ActionEvent
     */
    @FXML
    private void handleCleaningStatusButtonClick(ActionEvent event) {
        Integer currentCleaningStatus = getCleaningStatusFromDisplayText(cleaningStatusButton.getText());
        int nextCleaningStatus = (currentCleaningStatus != null && currentCleaningStatus < 2) ? currentCleaningStatus + 1 : 0; // Cycle 0 -> 1 -> 0
        cleaningStatusButton.setText(getDisplayCleaningStatus(nextCleaningStatus));
    }

    /**
     * Converts the integer status to a human-readable Vietnamese string.
     * 1: Còn trống, 2: Đang được thuê, 3: Được đặt trước.
     * @param status The integer status.
     * @return The status as a Vietnamese string.
     */
    private String getDisplayStatus(Integer status) {
        if (status == null) return "Không xác định";
        switch (status) {
            case 1: return "Còn trống";
            case 2: return "Đang được thuê";
            case 3: return "Được đặt trước";
            default: return "Không xác định";
        }
    }

    /**
     * Converts the human-readable Vietnamese status string back to its integer representation.
     * @param displayText The status as a Vietnamese string.
     * @return The integer status, or null if not found.
     */
    private Integer getStatusFromDisplayText(String displayText) {
        if (displayText == null) return null;
        switch (displayText) {
            case "Còn trống": return 1;
            case "Đang được thuê": return 2;
            case "Được đặt trước": return 3;
            default: return null;
        }
    }

    /**
     * Converts the integer cleaning status to a human-readable Vietnamese string.
     * 0: Sạch, 1: Bẩn.
     * @param cleaningStatus The integer cleaning status.
     * @return The cleaning status as a Vietnamese string.
     */
    private String getDisplayCleaningStatus(Integer cleaningStatus) {
        if (cleaningStatus == null) return "Không xác định";
        switch (cleaningStatus) {
            case 0: return "Sạch";
            case 1: return "Bẩn";
            case 2: return "Đang dọn dẹp";
            default: return "Không xác định";
        }
    }

    /**
     * Converts the human-readable Vietnamese cleaning status string back to its integer representation.
     * @param displayText The cleaning status as a Vietnamese string.
     * @return The integer cleaning status, or null if not found.
     */
    private Integer getCleaningStatusFromDisplayText(String displayText) {
        if (displayText == null) return null;
        switch (displayText) {
            case "Sạch": return 0;
            case "Bẩn": return 1;
            case "Đang dọn dẹp": return 2;
            default: return null;
        }
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        roomNumberField.clear();
        roomTypeField.clear();
        roomNoteField.clear();
        statusButton.setText("");
        cleaningStatusButton.setText("");
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
