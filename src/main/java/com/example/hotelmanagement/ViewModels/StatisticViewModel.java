package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.RevenueReportDAO;
import com.example.hotelmanagement.DAO.RevenueReportDetailDAO;
import com.example.hotelmanagement.DTO.RoomStatistic;
import com.example.hotelmanagement.Models.RevenueReport;
import com.example.hotelmanagement.Models.RevenueReportDetail;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class StatisticViewModel {

    private final ObservableList<String> months = FXCollections.observableArrayList();
    private final ObservableList<String> years = FXCollections.observableArrayList();
    private ObservableList<RoomStatistic> statisticData = FXCollections.observableArrayList();


    private final StringProperty selectedMonth = new SimpleStringProperty();
    private final StringProperty selectedYear = new SimpleStringProperty();

    private final StringProperty totalRevenue = new SimpleStringProperty();
    private final StringProperty totalRental = new SimpleStringProperty();
    private final StringProperty totalBookings = new SimpleStringProperty();
    private final StringProperty popularRoomType = new SimpleStringProperty();
    private final StringProperty averageRevenue = new SimpleStringProperty();
    private BigDecimal currentTotalRevenue = BigDecimal.ZERO;


    private final ObservableList<RevenueReportDetail> revenueDetails = FXCollections.observableArrayList();
    private final RevenueReportDAO revenueReportDAO = new RevenueReportDAO();
    private final RevenueReportDetailDAO revenueReportDetailDAO = new RevenueReportDetailDAO();

    public StatisticViewModel() {
        for (int i = 1; i <= 12; i++) months.add(String.valueOf(i));
        for (int y = 2020; y <= 2025; y++) years.add(String.valueOf(y));
    }

    public ObservableList<String> getMonths() { return months; }
    public ObservableList<String> getYears() { return years; }

    public StringProperty selectedMonthProperty() { return selectedMonth; }
    public StringProperty selectedYearProperty() { return selectedYear; }

    public StringProperty totalRevenueProperty() { return totalRevenue; }
    public StringProperty totalRentalProperty() { return totalRental; }
    public StringProperty totalBookingsProperty() { return totalBookings; }
    public StringProperty popularRoomTypeProperty() { return popularRoomType; }
    public StringProperty averageRevenueProperty() { return averageRevenue; }

    public ObservableList<RevenueReportDetail> getRevenueDetails() { return revenueDetails; }
    public void loadStatisticsByMonth(int month, int year) {
        clearStatistics();
        RevenueReport report = revenueReportDAO.getByMonth(month, year);
        if (report != null) {
            List<RevenueReportDetail> details = revenueReportDetailDAO.getByReportId(report.getId());
            updateStatistics(report.getTotalService(), report.getTotalRental(), details);
            revenueDetails.setAll(details);
        }
    }

    public void loadStatisticsByYear(int year) {
        clearStatistics();
        List<RevenueReport> monthlyReports = revenueReportDAO.getAll().stream()
                .filter(r -> r.getYear() == year && r.getMonth() != null)
                .collect(Collectors.toList());

        if (!monthlyReports.isEmpty()) {
            BigDecimal totalYearRevenue = monthlyReports.stream()
                    .map(RevenueReport::getTotalService)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalYearRental = monthlyReports.stream()
                    .map(RevenueReport::getTotalRental)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<RevenueReportDetail> allDetails = monthlyReports.stream()
                    .flatMap(r -> revenueReportDetailDAO.getByReportId(r.getId()).stream())
                    .collect(Collectors.toList());

            List<RevenueReportDetail> aggregatedDetails = allDetails.stream()
                    .collect(Collectors.groupingBy(d -> d.getRoomTypeID().getTypeName(),
                            Collectors.reducing((d1, d2) -> {
                                d1.setRevenue(d1.getRevenue().add(d2.getRevenue()));
                                d1.setTotalReservation(d1.getTotalReservation() + d2.getTotalReservation());
                                return d1;
                            })
                    ))
                    .values().stream().map(Optional::get).collect(Collectors.toList());

            updateStatistics(totalYearRevenue, totalYearRental, aggregatedDetails);
            revenueDetails.setAll(aggregatedDetails);

        }
    }

    private void updateStatistics(BigDecimal revenue, BigDecimal rental, List<RevenueReportDetail> details) {
        this.currentTotalRevenue = revenue;
        totalRevenue.set(String.format("%,.0f VNĐ", revenue));
        totalRental.set(String.format("%,.0f VNĐ", rental));
        revenueDetails.setAll(details);

        int totalReservations = details.stream()
                .mapToInt(RevenueReportDetail::getTotalReservation)
                .sum();
        totalBookings.set(String.valueOf(totalReservations));

        details.stream()
                .max(Comparator.comparing(RevenueReportDetail::getTotalReservation))
                .ifPresent(d -> popularRoomType.set(d.getRoomTypeID().getTypeName()));

        if (totalReservations > 0) {
            BigDecimal avgRevenue = revenue.divide(new BigDecimal(totalReservations), 2, RoundingMode.HALF_UP);
            averageRevenue.set(String.format("%,.0f VNĐ", avgRevenue));
        } else {
            averageRevenue.set("0 VNĐ");
        }
    }

    private void clearStatistics() {
        this.currentTotalRevenue = BigDecimal.ZERO;
        totalRevenue.set("0 VNĐ");
        totalRental.set("0 VNĐ");
        totalBookings.set("0");
        popularRoomType.set("N/A");
        averageRevenue.set("0 VNĐ");
        revenueDetails.clear();
    }
    public BigDecimal getCurrentTotalRevenue() {
        return currentTotalRevenue;
    }}
