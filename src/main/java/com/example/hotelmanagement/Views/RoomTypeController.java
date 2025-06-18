package com.example.hotelmanagement.Views; // This package matches your FXML fx:controller

import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.Services.RoomService;
import com.example.hotelmanagement.Services.RoomTypeService;
import com.example.hotelmanagement.ViewModels.RoomTypeViewModel;
import com.example.hotelmanagement.ViewModels.RoomViewModel;
import com.example.hotelmanagement.Views.RoomTypeCardController; // Import the card controller

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label; // Import Label for error messages
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RoomTypeController implements Initializable {

    public Label roomTypeNameLabel;
    public Label emptyRoomsLabel;
    public Label statusLabel;
    public MFXButton editDetailsButton;
    public MFXTableView<RoomViewModel> roomListTable;
    public SplitPane splitPane1;
    public SplitPane splitPane2;
    public ImageView roomTypeImageView;
    public VBox statusIndicatorVBox;
    public Label typeNameLabel;
    public Label priceTextField;
    public Label maxOccupancyTextField;
    public Label descriptionTextArea;
    @FXML
    private FlowPane roomCardsContainer; // This fx:id matches the FlowPane in ManageRoomType.fxml

    private RoomTypeService roomTypeService;
    private RoomService roomService;
    private Roomtype currentlyDisplayedRoomType; // To hold the Roomtype object currently shown in details
    private RoomTypeCardController currentlyHighlightedCardController;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the RoomTypeService
        this.roomTypeService = new RoomTypeService();

        this.roomService = new RoomService(); // Initialize RoomService


        // Load the RoomType cards into the FlowPane
        loadRoomTypeCards();
        setupRoomsTable();
    }
    /**
     * Helper method to disable resizing for all dividers in a SplitPane.
     */
    private void disableSplitPaneDividers(SplitPane splitPane) {
        if (splitPane != null) {
            for (SplitPane.Divider divider : splitPane.getDividers()) {

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
                    setText(null);
                } else {
                    setGraphic(null); // Clear graphic if item is null
                    setText(null);
                }
            }
        });
        actionsCol.setPrefWidth(150); // Adjust width to fit buttons

        roomListTable.getTableColumns().addAll(roomNumberCol, statusCol, cleaningStatusCol, actionsCol);
    }

    private void handleDeleteRoom(RoomViewModel item) {
        System.out.println("Attempting to delete Room: " + item.getRoomNumber() + " (ID: " + item.getId() + ")");
        if(!item.getDisplayStatus().equals("Còn trống")){
            String message = item.getDisplayStatus().equals("Đang được thuê")?"phòng đang được thuê":"phòng được đặt trước";
            showAlert(Alert.AlertType.ERROR, "Không xóa được phòng " + item.getRoomNumber(), "Không xóa được phòng " + item.getRoomNumber() + " vì " + message);
            return;
        }
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận xóa phòng");
        confirmationAlert.setHeaderText("Bạn có chắc chắn muốn xóa phòng này?");
        confirmationAlert.setContentText("Phòng số " + item.getRoomNumber() + " sẽ được đánh dấu là đã xóa. Bạn không thể hoàn tác thao tác này.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Call the service to soft delete the room
                boolean success = roomService.delete(item.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa mềm phòng thành công!");
                    // Refresh the rooms list in the table after successful deletion
                    loadRoomTypeCards(); // Refresh the list of cards
                    // After update, re-display the updated details in the side panel
                    if (this.currentlyDisplayedRoomType != null && this.currentlyDisplayedRoomType.getId() != null) {
                        Roomtype a = roomTypeService.getRoomTypeById(this.currentlyDisplayedRoomType.getId());
                        if(a != null){
                            displayRoomTypeDetails(a);
                        }
                    }

                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa mềm phòng. Vui lòng kiểm tra nhật ký lỗi.");
                }
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Đã xảy ra lỗi khi xóa mềm phòng: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleEditRoom(RoomViewModel item) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateRoom.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            UpdateRoomController updateRoomController = loader.getController();
            updateRoomController.handleSetRoomData(item);
            updateRoomController.setOnUpdateCallback(success -> {
                if (success) {
                    loadRoomTypeCards(); // Refresh the list of cards
                    // After update, re-display the updated details in the side panel
                    if (this.currentlyDisplayedRoomType != null && this.currentlyDisplayedRoomType.getId() != null) {
                        Roomtype a = roomTypeService.getRoomTypeById(this.currentlyDisplayedRoomType.getId());
                        if(a != null){
                            displayRoomTypeDetails(a);
                        }
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Cập nhật thất bại", "Có lỗi xảy ra khi cập nhật phòng.");
                }
            });

            //        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainWindow.fxml"));
            //        Scene scene = new Scene(fxmlLoader.load(), 1360, 820);

            scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
            System.out.println(getClass().getResource("/CSS/style.css"));
            Stage stage = new Stage();
            stage.setTitle("Add Room");
            stage.setScene(scene);
            stage.show();
        }catch (IOException e) {
            e.printStackTrace(); // Or throw a Deadpool-style panic attack
        }
    }

    /**
     * Loads Roomtype data from the service and dynamically creates Roomtype cards,
     * adding them to the roomCardsContainer (FlowPane).
     */
    private void loadRoomTypeCards() {
        currentlyHighlightedCardController = null;
        RoomTypeCardController firstCard = null;
        RoomTypeViewModel firstViewModel = null;

        roomCardsContainer.getChildren().clear(); // Clear existing cards before loading new ones

        try {
            // Fetch Roomtype entities using the RoomTypeService
            List<Roomtype> roomTypes = roomTypeService.getAllRoomTypes();

            // Convert Roomtype entities to RoomTypeViewModel objects
            List<RoomTypeViewModel> roomTypeViewModels = roomTypes.stream()
                    .map(RoomTypeViewModel::new) // Use the RoomTypeViewModel constructor that takes a Roomtype entity
                    .toList();

            if (roomTypeViewModels.isEmpty()) {
                System.out.println("No RoomTypes found in the database.");
                // Display a message to the user if no data is found
                Label noDataLabel = new Label("No room types available.");
                roomCardsContainer.getChildren().add(noDataLabel);
                return;
            }

            // For each RoomTypeViewModel, load its corresponding FXML card and populate it
            for (RoomTypeViewModel viewModel : roomTypeViewModels) {
                try {
                    // Load the FXML for a single Roomtype card
                    // Ensure this path is correct relative to your classpath (e.g., in src/main/resources)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/RoomtypeCard.fxml"));
                    Parent roomCardNode = loader.load(); // Load the FXML node

                    // Get the controller for the loaded card
                    RoomTypeCardController cardController = loader.getController();
                    // Set the data on the card's controller using the ViewModel
                    cardController.setRoomtypeData(viewModel);
                    // Set the click handler for the card
                    cardController.setOnCardClick(clickedViewModel -> { handleRoomTypeCardClick(clickedViewModel, cardController); });                    //Set the delete handler for the card
                    cardController.handleDeleteRoomtype(this::handleDeleteRoomTypeClick);

                    // Add the loaded card to our container (FlowPane)
                    roomCardsContainer.getChildren().add(roomCardNode);
                    if (firstCard == null && firstViewModel == null) {
                        firstCard = cardController;
                        firstViewModel = viewModel;
                    }
                } catch (IOException e) {
                    System.err.println("Error loading RoomtypeCard.fxml: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (firstCard != null && firstViewModel != null) {
                handleRoomTypeCardClick(firstViewModel, firstCard);
            }
            System.out.println("Successfully loaded " + roomTypeViewModels.size() + " Roomtype cards.");

        } catch (RuntimeException e) {
            System.err.println("Error fetching RoomType data from service: " + e.getMessage());
            e.printStackTrace();
            // Display an error message to the user if data fetching fails
            Label errorLabel = new Label("Failed to load room types: " + e.getMessage());
            roomCardsContainer.getChildren().add(errorLabel);
        }
    }
    /**
     * Handles the click event on a Roomtype card.
     * Opens the EditRoomType window with the data of the clicked card.
     * @param clickedViewModel The RoomTypeViewModel of the clicked card.
     */
    private void handleRoomTypeCardClick(RoomTypeViewModel clickedViewModel, RoomTypeCardController cardController) {
        try {
            if (currentlyHighlightedCardController != null && currentlyHighlightedCardController != cardController) { currentlyHighlightedCardController.setSelected(false); }
            // Fetch the full Roomtype entity from the database using its ID
            Roomtype roomTypeOptional = roomTypeService.getRoomTypeById(clickedViewModel.getId());

            if (roomTypeOptional != null) {
                cardController.setSelected(true);
                currentlyHighlightedCardController = cardController;
                currentlyDisplayedRoomType = roomTypeOptional;
                editDetailsButton.setDisable(false);
                displayRoomTypeDetails(currentlyDisplayedRoomType);

            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin loại phòng để chỉnh sửa.");
                clearRoomDetails();
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi truy xuất dữ liệu", "Không thể lấy dữ liệu loại phòng từ cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
            clearRoomDetails();

        }
    }
    private void handleDeleteRoomTypeClick(RoomTypeViewModel deletedViewModel){
        // Fetch the room type details to show in the confirmation alert
        Roomtype roomTypeOptional = roomTypeService.getRoomTypeById(deletedViewModel.getId());
        Integer roomTypeId = deletedViewModel.getId();
        String roomTypeName = roomTypeOptional.getTypeName();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận xóa loại phòng");
        confirmationAlert.setHeaderText("Bạn có chắc chắn muốn xóa loại phòng này?");
        confirmationAlert.setContentText("Loại phòng '" + roomTypeName + "' sẽ được đánh dấu là đã xóa. Bạn không thể hoàn tác thao tác này.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = roomTypeService.softDeleteRoomType(roomTypeId);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa mềm loại phòng thành công!");
                    loadRoomTypeCards(); // Refresh the list of cards
                    clearRoomDetails(); // Clear details since the room type might be gone
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa mềm loại phòng. Vui lòng kiểm tra nhật ký lỗi.");
                }
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Đã xảy ra lỗi khi xóa mềm loại phòng: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void displayRoomTypeDetails(Roomtype roomType) {
        if (roomType == null) {
            clearRoomDetails();
            return;
        }
        RoomService roomService = new RoomService();
        long roomsCount = roomService.countTotalRoomsByRoomTypeId(roomType.getId());
        long availableRoomCount = roomService.countAvailableRoomsByRoomTypeId(roomType.getId());
        boolean isThereRoom = (availableRoomCount != 0);
        roomTypeNameLabel.setText(roomType.getTypeName());
        emptyRoomsLabel.setText(String.format("Còn trống: %d/%d phòng", availableRoomCount, roomsCount));
        statusLabel.setText(isThereRoom? "Còn phòng": "Hết phòng");
        statusIndicatorVBox.setStyle(String.format("-fx-background-color: %s", isThereRoom? "green": "red"));
        String base64Image = roomType.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
                Image image = new Image(new ByteArrayInputStream(decodedBytes));
                roomTypeImageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid Base64 image string for Roomtype: " + roomType.getTypeName());
                roomTypeImageView.setImage(new Image(getClass().getResource("/Images/icon.png").toExternalForm()));
            }
        }
        else{
            roomTypeImageView.setImage(new Image(getClass().getResource("/Images/icon.png").toExternalForm()));
        }
        priceTextField.setText(roomType.getBasePrice() + "");
        maxOccupancyTextField.setText(roomType.getMaxOccupancy() + "");
        descriptionTextArea.setText(roomType.getDescription());
        loadRoomsForRoomType(roomType.getId());
    }
    private void loadRoomsForRoomType(Integer roomTypeId) {
        if (roomTypeId == null) {
            roomListTable.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            // Assuming RoomService has a method like findRoomsByRoomTypeId
            // You will need to implement this method in your RoomDAO and RoomService
            List<Room> rooms = roomService.findRoomsByRoomTypeId(roomTypeId);
            List<RoomViewModel> roomViewModels = rooms.stream()
                    .map(RoomViewModel::new)
                    .toList();
            roomListTable.setItems(FXCollections.observableArrayList(roomViewModels));
            System.out.println("Loaded " + roomViewModels.size() + " rooms for RoomType ID: " + roomTypeId);
        } catch (Exception e) {
            System.err.println("Error loading rooms for RoomType ID " + roomTypeId + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi tải phòng", "Không thể tải danh sách phòng liên quan.");
        }
    }
    private void clearRoomDetails() {
        if (currentlyHighlightedCardController != null) {
            currentlyHighlightedCardController.setSelected(false);
            currentlyHighlightedCardController = null;
        }
        roomTypeNameLabel.setText("");
        statusLabel.setText("");
        //availableRoomsLabel.setText("Chọn một loại phòng để xem chi tiết");
        editDetailsButton.setDisable(true); // Disable the edit button until a card is selected
        roomListTable.setItems(FXCollections.observableArrayList());

        this.currentlyDisplayedRoomType = null;
        if(roomTypeImageView != null){
            roomTypeImageView.setImage(new Image(getClass().getResource("/Images/icon.png").toExternalForm()));
        }
    }
    public void handleEditClick() {
        if (this.currentlyDisplayedRoomType == null) {
            showAlert(Alert.AlertType.WARNING, "Không có loại phòng", "Vui lòng chọn một loại phòng để chỉnh sửa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/UpdateRoomType.fxml"));
            Parent editRoot = loader.load();
            EditRoomTypeController editController = loader.getController();

            // Pass the Roomtype entity to the edit controller
            editController.setRoomTypeData(this.currentlyDisplayedRoomType);

            // Set a callback to refresh the card list after the edit window closes
            editController.setOnUpdateCallback(success -> {
                if (success) {
                    loadRoomTypeCards(); // Refresh the list of cards
                    // After update, re-display the updated details in the side panel
                    if (this.currentlyDisplayedRoomType != null && this.currentlyDisplayedRoomType.getId() != null) {
                        Roomtype a = roomTypeService.getRoomTypeById(this.currentlyDisplayedRoomType.getId());
                        if(a != null){
                            displayRoomTypeDetails(a);
                        }
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Cập nhật thất bại", "Có lỗi xảy ra khi cập nhật loại phòng.");
                }
            });

            Stage editStage = new Stage();
            editStage.setTitle("Chỉnh sửa loại phòng");
            editStage.setScene(new Scene(editRoot));
            editStage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
            editStage.showAndWait(); // Show and wait for it to close
        }catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải cửa sổ", "Không thể tải cửa sổ chỉnh sửa loại phòng.");
            e.printStackTrace();
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi truy xuất dữ liệu", "Không thể lấy dữ liệu loại phòng từ cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Handles the action for the "Thêm phòng" (Add Room Type) button.
     * This is a placeholder; implement your logic to add a new room type here.
     */
    @FXML
    private void handleAddRoomType() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddRoomType.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            AddRoomTypeController addRoomTypeController = loader.getController();
            addRoomTypeController.setOnAddCallback((success) ->{
                if (success) {
                    loadRoomTypeCards(); // Refresh the list of cards
                    // After update, re-display the updated details in the side panel
                    if (this.currentlyDisplayedRoomType != null && this.currentlyDisplayedRoomType.getId() != null) {
                        Roomtype a = roomTypeService.getRoomTypeById(this.currentlyDisplayedRoomType.getId());
                        if(a != null){
                            displayRoomTypeDetails(a);
                        }
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thêm thất bại", "Có lỗi xảy ra khi thêm loại phòng.");
                }
            });

    //        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainWindow.fxml"));
    //        Scene scene = new Scene(fxmlLoader.load(), 1360, 820);

            scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
            System.out.println(getClass().getResource("/CSS/style.css"));
            Stage stage = new Stage();
            stage.setTitle("Add Room Type");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e) {
            e.printStackTrace(); // Or throw a Deadpool-style panic attack
        }
        // Example: You might open a dialog to get new room type details
        // Roomtype newRoomType = showAddRoomTypeDialog(); // A hypothetical method
        // if (newRoomType != null) {
        //     try {
        //         Roomtype savedRoomType = roomTypeService.addRoomType(newRoomType);
        //         System.out.println("Added new RoomType: " + savedRoomType.getTypeName());
        //         loadRoomTypeCards(); // Refresh the display to show the new card
        //     } catch (RuntimeException e) {
        //         System.err.println("Failed to add RoomType: " + e.getMessage());
        //         // Handle error, e.g., show an alert
        //     }
        // }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleAddRoomClick(ActionEvent actionEvent) {
        if(currentlyDisplayedRoomType == null){
            showAlert(Alert.AlertType.WARNING, "Không có loại phòng", "Vui lòng chọn một loại phòng để chỉnh sửa.");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddRoom.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            AddRoomController addRoomController = loader.getController();
            addRoomController.setUpRoomTypeName(currentlyDisplayedRoomType);
            addRoomController.setOnAddCallback(success -> {
                if (success) {
                    loadRoomTypeCards(); // Refresh the list of cards
                    // After update, re-display the updated details in the side panel
                    if (this.currentlyDisplayedRoomType != null && this.currentlyDisplayedRoomType.getId() != null) {
                        Roomtype a = roomTypeService.getRoomTypeById(this.currentlyDisplayedRoomType.getId());
                        if(a != null){
                            displayRoomTypeDetails(a);
                        }
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thêm thất bại", "Có lỗi xảy ra khi thêm phòng.");
                }
            });
            //        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainWindow.fxml"));
            //        Scene scene = new Scene(fxmlLoader.load(), 1360, 820);

            scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
            System.out.println(getClass().getResource("/CSS/style.css"));
            Stage stage = new Stage();
            stage.setTitle("Add Room");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e) {
            e.printStackTrace(); // Or throw a Deadpool-style panic attack
        }
    }
}