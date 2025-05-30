package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Customertype;
import com.example.hotelmanagement.ViewModels.AddCustomerViewModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    @FXML private Label messageLabel;
    @FXML private ToggleGroup IDGroup;
    @FXML private MFXRadioButton hochieuRadio;
    @FXML private MFXRadioButton cccdRadio;
    @FXML private MFXRadioButton maleRadio;
    @FXML private MFXRadioButton femaleRadio;
    @FXML private ToggleGroup genderGroup;
    @FXML private MFXTextField addressTextField;
    @FXML private MFXComboBox<Customertype> customerTypeCombobox;
    @FXML private MFXTextField phoneNumberTextField;
    @FXML private MFXTextField customerNameTextField;
    @FXML private MFXTextField cccdTextField;
    @FXML private VBox AddCustomerVBox;
    @FXML private ImageView closeIcon;
    @FXML private MFXDatePicker dobDatepicker;

    private ResourceBundle bundle;
    private AddCustomerViewModel viewModel;

    public void setViewModel(AddCustomerViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
        setupCustomerTypeComboBox();
    }

    private void bindViewModel() {
        viewModel.getCustomerItem().genderProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                genderGroup.selectToggle(newVal ? maleRadio : femaleRadio);
            }
        });

        genderGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null && newToggle.getUserData() instanceof Boolean boolVal) {
                viewModel.getCustomerItem().setGender(boolVal);
            }
        });

        viewModel.getCustomerItem().identityTypeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                IDGroup.selectToggle(newVal.equals("CCCD") ? cccdRadio : hochieuRadio);
            }
        });

        IDGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null && newToggle.getUserData() instanceof String stringVal) {
                viewModel.getCustomerItem().setIdentityType(stringVal);
            }
        });
        IDGroup.selectToggle(viewModel.getCustomerItem().getIdentityType().equals("CCCD") ? cccdRadio : hochieuRadio);
        genderGroup.selectToggle(viewModel.getCustomerItem().getGender() ? maleRadio : femaleRadio);
        cccdTextField.textProperty().bindBidirectional(viewModel.getCustomerItem().identityNumberProperty());
        customerNameTextField.textProperty().bindBidirectional(viewModel.getCustomerItem().fullNameProperty());
        phoneNumberTextField.textProperty().bindBidirectional(viewModel.getCustomerItem().phoneNumberProperty());
        addressTextField.textProperty().bindBidirectional(viewModel.getCustomerItem().customerAddressProperty());

        dobDatepicker.valueProperty().bindBidirectional(viewModel.getCustomerItem().dateOfBirthProperty());
        customerTypeCombobox.valueProperty().bindBidirectional(viewModel.getCustomerItem().customerTypeIDProperty());

    }

    private void setupCustomerTypeComboBox() {
        customerTypeCombobox.setItems(viewModel.getCustomertypeList());

        customerTypeCombobox.setConverter(new StringConverter<Customertype>() {
            @Override
            public String toString(Customertype customertype) {
                return customertype != null ? customertype.getTypeName() : "";
            }

            @Override
            public Customertype fromString(String string) {
                return viewModel.getCustomertypeList().stream()
                        .filter(ct -> ct.getTypeName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        customerTypeCombobox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.getCustomerItem().setCustomerTypeID(newVal);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale vietnameseLocale = new Locale("vi", "VN");
        Locale.setDefault(vietnameseLocale);
        setupMFXDatePicker();
        Platform.runLater(() -> AddCustomerVBox.requestFocus());
        messageLabel.setVisible(false);
        setInfoTextFieldDisable(true);
    }

    public void setInfoTextFieldDisable(boolean disable) {
        cccdTextField.setDisable(!disable);
        customerNameTextField.setDisable(disable);
        phoneNumberTextField.setDisable(disable);
        addressTextField.setDisable(disable);
        dobDatepicker.setDisable(disable);
        customerTypeCombobox.setDisable(disable);
        femaleRadio.setDisable(disable);
        hochieuRadio.setDisable(!disable);
        maleRadio.setDisable(disable);
        cccdRadio.setDisable(!disable);
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

    public void handleAddCustomer(MouseEvent mouseEvent) {
        Customer customer = new Customer();
        customer.setIdentityNumber(cccdTextField.getText());
        customer.setFullName(customerNameTextField.getText());
        customer.setPhoneNumber(phoneNumberTextField.getText());
        customer.setCustomerAddress(addressTextField.getText());
        customer.setGender(genderGroup.getSelectedToggle().getUserData() == "true");
        customer.setIdentityType(IDGroup.getSelectedToggle().getUserData().toString());
        customer.setCustomerTypeID(customerTypeCombobox.getValue());
        customer.setDateOfBirth(dobDatepicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        setInfoTextFieldDisable(true);
        messageLabel.setVisible(false);
        viewModel.getParent().getCustomerList().add(customer);
        viewModel.resetCustomer();
        bindViewModel();
        setupCustomerTypeComboBox();
    }

    public void handleClear(MouseEvent mouseEvent) {
        viewModel.resetCustomer();
        bindViewModel();
        setupCustomerTypeComboBox();
        setInfoTextFieldDisable(true);
        messageLabel.setVisible(false);
    }

    public void checkCustomerInfo(MouseEvent mouseEvent) {
        setInfoTextFieldDisable(false);
        boolean found = viewModel.isCustomerExist(cccdTextField.getText(), IDGroup.getSelectedToggle().getUserData().toString());
        messageLabel.setVisible(true);
        if (found) {
            messageLabel.setText("Tồn tại thông tin khách hàng");
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setText("Không tồn tại thông tin khách hàng");
            messageLabel.setStyle("-fx-text-fill: black;");
        }
        bindViewModel();
        setupCustomerTypeComboBox();
    }
}