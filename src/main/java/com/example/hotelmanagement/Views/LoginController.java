package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Useraccount;
import com.example.hotelmanagement.ViewModels.LoginViewModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private ImageView carouselImage;

    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton loginButton;

    @FXML
    private ImageView closeIcon;

    private final List<Image> imageList = List.of(
            new Image(getClass().getResource("/Images/sanh1.jpg").toExternalForm()),
            new Image(getClass().getResource("/Images/sanh2.jpg").toExternalForm()),
            new Image(getClass().getResource("/Images/sanh3.jpg").toExternalForm()),
            new Image(getClass().getResource("/Images/sanh4.jpg").toExternalForm())
    );

    private int currentIndex = 0;

    private final LoginViewModel viewModel = new LoginViewModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setDefaultButton(true);

        // Carousel ảnh
        carouselImage.setImage(imageList.get(currentIndex));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), e -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            carouselImage.setImage(imageList.get(currentIndex));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());

        loginButton.disableProperty().bind(viewModel.loginDisabledProperty());

        viewModel.loginMessageProperty().addListener((obs, oldMsg, newMsg) -> {
            if (newMsg != null && !newMsg.isEmpty()) {
                Platform.runLater(() -> {
                    if (newMsg.equalsIgnoreCase("Đăng nhập thành công!")) {
                        try {
                            // Lấy user sau khi login thành công
                            Useraccount currentUser = viewModel.getLoggedInUser();
                            if (currentUser == null) {
                                // Không nên xảy ra nhưng kiểm tra an toàn
                                return;
                            }
                            int roleId = currentUser.getRoleID().getId();

                            Stage currentStage = (Stage) loginButton.getScene().getWindow();
                            currentStage.close();

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/MainWindow.fxml"));
                            Parent root = loader.load();

                            MainWindowController controller = loader.getController();
                            controller.initializeRolePermissions(roleId);

                            Rectangle clip = new Rectangle(1360, 820);
                            clip.setArcWidth(20);
                            clip.setArcHeight(20);
                            root.setClip(clip);

                            Scene scene = new Scene(root);
                            scene.setFill(Color.TRANSPARENT);

                            Stage stage = new Stage();
                            stage.initStyle(StageStyle.TRANSPARENT);
                            stage.setScene(scene);
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Login");
                        alert.setHeaderText(null);
                        alert.setContentText(newMsg);
                        alert.showAndWait();
                    }
                });
            }
        });
        loginButton.setOnAction(e -> viewModel.login());
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }
}
