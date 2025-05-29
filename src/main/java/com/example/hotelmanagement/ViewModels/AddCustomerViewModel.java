package com.example.hotelmanagement.ViewModels;
import com.example.hotelmanagement.DAO.CustomerTypeDAO;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Customertype;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class AddCustomerViewModel {
    @Getter
    private ObservableList<Customertype> customertypeList;
    @Setter
    @Getter
    private BookingViewModel parent;
    private Customer customer;
    @Getter
    private CustomerInput customerItem;

    public AddCustomerViewModel() {
        List<Customertype> customertypes = new CustomerTypeDAO().getAll();
        customertypeList = FXCollections.observableList(customertypes);
        customer = new Customer();
        resetCustomer();
    }

    public void resetCustomer() {
        customer.setFullName("");
        customer.setDateOfBirth(null);
        customer.setIdentityNumber("");
        customer.setPhoneNumber("");
        customer.setGender(true);
        customer.setIdentityType("CCCD");
        customer.setCustomerAddress("");
        customer.setCustomerTypeID(null);
        customerItem = new CustomerInput(customer);
    }

    public class CustomerInput {
        private final StringProperty fullName = new SimpleStringProperty("");
        private final ObjectProperty<LocalDate> dateOfBirth = new SimpleObjectProperty<>();
        private final StringProperty identityNumber = new SimpleStringProperty();
        private final BooleanProperty gender = new SimpleBooleanProperty();
        private final StringProperty identityType = new SimpleStringProperty();
        private final StringProperty phoneNumber = new SimpleStringProperty();
        private final StringProperty customerAddress = new SimpleStringProperty();
        private final ObjectProperty<Customertype> customerTypeID = new SimpleObjectProperty<>();

        public CustomerInput(Customer customer) {
            this.fullName.set(customer.getFullName());
            Instant dob = customer.getDateOfBirth();
            if (dob != null) {
                this.dateOfBirth.set(dob.atZone(ZoneId.systemDefault()).toLocalDate());
            } else {
                this.dateOfBirth.set(null);
            }
            this.identityNumber.set(customer.getIdentityNumber());
            this.gender.set(customer.getGender());
            this.identityType.set(customer.getIdentityType());
            this.phoneNumber.set(customer.getPhoneNumber());
            this.customerAddress.set(customer.getCustomerAddress());
            this.customerTypeID.set(customer.getCustomerTypeID());
        }

        public String getFullName() {
            return fullName.get();
        }

        public StringProperty fullNameProperty() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName.set(fullName);
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth.get();
        }

        public ObjectProperty<LocalDate> dateOfBirthProperty() {
            return dateOfBirth;
        }

        public void setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth.set(dateOfBirth);
        }

        public String getIdentityNumber() {
            return identityNumber.get();
        }

        public StringProperty identityNumberProperty() {
            return identityNumber;
        }

        public void setIdentityNumber(String identityNumber) {
            this.identityNumber.set(identityNumber);
        }

        public boolean getGender() {
            return gender.get();
        }

        public BooleanProperty genderProperty() {
            return gender;
        }

        public void setGender(boolean gender) {
            this.gender.set(gender);
        }

        public String getIdentityType() {
            return identityType.get();
        }

        public StringProperty identityTypeProperty() {
            return identityType;
        }

        public void setIdentityType(String identityType) {
            this.identityType.set(identityType);
        }

        public String getPhoneNumber() {
            return phoneNumber.get();
        }

        public StringProperty phoneNumberProperty() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber.set(phoneNumber);
        }

        public String getCustomerAddress() {
            return customerAddress.get();
        }

        public StringProperty customerAddressProperty() {
            return customerAddress;
        }

        public void setCustomerAddress(String customerAddress) {
            this.customerAddress.set(customerAddress);
        }

        public Object getCustomerTypeID() {
            return customerTypeID.get();
        }

        public ObjectProperty<Customertype> customerTypeIDProperty() {
            return customerTypeID;
        }

        public void setCustomerTypeID(Customertype customerTypeID) {
            this.customerTypeID.set(customerTypeID);
        }
    }
}
