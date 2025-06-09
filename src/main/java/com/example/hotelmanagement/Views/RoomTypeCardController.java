package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.ViewModels.RoomTypeViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.Locale;
import java.util.function.Consumer;

public class RoomTypeCardController {

    public VBox roomCardRoot;
    public MFXButton deleteButton;
    @FXML
    private ImageView roomTypeImageView;
    @FXML
    private Label typeNameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private MFXTextField priceTextField;
    @FXML
    private MFXTextField quantityTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private MFXTextField maxOccupancyTextField;
    private RoomTypeViewModel viewModel; // Store the view model

    private Consumer<RoomTypeViewModel> onCardClick; // Callback for click event

    private Consumer<RoomTypeViewModel> onDeleteClick;
    public void setRoomtypeData(RoomTypeViewModel viewModel) {
        this.viewModel = viewModel; // Store the view model
        if (viewModel == null) {
            clearCardData();
            return;
        }

        typeNameLabel.setText(viewModel.getTypeName());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        priceTextField.setText(currencyFormat.format(viewModel.getBasePrice()));
        priceTextField.setEditable(false);

        quantityTextField.setText("N/A");
        quantityTextField.setEditable(false);

        descriptionTextArea.setText(viewModel.getDescription());
        descriptionTextArea.setEditable(false);

        maxOccupancyTextField.setText(String.valueOf(viewModel.getMaxOccupancy()));
        maxOccupancyTextField.setEditable(false);

        String base64Image = viewModel.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
                Image image = new Image(new ByteArrayInputStream(decodedBytes));
                roomTypeImageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid Base64 image string for Roomtype: " + viewModel.getTypeName());
                roomTypeImageView.setImage(null);
            }
        } else {
            roomTypeImageView.setImage(null);
        }
        if (roomCardRoot != null) {
            roomCardRoot.setOnMouseClicked(event -> {
                if (onCardClick != null) {
                    onCardClick.accept(this.viewModel); // Invoke the callback with the view model
                }
            });
        }
        if(deleteButton != null){
            deleteButton.setOnAction(event ->{
                if(onDeleteClick != null){
                    onDeleteClick.accept(this.viewModel);
                }
            });
        }
    }

    /**
     * Sets the callback function to be executed when this card is clicked.
     * @param onCardClick A Consumer that accepts the RoomTypeViewModel of the clicked card.
     */
    public void setOnCardClick(Consumer<RoomTypeViewModel> onCardClick) {
        this.onCardClick = onCardClick;
    }

    private void clearCardData() {
        roomTypeImageView.setImage(null);
        typeNameLabel.setText("");
        priceTextField.setText("");
        quantityTextField.setText("");
        descriptionTextArea.setText("");
        maxOccupancyTextField.setText("");
    }

    public void handleDeleteRoomtype(Consumer<RoomTypeViewModel> onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }
}