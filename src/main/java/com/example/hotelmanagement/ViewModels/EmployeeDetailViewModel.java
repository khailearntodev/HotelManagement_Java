package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.EmployeeDAO;
import com.example.hotelmanagement.DTO.EmployeeManagement_EmployeeDisplay;
import com.example.hotelmanagement.Models.Employee;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class EmployeeDetailViewModel {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Getter private final IntegerProperty employeeId = new SimpleIntegerProperty();
    @Getter private final StringProperty fullName = new SimpleStringProperty();
    @Getter private final ObjectProperty<Instant> dateOfBirth = new SimpleObjectProperty<>();
    @Getter private final StringProperty identityNumber = new SimpleStringProperty();
    @Getter private final StringProperty phoneNumber = new SimpleStringProperty();
    @Getter private final StringProperty address = new SimpleStringProperty();
    @Getter private final BooleanProperty gender = new SimpleBooleanProperty();
    @Getter private final ObjectProperty<Instant> startingDate = new SimpleObjectProperty<>();
    @Getter private final StringProperty email = new SimpleStringProperty();
    @Getter private final StringProperty contractType = new SimpleStringProperty();
    @Getter private final ObjectProperty<Instant> contractDate = new SimpleObjectProperty<>();
    @Getter private final ObjectProperty<BigDecimal> salaryRate = new SimpleObjectProperty<>();
    @Getter private final StringProperty avatar = new SimpleStringProperty();
    @Getter private final StringProperty position = new SimpleStringProperty();

    public EmployeeDetailViewModel()
    {
        this.employeeId.set(-1);
    }

    public EmployeeDetailViewModel(EmployeeManagement_EmployeeDisplay dto) {
        this.employeeId.set(dto.getEmployeeId());
        this.fullName.set(dto.getEmployeeName());
        this.dateOfBirth.set(dto.getBirthday());
        this.identityNumber.set(dto.getIdentityNumber());
        this.phoneNumber.set(dto.getPhoneNumber());
        this.address.set(dto.getAddress());
        this.gender.set(dto.isGender());
        this.startingDate.set(dto.getStartingDate());
        this.email.set(dto.getEmail());
        this.contractType.set(dto.getContractType());
        this.contractDate.set(dto.getContractDate());
        this.salaryRate.set(dto.getSalaryRate());
        this.avatar.set(dto.getAvatar());
        this.position.set(dto.getPosition());
    }

    public boolean saveEmployee() {
        Employee updated = new Employee();

        if (employeeId.get() != -1)
        {
            updated.setId(employeeId.get());
        }

        updated.setFullName(fullName.get());
        updated.setDateOfBirth(dateOfBirth.get());
        updated.setIdentityNumber(identityNumber.get());
        updated.setPhoneNumber(phoneNumber.get());
        updated.setAddress(address.get());
        updated.setGender(gender.get());
        updated.setStartingDate(startingDate.get());
        updated.setEmail(email.get());
        updated.setContractType(contractType.get());
        updated.setContractDate(contractDate.get());
        updated.setSalaryRate(salaryRate.get());
        updated.setAvatar(avatar.get());
        updated.setPosition(position.get());

        if (employeeId.get() == -1) {
            return employeeDAO.save(updated);
        } else {
            return employeeDAO.update(updated);
        }
    }
}
