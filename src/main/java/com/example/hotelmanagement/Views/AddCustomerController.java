package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.CustomerDAO;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Customertype;
import com.example.hotelmanagement.ViewModels.AddCustomerViewModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    @FXML private ToggleGroup IDGroup;
    @FXML private MFXRadioButton hochieuRatio;
    @FXML private MFXRadioButton cccdRatio;
    @FXML private MFXRadioButton maleRatio;
    @FXML private MFXRadioButton femaleRatio;
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
                genderGroup.selectToggle(newVal ? maleRatio : femaleRatio);
            }
        });

        genderGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null && newToggle.getUserData() instanceof Boolean boolVal) {
                viewModel.getCustomerItem().setGender(boolVal);
            }
        });

        viewModel.getCustomerItem().identityTypeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                IDGroup.selectToggle(newVal.equals("CCCD") ? cccdRatio : hochieuRatio);
            }
        });

        IDGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null && newToggle.getUserData() instanceof String stringVal) {
                viewModel.getCustomerItem().setIdentityType(stringVal);
            }
        });
        IDGroup.selectToggle(viewModel.getCustomerItem().getIdentityType().equals("CCCD") ? cccdRatio : hochieuRatio);
        genderGroup.selectToggle(viewModel.getCustomerItem().getGender() ? maleRatio : femaleRatio);
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
        System.out.println(cccdTextField.getText());
        System.out.println(customerNameTextField.getText());
        System.out.println(dobDatepicker.getValue());
        System.out.println("Customer Type: " + customerTypeCombobox.getValue().getId());

        Customer customer = new Customer();
        customer.setIdentityNumber(cccdTextField.getText());
        customer.setFullName(customerNameTextField.getText());
        customer.setPhoneNumber(phoneNumberTextField.getText());
        customer.setCustomerAddress(addressTextField.getText());
        customer.setGender(genderGroup.getSelectedToggle().getUserData() == "true");
        customer.setIdentityType(IDGroup.getSelectedToggle().getUserData().toString());
        customer.setCustomerTypeID(customerTypeCombobox.getValue());
        customer.setDateOfBirth(dobDatepicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        viewModel.getParent().getCustomerList().add(customer);
        System.out.println(viewModel.getParent().getCustomerList().size());
    }

    public void handleClear(MouseEvent mouseEvent) {
        viewModel.resetCustomer();
        bindViewModel();
        setupCustomerTypeComboBox();
    }
}