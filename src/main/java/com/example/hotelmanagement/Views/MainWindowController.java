package com.example.hotelmanagement.Views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private StackPane barContainer;

    private boolean isAdmin = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (isAdmin) {
            loadBarView("/com/example/hotelmanagement/Views/UserControls/AdminBar.fxml");
        } else {
            loadBarView("/com/example/hotelmanagement/Views/UserControls/EmployeeBar.fxml");
        }
    }

    private void loadBarView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent barView = loader.load();
            barContainer.getChildren().clear();
            barContainer.getChildren().add(barView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
