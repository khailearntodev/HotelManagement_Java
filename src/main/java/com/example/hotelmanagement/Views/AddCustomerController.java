package com.example.hotelmanagement.Views;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    @FXML private VBox AddCustomerVBox;
    @FXML private ImageView closeIcon;
    @FXML private MFXDatePicker dobDatepicker; // MFXDatePicker

    private ResourceBundle bundle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale vietnameseLocale = new Locale("vi", "VN");
        Locale.setDefault(vietnameseLocale);
        setupMFXDatePicker();
        Platform.runLater(() -> AddCustomerVBox.requestFocus());
    }

    private void setupMFXDatePicker() {
        if (dobDatepicker != null) {
            Locale vietnameseLocale = new Locale("vi", "VN");

            dobDatepicker.setConverterSupplier(() -> new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", vietnameseLocale);
                    return date != null ? date.format(formatter) : "";
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.trim().isEmpty()) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", vietnameseLocale);
                            return LocalDate.parse(string, formatter);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                }
            });

            try {
                Method setLocaleMethod = dobDatepicker.getClass().getMethod("setLocale", Locale.class);
                setLocaleMethod.invoke(dobDatepicker, vietnameseLocale);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }
}