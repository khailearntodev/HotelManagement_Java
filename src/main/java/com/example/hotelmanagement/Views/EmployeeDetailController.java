package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.EmployeeManagement_EmployeeDisplay;
import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.ViewModels.EmployeeDetailViewModel;
import io.github.palexdev.materialfx.controls.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class EmployeeDetailController {
    @FXML private MFXTextField txtEmployeeId;
    @FXML private MFXTextField txtFullName;
    @FXML private MFXDatePicker dpDateOfBirth;
    @FXML private MFXTextField txtIdentityNumber;
    @FXML private MFXTextField txtPhoneNumber;
    @FXML private MFXTextField txtAddress;
    @FXML private MFXComboBox<String> comboGender;
    @FXML private MFXDatePicker dpStartingDate;
    @FXML private MFXTextField txtEmail;
    @FXML private MFXTextField txtContractType;
    @FXML private MFXDatePicker dpContractDate;
    @FXML private MFXTextField txtSalaryRate;
    @FXML private MFXTextField txtPosition;
    @FXML private ImageView imageAvatar;
    @FXML private MFXButton btnViewAccount;
    @Setter private boolean readOnlyMode = false;
    @FXML private ImageView avatarImageView;
    @FXML private MFXButton btnUploadAvatar;
    private File selectedAvatarFile;

    private EmployeeDetailViewModel viewModel;
    @Setter private Runnable onSaveSuccess;
    private final Locale vietnamLocale = new Locale("vi", "VN");
    private final DateTimeFormatter vietnameseFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Hàm tạo StringConverter cho DatePicker

    private StringConverter<LocalDate> getVietnameseDateConverter() {
        return new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date == null) return "";
                return vietnameseFormatter.format(date);
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;
                try {
                    return LocalDate.parse(string, vietnameseFormatter);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        };
    }
    @FXML
    private void onViewAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Views/EmployeeAccountInformationView.fxml"));
            Parent root = loader.load();

            EmployeeAccountInformationController controller = loader.getController();
            controller.loadAccount(viewModel.getEmployeeId().get());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thông tin tài khoản");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadEmployee(EmployeeManagement_EmployeeDisplay dto) {
        this.viewModel = (dto == null) ? new EmployeeDetailViewModel() : new EmployeeDetailViewModel(dto);
        bindFields();
    }

    private void bindFields() {

        Bindings.bindBidirectional(txtEmployeeId.textProperty(), viewModel.getEmployeeId(), new NumberStringConverter());
        txtFullName.textProperty().bindBidirectional(viewModel.getFullName());
        txtIdentityNumber.textProperty().bindBidirectional(viewModel.getIdentityNumber());
        txtPhoneNumber.textProperty().bindBidirectional(viewModel.getPhoneNumber());
        txtAddress.textProperty().bindBidirectional(viewModel.getAddress());
        txtEmail.textProperty().bindBidirectional(viewModel.getEmail());
        txtContractType.textProperty().bindBidirectional(viewModel.getContractType());
        txtPosition.textProperty().bindBidirectional(viewModel.getPosition());

        comboGender.getItems().setAll("Nam", "Nữ");
        Boolean genderBool = viewModel.getGender().get();
        String genderText = (genderBool != null && genderBool) ? "Nam" : "Nữ";

        Platform.runLater(() -> comboGender.setValue(genderText));

        comboGender.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.getGender().set(newVal.equals("Nam"));
            }
        });

        viewModel.getGender().addListener((obs, oldVal, newVal) -> {
            String newGenderText = (newVal != null && newVal) ? "Nam" : "Nữ";
            if (!newGenderText.equals(comboGender.getValue())) {
                comboGender.setValue(newGenderText);
            }
        });

        txtSalaryRate.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal == null || newVal.isBlank()) {
                    viewModel.getSalaryRate().set(BigDecimal.ZERO);
                } else {
                    viewModel.getSalaryRate().set(new BigDecimal(newVal));
                }
            } catch (NumberFormatException ignored) {}
        });

        viewModel.getSalaryRate().addListener((obs, oldVal, newVal) -> {
            txtSalaryRate.setText(newVal != null ? newVal.toPlainString() : "");
        });

        if (viewModel.getSalaryRate().get() != null) {
            txtSalaryRate.setText(viewModel.getSalaryRate().get().toPlainString());
        }

        if (viewModel.getDateOfBirth().get() != null)
            dpDateOfBirth.setValue(viewModel.getDateOfBirth().get().atZone(ZoneId.systemDefault()).toLocalDate());

        dpDateOfBirth.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                viewModel.getDateOfBirth().set(newVal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        });

        if (viewModel.getStartingDate().get() != null)
            dpStartingDate.setValue(viewModel.getStartingDate().get().atZone(ZoneId.systemDefault()).toLocalDate());

        dpStartingDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                viewModel.getStartingDate().set(newVal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        });

        if (viewModel.getContractDate().get() != null)
            dpContractDate.setValue(viewModel.getContractDate().get().atZone(ZoneId.systemDefault()).toLocalDate());

        dpContractDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                viewModel.getContractDate().set(newVal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        });

        if (viewModel.getAvatar().get() != null && !viewModel.getAvatar().get().isBlank()) {
            try {
                Image img = new Image(viewModel.getAvatar().get(), true);
                avatarImageView.setImage(img);
            } catch (Exception ignored) {}
        }

        if (readOnlyMode) {
            btnViewAccount.setDisable(true);
        }

        btnUploadAvatar.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn ảnh đại diện");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(btnUploadAvatar.getScene().getWindow());
            if (file != null) {
                selectedAvatarFile = file;
                avatarImageView.setImage(new Image(file.toURI().toString()));
                viewModel.getAvatar().set(file.toURI().toString());
            }
        });

    }

    @FXML
    private void onSave() {
        String phone = txtPhoneNumber.getText().trim();
        if (!phone.matches("\\d+")) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Số điện thoại không hợp lệ.");
            return;
        }

        String idNumber = txtIdentityNumber.getText().trim();
        if (!idNumber.matches("\\d+")) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "CMND/CCCD không hợp lệ. Vui lòng nhập tối đa 12 số.");
            return;
        }

        String salary = txtSalaryRate.getText().trim();
        if (!salary.matches("\\d+(\\.\\d+)?")) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Lương phải là số hợp lệ.");
            return;
        }

        boolean success = viewModel.saveEmployee();
        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setHeaderText(success ? "Thành công" : "Thất bại");
        alert.setContentText(success ? "Đã lưu thông tin nhân viên." : "Không thể lưu thông tin.");
        alert.showAndWait();

        if (success) {
            if (onSaveSuccess != null) onSaveSuccess.run();
            Stage stage = (Stage) txtFullName.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) txtFullName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
