package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.ViewModels.CancelBookingViewModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CancelBookingController implements Initializable {
    @FXML private Label depositLabel;
    @FXML private Label startEndDateLabel;
    @FXML private Label roomTypeNameLabel;
    @FXML private Label roomNumberLabel;
    @FXML private Label customerName;
    @FXML private Label messageLabel;
    @FXML private MFXTextField cancelCodeTextField;
    @FXML private VBox customerInforVBox;
    @FXML private VBox cancelBookingVBox;

    private CancelBookingViewModel viewModel;
    void setViewModel(CancelBookingViewModel viewModel) {
        this.viewModel = viewModel;
        setUp();
    }

    private void setUp() {
        messageLabel.setVisible(false);
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelBookingVBox.getScene().getWindow();
        stage.close();
    }

    public void showInfo(MouseEvent mouseEvent) {
        boolean isExist = viewModel.findPreBooking(cancelCodeTextField.getText());
        if (isExist) {
            customerName.textProperty().bind(viewModel.getPreBookingDisplay().customerNameProperty());
            roomNumberLabel.textProperty().bind(viewModel.getPreBookingDisplay().roomNumberProperty().asString());
            roomTypeNameLabel.textProperty().bind(viewModel.getPreBookingDisplay().roomTypeProperty());
            startEndDateLabel.textProperty().bind(viewModel.getPreBookingDisplay().start_endDateProperty());
            depositLabel.textProperty().bind(viewModel.getPreBookingDisplay().depositProperty());
            customerInforVBox.setVisible(true);
            customerInforVBox.setManaged(true);
            messageLabel.setVisible(false);
            Stage stage = (Stage) cancelBookingVBox.getScene().getWindow();
            stage.sizeToScene();
        } else {
            messageLabel.setVisible(true);
            customerInforVBox.setVisible(false);
            customerInforVBox.setManaged(false);
            Stage stage = (Stage) cancelBookingVBox.getScene().getWindow();
            stage.sizeToScene();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleDeletePreBooking(MouseEvent mouseEvent) {
        viewModel.deletePrebooking();
        Stage stage = (Stage) cancelBookingVBox.getScene().getWindow();
        stage.close();
    }
}
