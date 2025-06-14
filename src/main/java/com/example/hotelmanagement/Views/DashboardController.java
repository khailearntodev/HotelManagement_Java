package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.Dashboard_ActivityItem;
import com.example.hotelmanagement.DTO.Dashboard_BookingDisplay;
import com.example.hotelmanagement.ViewModels.DashboardViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardController {
    @FXML
    private Label totalNewBookingToday;
    @FXML
    private Label totalCheckInToday;
    @FXML
    private Label totalCheckOutToday;
    @FXML
    private Label totalRoomInUse;
    @FXML
    private Label realTimeClock;
    @FXML
    private Label greetingLabel;
    @FXML
    private MFXComboBox<String> statusComboBox;
    @FXML
    private MFXTextField searchField;
    @FXML
    private TableView<Dashboard_BookingDisplay> bookingTable;
    @FXML
    private TableColumn<Dashboard_BookingDisplay, String> bookingIdColumn;
    @FXML
    private TableColumn<Dashboard_BookingDisplay, String> customerNameColumn;
    @FXML
    private TableColumn<Dashboard_BookingDisplay, Integer> roomNumberColumn;
    @FXML
    private TableColumn<Dashboard_BookingDisplay, String> roomTypeColumn;
    @FXML
    private TableColumn<Dashboard_BookingDisplay, String> checkInDateColumn;
    @FXML
    private TableColumn<Dashboard_BookingDisplay, String> checkOutDateColumn;
    @FXML
    private TableColumn<Dashboard_BookingDisplay, String> statusColumn;
    @FXML
    private BarChart<String, Number> roomChart;
    @FXML
    private CategoryAxis roomTypeAxis;
    @FXML
    private NumberAxis roomValueAxis;
    @FXML
    private MFXButton btnExportPNG;
    @FXML
    private MFXButton btnRefresh;
    @FXML
    private AnchorPane dashboardPane;

    @FXML
    private ListView<Dashboard_ActivityItem> recentActivitiesList;

    private DashboardViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new DashboardViewModel();
        String css = getClass().getResource("/CSS/dashboard.css").toExternalForm();
        if (css != null) {
            dashboardPane.getStylesheets().add(css);
        } else {
            System.err.println("Không tìm thấy file CSS!");
        }

        // Binding labels
        totalNewBookingToday.textProperty().bind(viewModel.totalNewBookingTodayProperty().asString());
        totalCheckInToday.textProperty().bind(viewModel.totalCheckInTodayProperty().asString());
        totalCheckOutToday.textProperty().bind(viewModel.totalCheckOutTodayProperty().asString());
        totalRoomInUse.textProperty().bind(viewModel.totalRoomInUseProperty().asString());
        realTimeClock.textProperty().bind(viewModel.currentTimeProperty());
        greetingLabel.textProperty().bind(viewModel.greetingMessageProperty());

        // Cấu hình cột cho TableView
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDateString"));
        checkOutDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDateString"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusText"));

        searchField.textProperty().bindBidirectional(viewModel.searchKeywordProperty());
        statusComboBox.setItems(viewModel.getRoomTypes());
        statusComboBox.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

        bookingTable.setItems(viewModel.getFilteredBookingList());

        // === Biểu đồ BarChart (roomChart) ===
        roomChart.setLegendVisible(false);
        roomChart.getData().add(viewModel.getChartSeries());
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : roomChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    Node node = data.getNode();
                    if (node != null) {
                        Tooltip tooltip = new Tooltip(
                                "Loại phòng: " + data.getXValue() + "\n" +
                                        "Số lượng: " + data.getYValue()
                        );
                        tooltip.setStyle(
                                "-fx-background-color: rgba(255, 255, 255, 0.8);" +
                                        "-fx-text-fill: black;" +
                                        "-fx-padding: 5px;" +
                                        "-fx-background-radius: 5px;" +
                                        "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.2), 4, 0, 0, 1 );"
                        );
                        Tooltip.install(data.getNode(), tooltip);
                        node.setOnMouseEntered(e -> {
                            Tooltip.install(node, tooltip);
                            tooltip.show(node, e.getScreenX(), e.getScreenY() + 10);
                        });
                        node.setOnMouseExited(e -> {
                            tooltip.hide();
                        });
                    }
                }
            }
        });


        // === ListView Recent Activities ===
        recentActivitiesList.setItems(viewModel.getRecentActivities());
        recentActivitiesList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Dashboard_ActivityItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Label dấu chấm ở bên trái
                    Label dotLabel = new Label("·");
                    dotLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #888;");
                    dotLabel.setMinWidth(20);
                    dotLabel.setMaxWidth(20);
                    dotLabel.setAlignment(javafx.geometry.Pos.CENTER);

                    // VBox chứa thời gian và mô tả
                    VBox vbox = new VBox();
                    vbox.setSpacing(2);

                    Label timeLabel = new Label(item.getFormattedTimestamp());
                    timeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

                    Label contentLabel = new Label(item.getDescription() + " - Phòng " + item.getRoomNumber());
                    contentLabel.setStyle("-fx-text-fill: #666;");

                    vbox.getChildren().addAll(timeLabel, contentLabel);

                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.getChildren().addAll(dotLabel, vbox);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    setGraphic(hbox);
                }
            }
        });
        btnRefresh.setOnAction(e -> viewModel.loadDashboardData());
        btnExportPNG.setOnAction(e -> viewModel.exportSnapshot(dashboardPane));
    }
}
