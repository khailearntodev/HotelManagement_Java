package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.ViewModels.AddCustomerViewModel;
import com.example.hotelmanagement.ViewModels.BookingCalendarViewModel;
import com.example.hotelmanagement.ViewModels.BookingViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.Setter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BookingController implements Initializable {
    @FXML private MFXButton openAddCustomerBtn;
    @FXML private MFXButton bookingServiceBtn;
    @FXML private MFXTextField bookingNoteTextField;
    @FXML private Label priceLabel;
    @FXML private Button calendarButton;
    @Setter
    @FXML private MFXTextField checkoutDateTextfield;
    @FXML private Label checkInDateLabel;
    @FXML private Label roomTypeLabel;
    @FXML private Label roomNumberLabel;
    @FXML private VBox BookingVBox;
    @FXML private MFXButton addCustomerBtn;
    @FXML private ImageView closeIcon;

    private BookingViewModel viewModel;
    private ObservableList<ServiceItem> serviceItems;
    private List<ServiceItem> selectedServices = new ArrayList<>();

    public void setViewModel(BookingViewModel viewModel) {
        this.viewModel = viewModel;
        bindData();
    }

    private void bindData() {
        roomNumberLabel.textProperty().bindBidirectional(viewModel.getRoom().roomNumberProperty(), new NumberStringConverter());
        roomTypeLabel.textProperty().bindBidirectional(viewModel.getRoom().roomTypeNameProperty());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter(formatter, null);
        ObjectProperty<LocalDateTime> dateProperty = new SimpleObjectProperty<>(LocalDateTime.now());
        checkInDateLabel.textProperty().bindBidirectional(dateProperty, converter);
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMaximumFractionDigits(0);
        priceLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            BigDecimal price = viewModel.getRoom().roomTypePriceProperty().get();
                            return price != null
                                    ? currencyFormat.format(price) + " VNĐ/đêm"
                                    : "";
                        },
                        viewModel.getRoom().roomTypePriceProperty()
                )
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceItems = FXCollections.observableArrayList(
                new ServiceItem(new Service("Massage toàn thân", 500000)),
                new ServiceItem(new Service("Chăm sóc da mặt", 300000)),
                new ServiceItem(new Service("Cắt tóc nam", 150000)),
                new ServiceItem(new Service("Nhuộm tóc", 400000)),
                new ServiceItem(new Service("Làm nail", 200000)),
                new ServiceItem(new Service("Tắm trắng", 800000)),
                new ServiceItem(new Service("Giặt ủi quần áo", 100000)),
                new ServiceItem(new Service("Bữa sáng tại phòng", 250000)),
                new ServiceItem(new Service("Thuê xe đạp", 120000)),
                new ServiceItem(new Service("Thuê xe máy", 180000)),
                new ServiceItem(new Service("Đưa đón sân bay", 600000)),
                new ServiceItem(new Service("Xông hơi/sauna", 350000)),
                new ServiceItem(new Service("Dọn phòng theo yêu cầu", 50000)),
                new ServiceItem(new Service("Gọi đồ ăn nhẹ", 150000)),
                new ServiceItem(new Service("Trà chiều", 180000)),
                new ServiceItem(new Service("Chụp hình lưu niệm", 400000)),
                new ServiceItem(new Service("Tổ chức tiệc sinh nhật", 2000000)),
                new ServiceItem(new Service("Tour du lịch nội thành", 900000)),
                new ServiceItem(new Service("Trông trẻ", 300000)),
                new ServiceItem(new Service("Huấn luyện viên cá nhân tại phòng gym", 500000))
        );

        Platform.runLater(() -> BookingVBox.requestFocus());
    }

    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    public void showServicePopup(MouseEvent event) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        ListView<ServiceItem> listView = new ListView<>(serviceItems);
        listView.setPrefSize(350, 250);
        listView.setStyle("-fx-background-color: white; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        listView.setCellFactory(new Callback<ListView<ServiceItem>, ListCell<ServiceItem>>() {
            @Override
            public ListCell<ServiceItem> call(ListView<ServiceItem> param) {
                return new ServiceListCell();
            }
        });

        MFXButton okBtn = new MFXButton("OK");
        okBtn.getStyleClass().add("OK-button");
        okBtn.setOnAction(e -> popup.hide());
        popup.setOnHidden(e -> {
            selectedServices.clear();
            for (ServiceItem item : serviceItems) {
                if (item.isSelected.get() && item.getQuantity() > 0) {
                    selectedServices.add(item);
                }
            }
            System.out.println("Dịch vụ đã chọn:");
            selectedServices.forEach(item -> System.out.println(
                    item.service.getName() + " - Số lượng: " + item.getQuantity()
            ));
        });

        HBox buttons = new HBox(okBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(10, listView, buttons);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: white; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;-fx-border-width: 1;-fx-border-color: black");

        popup.getContent().add(layout);
        listView.setFocusTraversable(false);
        okBtn.setFocusTraversable(false);
        layout.setFocusTraversable(false);
        Node source = (Node) event.getSource();
        Bounds bounds = source.localToScreen(source.getBoundsInLocal());
        popup.show(source, bounds.getMinX(), bounds.getMaxY() + 5);
    }

    public static class Service {
        private final String name;
        private final int price;

        public Service(String name, int price) {
            this.name = name;
            this.price = price;
        }
        public String getName() { return name; }
        public int getPrice() { return price; }
    }

    public static class ServiceItem {
        private final Service service;
        private final BooleanProperty isSelected = new SimpleBooleanProperty(false);
        private final IntegerProperty quantity = new SimpleIntegerProperty(0);

        public ServiceItem(Service service) {
            this.service = service;
        }

        public Service getService() { return service; }
        public boolean isSelected() { return isSelected.get(); }
        public void setSelected(boolean selected) { isSelected.set(selected); }
        public BooleanProperty selectedProperty() { return isSelected; }

        public int getQuantity() { return quantity.get(); }
        public void setQuantity(int qty) { quantity.set(qty); }
        public IntegerProperty quantityProperty() { return quantity; }
    }

    private class ServiceListCell extends ListCell<ServiceItem> {
        private HBox root;
        private MFXCheckbox checkBox;
        private Label nameLabel;
        private Label priceLabel;
        private MFXTextField quantityField;

        private ServiceItem currentItem;

        public ServiceListCell() {
            super();
            checkBox = new MFXCheckbox();
            checkBox.setStyle("-mfx-main: #2356eb");
            Platform.runLater(() -> checkBox.autosize());

            nameLabel = new Label();
            nameLabel.setPrefWidth(200);
            nameLabel.setMinWidth(200);
            nameLabel.setMaxWidth(200);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            priceLabel = new Label();
            priceLabel.setStyle("-fx-font-size: 12px;");

            VBox infoBox = new VBox(2, nameLabel, priceLabel);

            quantityField = new MFXTextField("0");
            quantityField.setPrefWidth(40);
            quantityField.setMinWidth(40);
            quantityField.setMaxWidth(40);
            quantityField.getStyleClass().add("quantity-textfield");
            quantityField.setDisable(true);

            root = new HBox(10, checkBox, infoBox, new Region(), quantityField);
            HBox.setHgrow(root.getChildren().get(2), Priority.ALWAYS);
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(5));

            quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (currentItem != null) {
                    if (newVal.matches("\\d*")) {
                        int val = newVal.isEmpty() ? 0 : Integer.parseInt(newVal);
                        if (val < 0) val = 0;
                        currentItem.setQuantity(val);
                    } else {
                        quantityField.setText(oldVal);
                    }
                }
            });

            checkBox.selectedProperty().addListener((obs, oldSel, newSel) -> {
                if (currentItem != null) {
                    currentItem.setSelected(newSel);
                    quantityField.setDisable(!newSel);
                    if (!newSel) {
                        currentItem.setQuantity(0);
                        quantityField.setText("0");
                    }
                }
            });
        }

        @Override
        protected void updateItem(ServiceItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                currentItem = null;
                setGraphic(null);
            } else {
                currentItem = item;
                nameLabel.setText(item.getService().getName());
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                priceLabel.setText(nf.format(item.getService().getPrice()) + " VNĐ");
                checkBox.setSelected(item.isSelected());
                quantityField.setText(String.valueOf(item.getQuantity()));
                quantityField.setDisable(!item.isSelected());

                setGraphic(root);
            }
        }
    }

    public void handleAddCustomer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/AddCustomerView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
            AddCustomerViewModel vm = new AddCustomerViewModel();
            vm.setParent(viewModel);
            AddCustomerController controller = loader.getController();
            controller.setViewModel(vm);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openCalendar(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingCalendarView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
            BookingCalendarViewModel vm = new BookingCalendarViewModel();
            vm.setParent(viewModel);
            BookingCalendarController controller = loader.getController();
            controller.setViewModel(vm);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            LocalDateStringConverter dateConverter = new LocalDateStringConverter(DateTimeFormatter.ofPattern("dd/MM/yyyy"), null);
            Bindings.bindBidirectional(checkoutDateTextfield.textProperty(), viewModel.getCheckOutDate(), dateConverter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
