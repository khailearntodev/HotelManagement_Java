package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.ViewModels.LoginViewModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainWindowController{

    @FXML
    private StackPane barContainer;

    @FXML
    private StackPane contentContainer;

    @FXML
    private Label employeeName;

    @FXML
    private Label tabName;

    private SlideBarController slideBarController;

    public void setTabName(String name)
    {
        tabName.setText(name);
    }

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/UserControls/SlideBar.fxml"));
            Node slideBar = loader.load();
            slideBarController = loader.getController();
            slideBarController.setMainWindowController(this);
            barContainer.getChildren().add(slideBar);
            String name = LoginViewModel.employeeName;
            employeeName.setText(name);

            employeeName.setOnMouseClicked(event -> {
                openEmployeeDetailView();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEmployeeDetailView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Views/EmployeeDetailView.fxml"));
            Parent root = loader.load();

            EmployeeDetailController controller = loader.getController();
            controller.setReadOnlyMode(true);
            controller.loadEmployee(LoginViewModel.loggedInEmployee);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thông tin nhân viên");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initializeRolePermissions(int roleId) {
        if (slideBarController != null) {
            slideBarController.initializePermissionsForRole(roleId);
        } else {
            System.out.println("SlideBarController chưa được khởi tạo.");
        }
    }

    public void loadContentView(String fxmlPath) {
        try {
            //FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent view = loader.load();

            contentContainer.getChildren().clear();
            contentContainer.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBarView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent barView = loader.load();
            barContainer.getChildren().clear();
            barContainer.getChildren().add(barView);
            barView.getStylesheets().add(getClass().getResource("CSS/style.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCloseButtonClicked()
    {
        Stage stage = (Stage) barContainer.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onButtonMinimize()
    {
        Stage stage = (Stage) barContainer.getScene().getWindow();
        stage.setIconified(true);
    }


    @FXML
    public void onLogoutClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất không?");
        alert.setContentText("Tất cả các thay đổi chưa lưu sẽ bị mất.");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-border-color: black; " +
                        "-fx-border-width: 2; "
        );
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.UNDECORATED);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/LoginView.fxml"));
                        Parent root = loader.load();
                        Scene loginScene = new Scene(root);

                        Stage currentStage = (Stage) barContainer.getScene().getWindow();
                        currentStage.setScene(loginScene);
                        currentStage.centerOnScreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                pause.play();
            }
        });
    }

    public void handleEmployeeNameClick(javafx.scene.input.MouseEvent mouseEvent) {
        openEmployeeDetailView();
    }
}