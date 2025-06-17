package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.Services.RoomService;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AddRoomController implements Initializable {
    @FXML
    public MFXTextField roomNumberField;
    @FXML
    public MFXTextField roomTypeField;
    @FXML
    public TextArea roomNoteField;
    private RoomService roomService;
    private Consumer<Boolean> onAddCallback; // Callback to notify parent on add success/failure
    private Roomtype selectedRoomType;
    private Consumer<Boolean> onUpdateCallback; // Callback for parent controller

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roomService = new RoomService();
    }
    public void setUpRoomTypeName(Roomtype rt){
        roomTypeField.setText(rt.getTypeName());
        selectedRoomType = rt;

    }
    /**
     * Sets a callback function to be executed when a room is successfully added.
     * @param onAddCallback A Consumer that accepts a boolean (true for success, false for failure).
     */
    public void setOnAddCallback(Consumer<Boolean> onAddCallback) {
        this.onAddCallback = onAddCallback;
    }
    public void handleAddClick(ActionEvent actionEvent) {
        String roomNumberText = roomNumberField.getText();
        String roomNote = roomNoteField.getText();

        if (roomNumberText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập số phòng.");
            return;
        }


        Integer roomNumber;
        try {
            roomNumber = Integer.parseInt(roomNumberText);
            if (roomNumber <= 0) {
                showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Số phòng phải là một số nguyên dương.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Số phòng phải là một số nguyên hợp lệ.");
            return;
        }
        if (!roomService.isRoomNumberUnique(roomNumber, null)) { // Pass null for currentRoomId as it's a new room
            showAlert(Alert.AlertType.WARNING, "Số phòng đã tồn tại", "Số phòng này đã được sử dụng. Vui lòng chọn số khác.");
            return;
        }

        // 2. Create Room Object
        Room newRoom = new Room();
        newRoom.setRoomNumber(roomNumber);
        newRoom.setRoomTypeID(selectedRoomType); // Assign the stored Roomtype object
        newRoom.setNote(roomNote.isEmpty() ? "" : roomNote); // Set null if empty string
        newRoom.setStatus(1); // Default to 1: Còn trống
        newRoom.setCleaningStatus(0); // Default to 0: Sạch
        newRoom.setIsDeleted(false); // Default to false

        // 3. Save using RoomService
        try {
            // Assuming roomService.save() handles both persist (new) and merge (update)
            // You might need a specific saveRoom() method in RoomService if different
            boolean saved = roomService.save(newRoom);
            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm phòng số " + newRoom.getRoomNumber() + " thành công!");
                clearFields(); // Clear the form
                if (onAddCallback != null) {
                    onAddCallback.accept(true); // Notify parent of success
                }
                // Close the current window (dialog)
                Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm phòng. Vui lòng kiểm tra nhật ký lỗi.");
                if (onAddCallback != null) {
                    onAddCallback.accept(false); // Notify parent of failure
                }
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Đã xảy ra lỗi khi lưu phòng: " + e.getMessage());
            e.printStackTrace();
            if (onAddCallback != null) {
                onAddCallback.accept(false); // Notify parent of failure
            }
        }
    }
    private void clearFields() {
        roomNumberField.clear();
        roomNoteField.clear();
        // roomTypeField should not be cleared as it's set by parent
    }

    /**
     * Displays an alert dialog to the user.
     * @param alertType The type of alert (INFORMATION, WARNING, ERROR).
     * @param title The title of the alert dialog.
     * @param message The message content of the alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
