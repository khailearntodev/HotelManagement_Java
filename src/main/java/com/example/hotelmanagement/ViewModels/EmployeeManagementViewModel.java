package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.UserAccountDAO;
import com.example.hotelmanagement.DTO.EmployeeManagement_EmployeeDisplay;
import com.example.hotelmanagement.Models.Employee;
import com.example.hotelmanagement.DAO.EmployeeDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmployeeManagementViewModel {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Getter
    private final ObservableList<EmployeeManagement_EmployeeDisplay> employeeList = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<EmployeeManagement_EmployeeDisplay> filteredList = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<String> positionList = FXCollections.observableArrayList();


    // Filter properties
    private final StringProperty searchKeyword = new SimpleStringProperty("");
    private final StringProperty selectedPosition = new SimpleStringProperty("");
    private final StringProperty selectedGender = new SimpleStringProperty("");

    // Selected employee (ví dụ dùng cho xóa)
    private final ObjectProperty<EmployeeManagement_EmployeeDisplay> selectedEmployee = new SimpleObjectProperty<>();

    // Date formatter cho hiển thị ngày tháng
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());

    public EmployeeManagementViewModel() {
        loadEmployees();
        loadPositions();
        // Lắng nghe filter thay đổi
        searchKeyword.addListener((obs, oldVal, newVal) -> filterEmployees());
        selectedPosition.addListener((obs, oldVal, newVal) -> filterEmployees());
        selectedGender.addListener((obs, oldVal, newVal) -> filterEmployees());
    }

    public void setSelectedPosition(String value) {
        selectedPosition.set(value);
    }

    public void refreshEmployeeList() {
        loadEmployees();
    }

    public void loadEmployees() {
        List<Employee> employees = employeeDAO.getAll();

        employeeList.clear();
        for (Employee e : employees) {
            EmployeeManagement_EmployeeDisplay dto = new EmployeeManagement_EmployeeDisplay(
                    e.getId(),
                    e.getFullName(),
                    e.getDateOfBirth(),
                    e.getIdentityNumber(),
                    e.getPhoneNumber(),
                    e.getAddress(),
                    e.getGender(),
                    e.getStartingDate(),
                    e.getEmail(),
                    e.getContractType(),
                    e.getContractDate(),
                    e.getSalaryRate(),
                    e.getAvatar(),
                    e.getPosition()
            );
            employeeList.add(dto);
        }

        filterEmployees();
    }

    public void loadPositions() {
        List<String> positions = employeeDAO.getAllPositions();
        positionList.setAll(positions);
    }

    // Filter nhân viên theo keyword, position, gender
    public void filterEmployees() {
        String kw = searchKeyword.get() == null ? "" : searchKeyword.get().toLowerCase().trim();
        String posFilter = selectedPosition.get() == null ? "" : selectedPosition.get().toLowerCase().trim();
        String genderFilter = selectedGender.get() == null ? "" : selectedGender.get().toLowerCase().trim();

        Predicate<EmployeeManagement_EmployeeDisplay> filter = e -> {
            boolean matchKeyword = kw.isEmpty() || e.getEmployeeName().toLowerCase().contains(kw)
                    || String.valueOf(e.getEmployeeId()).contains(kw)
                    || e.getPosition().toLowerCase().contains(kw);

            boolean matchPosition = posFilter.equals("chức vụ") || posFilter.isEmpty()
                    || e.getPosition().toLowerCase().equals(posFilter);

            boolean matchGender = genderFilter.equals("giới tính") || genderFilter.isEmpty()
                    || (genderFilter.equals("nam") && e.isGender())
                    || (genderFilter.equals("nữ") && !e.isGender());

            return matchKeyword && matchPosition && matchGender;
        };

        filteredList.setAll(employeeList.stream().filter(filter).collect(Collectors.toList()));
    }

    // Xóa nhân viên
    public boolean deleteSelectedEmployee() {
        EmployeeManagement_EmployeeDisplay emp = selectedEmployee.get();
        if (emp == null) return false;

        UserAccountDAO dao = new UserAccountDAO();
        dao.softDelete(emp.getEmployeeId());

        boolean success = employeeDAO.softDelete(emp.getEmployeeId());
        if (success) {
            employeeList.remove(emp);
            filteredList.remove(emp);
        }
        return success;
    }

    public void exportToExcel(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách nhân viên");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("Danh_sach_nhan_vien_" + timestamp + ".xlsx");

        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách nhân viên");

            // Fonts
            Font titleFont = workbook.createFont();
            titleFont.setFontName("Arial");
            titleFont.setFontHeightInPoints((short) 14);
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.WHITE.getIndex());

            Font headerFont = workbook.createFont();
            headerFont.setFontName("Arial");
            headerFont.setBold(true);

            Font normalFont = workbook.createFont();
            normalFont.setFontName("Arial");

            // Styles
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(normalFont);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);

            CellStyle evenRowStyle = workbook.createCellStyle();
            evenRowStyle.cloneStyleFrom(cellStyle);
            evenRowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowIdx = 0;

            // Title
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH SÁCH NHÂN VIÊN");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            // Info row
            Row infoRow = sheet.createRow(rowIdx++);
            infoRow.createCell(0).setCellValue("Thời gian xuất: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            infoRow.createCell(5).setCellValue("Người xuất: Admin");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 9));

            rowIdx++; // Empty row

            // Header
            Row headerRow = sheet.createRow(rowIdx++);
            String[] headers = {"Mã NV", "Tên nhân viên", "Chức vụ", "Ngày sinh", "Số CMND", "Số điện thoại", "Loại hợp đồng", "Hệ số lương", "Địa chỉ", "Giới tính"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            for (int i = 0; i < filteredList.size(); i++) {
                EmployeeManagement_EmployeeDisplay e = filteredList.get(i);
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(e.getEmployeeId());
                row.createCell(col++).setCellValue(e.getEmployeeName());
                row.createCell(col++).setCellValue(e.getPosition());
                row.createCell(col++).setCellValue(e.getBirthday() == null ? "" : dateFormatter.format(e.getBirthday()));
                row.createCell(col++).setCellValue(e.getIdentityNumber());
                row.createCell(col++).setCellValue(e.getPhoneNumber());
                row.createCell(col++).setCellValue(e.getContractType());
                row.createCell(col++).setCellValue(e.getSalaryRate().toPlainString());
                row.createCell(col++).setCellValue(e.getAddress());
                row.createCell(col++).setCellValue(e.isGender() ? "Nam" : "Nữ");

                // Gán style cho từng ô trong dòng
                for (int j = 0; j < col; j++) {
                    Cell cell = row.getCell(j);
                    cell.setCellStyle(i % 2 == 0 ? evenRowStyle : cellStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi xuất file Excel: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // === Property getters ===
    public StringProperty searchKeywordProperty() { return searchKeyword; }
    public StringProperty selectedPositionProperty() { return selectedPosition; }
    public StringProperty selectedGenderProperty() { return selectedGender; }
    public ObjectProperty<EmployeeManagement_EmployeeDisplay> selectedEmployeeProperty() { return selectedEmployee; }
}
