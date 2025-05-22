package com.example.hotelmanagement.Views;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BookingController implements Initializable {
    @FXML private VBox BookingVBox;
    @FXML private ImageView closeIcon;
    @FXML private MFXButton addCustomerBtn;

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> BookingVBox.requestFocus());
    }

    public void handleAddCustomer(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/AddCustomerView.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
