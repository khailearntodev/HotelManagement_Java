package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.PrebookingDAO;
import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DAO.ServiceBookingDAO;
import com.example.hotelmanagement.DTO.Dashboard_ActivityItem;
import com.example.hotelmanagement.DTO.Dashboard_BookingDisplay;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.scene.Node;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.embed.swing.SwingFXUtils;
import lombok.Getter;

public class DashboardViewModel {
    String name = LoginViewModel.employeeName;
    int id = LoginViewModel.employeeId;

    // Database session
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final PrebookingDAO prebookingDAO = new PrebookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final ServiceBookingDAO serviceUsageDAO = new ServiceBookingDAO();

    // Summary properties
    private final IntegerProperty totalNewBookingToday = new SimpleIntegerProperty();
    private final IntegerProperty totalCheckInToday = new SimpleIntegerProperty();
    private final IntegerProperty totalCheckOutToday = new SimpleIntegerProperty();
    private final IntegerProperty totalRoomInUse = new SimpleIntegerProperty();
    private final StringProperty greetingMessage = new SimpleStringProperty();

    // Search & filter
    private final ObservableList<Dashboard_BookingDisplay> bookingList = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<Dashboard_BookingDisplay> filteredBookingList = FXCollections.observableArrayList();

    private final StringProperty searchKeyword = new SimpleStringProperty("");
    private final StringProperty selectedStatus = new SimpleStringProperty("Tất cả");

    // Chart
    @Getter
    private final XYChart.Series<String, Number> chartSeries = new XYChart.Series<>();
    @Getter
    private final ObservableList<String> roomTypes =
            FXCollections.observableArrayList("Tất cả", "Còn trống", "Đang được thuê", "Được đặt trước");


    // Recent activities
    @Getter
    private final ObservableList<Dashboard_ActivityItem> recentActivities = FXCollections.observableArrayList();

    //CLOCK
    private final StringProperty currentTime = new SimpleStringProperty();

    public DashboardViewModel()
    {
        List<Dashboard_ActivityItem> activities = serviceUsageDAO.getRecentActivities();
        searchKeyword.addListener((obs, old, newVal) -> filterBookings());
        selectedStatus.addListener((obs, old, newVal) -> filterBookings());
        loadDashboardData();
    }

    public void loadDashboardData() {
        LocalDate today = LocalDate.now();
        startClock();
        totalNewBookingToday.set(prebookingDAO.countByDate(today));
        totalCheckInToday.set(reservationDAO.countCheckInByDate(today));
        totalCheckOutToday.set(reservationDAO.countCheckOutByDate(today));
        totalRoomInUse.set(roomDAO.countInUseRooms());

        // Greeting
        greetingMessage.set(name);

        // Chart
        int totalRooms = (int) roomDAO.countAll();
        int roomsInUse = totalRoomInUse.get();
        int roomsAvailable = totalRooms - roomsInUse;

        chartSeries.getData().clear();
        chartSeries.getData().add(new XYChart.Data<>("Đang thuê", roomsInUse));
        chartSeries.getData().add(new XYChart.Data<>("Phòng trống", roomsAvailable));

        // Recent activities
        recentActivities.setAll(serviceUsageDAO.getRecentActivities());

        // Booking display
        List<Dashboard_BookingDisplay> allBookings = new ArrayList<>();
        try {
            List<Dashboard_BookingDisplay> reservationList = reservationDAO.getRecentBookingDisplays();
            List<Dashboard_BookingDisplay> prebookingList = prebookingDAO.getRecentBookingDisplays();

            allBookings.addAll(reservationList);
            allBookings.addAll(prebookingList);

            allBookings.sort(Comparator.comparing(Dashboard_BookingDisplay::getCheckInDate).reversed());
            bookingList.setAll(allBookings);
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy dữ liệu từ DAO:");
            e.printStackTrace();
        }

        filterBookings();
    }

    public void filterBookings() {
        String keyword = searchKeyword.get().toLowerCase();
        String status = selectedStatus.get();

        Predicate<Dashboard_BookingDisplay> filter = booking -> {
            boolean matchKeyword = keyword.isEmpty()
                    || booking.getCustomerName().toLowerCase().contains(keyword)
                    || String.valueOf(booking.getRoomNumber()).contains(keyword)
                    || booking.getBookingId().toLowerCase().contains(keyword)
                    || booking.getRoomType().toLowerCase().contains(keyword);

            boolean matchStatus = "Tất cả".equals(status)
                    || status.equalsIgnoreCase(booking.getStatusText());

            return matchKeyword && matchStatus;
        };

        filteredBookingList.setAll(bookingList.stream().filter(filter).collect(Collectors.toList()));
    }

    public void exportSnapshot(Node node) {
        WritableImage image = node.snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("dashboard_snapshot.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startClock() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    currentTime.set(now.format(formatter));
                });
            }
        }, 0, 1000);
    }

    // === Getters for properties ===

    public IntegerProperty totalNewBookingTodayProperty() { return totalNewBookingToday; }
    public IntegerProperty totalCheckInTodayProperty() { return totalCheckInToday; }
    public IntegerProperty totalCheckOutTodayProperty() { return totalCheckOutToday; }
    public IntegerProperty totalRoomInUseProperty() { return totalRoomInUse; }
    public StringProperty greetingMessageProperty() { return greetingMessage; }
    public StringProperty currentTimeProperty() { return currentTime; }
    public StringProperty searchKeywordProperty() { return searchKeyword; }
    public StringProperty selectedStatusProperty() { return selectedStatus; }
}
