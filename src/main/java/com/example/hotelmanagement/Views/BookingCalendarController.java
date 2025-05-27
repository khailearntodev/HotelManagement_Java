package com.example.hotelmanagement.Views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

public class BookingCalendarController implements Initializable {
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    private YearMonth currentYearMonth;
    @Getter
    private LocalDate selectedDate;
    @Getter
    private Set<LocalDate> unavailableDates;
    private List<Button> dayButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentYearMonth = YearMonth.now();
        unavailableDates = new HashSet<>();
        dayButtons = new ArrayList<>();
        initializeSampleUnavailableDates();
        buildCalendar();
    }

    private void initializeSampleUnavailableDates() {
        LocalDate today = LocalDate.now();
        unavailableDates.add(today.plusDays(2));
        unavailableDates.add(today.plusDays(5));
        unavailableDates.add(today.plusDays(8));
        unavailableDates.add(today.plusDays(12));
    }

    private void buildCalendar() {
        calendarGrid.getChildren().clear();
        dayButtons.clear();
        updateMonthYearLabel();
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        int dayCounter = 1;
        LocalDate today = LocalDate.now();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Button dayButton = new Button();
                dayButton.getStyleClass().add("calendar-day-button");
                dayButton.setAlignment(Pos.CENTER);
                int cellIndex = row * 7 + col;

                if (cellIndex < firstDayOfWeek || dayCounter > daysInMonth) {
                    if (cellIndex < firstDayOfWeek) {
                        YearMonth prevMonth = currentYearMonth.minusMonths(1);
                        int prevMonthDays = prevMonth.lengthOfMonth();
                        int dayOfPrevMonth = prevMonthDays - (firstDayOfWeek - cellIndex - 1);
                        dayButton.setText(String.valueOf(dayOfPrevMonth));
                        dayButton.getStyleClass().add("disabled");
                        dayButton.setDisable(true);
                    } else {
                        int dayOfNextMonth = dayCounter - daysInMonth;
                        dayButton.setText(String.valueOf(dayOfNextMonth));
                        dayButton.getStyleClass().add("disabled");
                        dayButton.setDisable(true);
                        dayCounter++;
                    }
                } else {
                    LocalDate buttonDate = currentYearMonth.atDay(dayCounter);
                    dayButton.setText(String.valueOf(dayCounter));
                    dayButton.setUserData(buttonDate);

                    if (buttonDate.equals(today)) {
                        dayButton.getStyleClass().add("today");
                    }

                    if (unavailableDates.contains(buttonDate)) {
                        dayButton.getStyleClass().add("unavailable");
                    }

                    if (buttonDate.equals(selectedDate)) {
                        dayButton.getStyleClass().add("selected");
                    }

                    if (buttonDate.isBefore(today) || buttonDate.isAfter(Collections.min(unavailableDates))) {
                        dayButton.getStyleClass().add("disabled");
                        dayButton.setDisable(true);
                    } else {
                        dayButton.setOnAction(this::onDayClicked);
                    }

                    dayCounter++;
                }

                dayButtons.add(dayButton);
                calendarGrid.add(dayButton, col, row);
            }
        }
    }

    private void updateMonthYearLabel() {
        String monthName = currentYearMonth.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("vi", "VN"));
        String displayText = "Tháng " + currentYearMonth.getMonthValue() + ", " + currentYearMonth.getYear();
        monthYearLabel.setText(displayText);
    }

    @FXML
    private void onPreviousMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        buildCalendar();
    }

    @FXML
    private void onNextMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        buildCalendar();
    }

    private void onDayClicked(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        LocalDate clickedDate = (LocalDate) clickedButton.getUserData();

        if (clickedDate != null && !unavailableDates.contains(clickedDate)) {
            dayButtons.forEach(btn -> btn.getStyleClass().remove("selected"));
            selectedDate = clickedDate;
            clickedButton.getStyleClass().add("selected");

            System.out.println("Selected date: " + selectedDate);
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onConfirm(ActionEvent event) {
        if (selectedDate != null) {
            System.out.println("Confirmed booking for date: " + selectedDate);
            closeWindow();
        } else {
            System.out.println("No date selected");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
        buildCalendar();
    }

    public void setUnavailableDates(Set<LocalDate> unavailableDates) {
        this.unavailableDates = unavailableDates != null ? unavailableDates : new HashSet<>();
        buildCalendar();
    }

    public void addUnavailableDate(LocalDate date) {
        unavailableDates.add(date);
        buildCalendar();
    }

    public void removeUnavailableDate(LocalDate date) {
        unavailableDates.remove(date);
        buildCalendar();
    }
}
