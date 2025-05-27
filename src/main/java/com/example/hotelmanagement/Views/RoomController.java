package com.example.hotelmanagement.Views;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;

import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class RoomController implements Initializable {
    @FXML
//    public MFXTableView<Room> tableView;
    @Override
    public  void initialize(URL url, ResourceBundle resourceBundle){

        // Define columns
//        MFXTableColumn<Room> roomNumberCol = new MFXTableColumn<>("Room Number", true);
//        MFXTableColumn<Room> roomTypeCol = new MFXTableColumn<>("Room Type", true);
//
//        // Set how to fetch the data for each column
//        roomNumberCol.setRowCellFactory(_ -> new MFXTableRowCell<>(Room::getRoomNumber));
//        roomTypeCol.setRowCellFactory(_ -> new MFXTableRowCell<>(Room::getRoomType));
//
//        // Add columns
//        tableView.getTableColumns().add(roomNumberCol);
//        tableView.getTableColumns().add(roomTypeCol);
//
//        // Add data
//        tableView.setItems(FXCollections.observableArrayList(
//                        new Room("101", "Deluxe"),
//                        new Room("102", "Standard"),
//                        new Room("201", "Suite"),
//                        new Room("301", "Economy")
//        ));
    }
}
