package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.EmployeeManagement_EmployeeDisplay;
import com.example.hotelmanagement.DTO.EmployeeManagement_EmployeeDisplay;
import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.ViewModels.EmployeeManagementViewModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeManagementController implements Initializable {

    // ViewModel
    private final EmployeeManagementViewModel viewModel = new EmployeeManagementViewModel();

    // FXML Bindings
    @FXML private ComboBox<String> cbPosition;
    @FXML private ComboBox<String> cbGender;
    @FXML private MFXTextField searchField;
    @FXML private TableView<EmployeeManagement_EmployeeDisplay> tableEmployees;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, ?> colCheck;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, Integer> colId;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, String> colName;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, String> colChucVu;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, String> colNgayTuyen;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, String> colHopDong;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, String> colHanHopDong;
    @FXML private TableColumn<EmployeeManagement_EmployeeDisplay, Number> colHeSoLuong;

    @FXML private Button btnAdd;
    @FXML private Button btnDelete;
    @FXML private Button btnExport;
    private Stage employeeDetailStage = null;

    private void openEmployeeDetail(EmployeeManagement_EmployeeDisplay employeeDTO) {
        if (employeeDetailStage != null && employeeDetailStage.isShowing()) {
            employeeDetailStage.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Views/EmployeeDetailView.fxml"));
            Parent root = loader.load();

            EmployeeDetailController controller = loader.getController();
            controller.loadEmployee(employeeDTO);

            controller.setOnSaveSuccess(() -> viewModel.loadEmployees());

            employeeDetailStage = new Stage();
            employeeDetailStage.setScene(new Scene(root));
            employeeDetailStage.setTitle("Chi tiết nhân viên");
            employeeDetailStage.initStyle(StageStyle.UNDECORATED);

            employeeDetailStage.setOnHidden(e -> employeeDetailStage = null);

            employeeDetailStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupTable();
        setupBindings();
        setupEventHandlers();
    }

    private void setupComboBoxes() {
        ObservableList<String> originalList = viewModel.getPositionList();
        ObservableList<String> extendedList = FXCollections.observableArrayList();

        extendedList.add("Chức vụ");
        extendedList.addAll(originalList);

        cbPosition.setItems(extendedList);
        cbPosition.getSelectionModel().selectFirst();

        cbPosition.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.setSelectedPosition(newVal);
            // Gọi filter nếu cần
        });

        cbGender.setItems(FXCollections.observableArrayList("Giới tính", "Nam", "Nữ"));
        cbGender.getSelectionModel().selectFirst();
    }



    private void setupTable() {
        // Table column bindings
        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEmployeeId()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmployeeName()));
        colChucVu.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPosition()));
        colNgayTuyen.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getStartingDate() != null ? data.getValue().getStartingDate().toString() : ""));
        colHopDong.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContractType()));
        colHanHopDong.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContractDate() != null ? data.getValue().getContractDate().toString() : ""));
        colHeSoLuong.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getSalaryRate()));

        // Table data
        tableEmployees.setItems(viewModel.getFilteredList());

        // Gán selected employee
        tableEmployees.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            viewModel.selectedEmployeeProperty().set(newSel);
        });
    }

    private void setupBindings() {
        viewModel.searchKeywordProperty().bind(searchField.textProperty());
        viewModel.selectedPositionProperty().bind(cbPosition.valueProperty());
        viewModel.selectedGenderProperty().bind(cbGender.valueProperty());
    }

    private void setupEventHandlers() {
        btnExport.setOnAction(e -> {
            Window window = btnExport.getScene().getWindow();
            viewModel.exportToExcel(window);
        });

        btnDelete.setOnAction(e -> {
            boolean deleted = viewModel.deleteSelectedEmployee();
            if (!deleted) {
                showAlert(Alert.AlertType.WARNING, "Vui lòng chọn một nhân viên để xóa.");
            }
        });

        btnAdd.setOnAction(e -> openEmployeeDetail(null));

        tableEmployees.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                EmployeeManagement_EmployeeDisplay selected = tableEmployees.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openEmployeeDetail(selected);
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
