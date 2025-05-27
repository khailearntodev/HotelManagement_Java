package com.example.hotelmanagement.Views;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;

public class LoginController implements Initializable {

    @FXML
    private Label welcomeText;

    @FXML
    private ImageView carouselImage;

    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton loginButton;

    private final List<Image> imageList = List.of(
            new Image(getClass().getResource("/Images/sanh.jpeg").toExternalForm()),
            new Image(getClass().getResource("/Images/phong.jpg").toExternalForm()),
            new Image(getClass().getResource("/Images/hanhlang.jpg").toExternalForm())
    );


    private int currentIndex = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carouselImage.setImage(imageList.get(currentIndex));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), e -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            carouselImage.setImage(imageList.get(currentIndex));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        loginButton.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if ("admin".equals(username) && "1234".equals(password)) {
            showAlert("Login Successful!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Invalid username or password!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private ImageView closeIcon;

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }
}
