package com.example.hotelmanagement.ViewModels;
import com.example.hotelmanagement.DAO.CustomerDAO;
import com.example.hotelmanagement.DAO.CustomerTypeDAO;
import com.example.hotelmanagement.DTO.CustomerInput;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Customertype;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

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

    public boolean isCustomerExist(String identityNumber, String identityType) {
        CustomerDAO customerDAO = new CustomerDAO();
        customer = customerDAO.findByIdentityNumber(identityNumber, identityType);
        if (customer == null) {
            customer = new Customer();
            resetCustomer();
            customer.setIdentityNumber(identityNumber);
            customer.setIdentityType(identityType);
            customerItem = new CustomerInput(customer);
            return false;
        } else {
            customerItem = new CustomerInput(customer);
            return true;
        }
    }
}
