package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DAO.ServiceDAO;
import com.example.hotelmanagement.Models.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import com.example.hotelmanagement.*;
import com.example.hotelmanagement.DTO.ServiceDisplay;
import com.example.hotelmanagement.Models.Service;

import java.math.BigDecimal;
import java.util.List;

public class ServiceManagementViewModel {

    private final ObservableList<ServiceDisplay> services = FXCollections.observableArrayList();
    private final ServiceDAO dao = new ServiceDAO();

    // Change the return type to javafx.collections.ObservableList
    public ObservableList<ServiceDisplay> getServiceList() {
        return services;
    }

    public void loadServices() {
        services.clear();
        List<Service> list = dao.getAll();
        for (Service s : list) {
            services.add(new ServiceDisplay(s));
        }
        System.out.println(services.size());
        System.out.println("Da load services done!");
    }

    public void addService(String name, BigDecimal price, String imageLink) {
        Service s = new Service();
        s.setServiceName(name);
        s.setPrice(price);
        s.setServiceImage(imageLink);
        s.setIsDeleted(false);
        dao.save(s);
        loadServices();
    }

    public void deleteService(ServiceDisplay sd) {
        dao.softDelete(sd.getMaDichVu());
        loadServices();
    }

    public boolean updateService(ServiceDisplay sd) {
        Service s = dao.findById(sd.getMaDichVu());
        if (s == null) return false;
        s.setServiceName(sd.getTenDichVu());
        s.setPrice(sd.getGiaDichVu());
        s.setServiceImage(sd.getImageLink());
        s.setIsDeleted(false);
        boolean success = dao.update(s);
        if (success) loadServices();
        return success;
    }

}