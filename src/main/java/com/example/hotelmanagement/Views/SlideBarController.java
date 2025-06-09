package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.RolePermissionDAO;
import com.example.hotelmanagement.Models.Permission;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SlideBarController {

    @FXML
    private ToggleButton btnTrangChu;
    @FXML
    private ToggleButton btnPhong;
    @FXML
    private ToggleButton btnDatPhong;
    @FXML
    private ToggleButton btnTienNghi;
    @FXML
    private ToggleButton btnDichVu;
    @FXML
    private ToggleButton btnLichSuHD;
    @FXML
    private ToggleButton btnKhachHang;
    @FXML
    private ToggleButton btnNhanVien;
    @FXML
    private ToggleButton btnThongKe;
    @FXML
    private ToggleButton btnCauHinh;

    @Setter
    private MainWindowController mainWindowController;

    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    private void updateButtonVisibility(ToggleButton button, boolean isVisible) {
        button.setVisible(isVisible);
        button.setManaged(isVisible);
    }

    public void initializePermissionsForRole(int roleId) {
        List<Permission> permissions = rolePermissionDAO.getPermissionsByRoleId(roleId);
        Set<String> allowedViews = permissions.stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet());

        if (allowedViews.contains("TrangChu")) {
            btnTrangChu.setSelected(true);
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/DashboardView.fxml");
            mainWindowController.setTabName("Trang chủ");
        }

        updateButtonVisibility(btnTrangChu, allowedViews.contains("TrangChu"));
        updateButtonVisibility(btnPhong, allowedViews.contains("Phong"));
        updateButtonVisibility(btnDatPhong, allowedViews.contains("DatPhong"));
        updateButtonVisibility(btnTienNghi, allowedViews.contains("TienNghi"));
        updateButtonVisibility(btnDichVu, allowedViews.contains("DichVu"));
        updateButtonVisibility(btnLichSuHD, allowedViews.contains("LichSuHoaDon"));
        updateButtonVisibility(btnKhachHang, allowedViews.contains("KhachHang"));
        updateButtonVisibility(btnNhanVien, allowedViews.contains("NhanVien"));
        updateButtonVisibility(btnThongKe, allowedViews.contains("ThongKe"));
        updateButtonVisibility(btnCauHinh, allowedViews.contains("CauHinh"));
    }


    @FXML
    private void onToggleSelected() {
        if (btnTrangChu.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/DashboardView.fxml");
            mainWindowController.setTabName("Trang Chủ");
        } else if (btnPhong.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/ManageRoomType.fxml");
            mainWindowController.setTabName("Quản Lý Phòng");
        } else if (btnDatPhong.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/ReservationView.fxml");
            mainWindowController.setTabName("Đặt Phòng");
//        } else if (btnTienNghi.isSelected()) {
//            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/UserControls/AmenityView.fxml");
//            mainWindowController.setTabName("Tiện nghi");
        } else if (btnDichVu.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/ServiceManagementView.fxml");
            mainWindowController.setTabName("Dịch Vụ");
        } else if (btnLichSuHD.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/InvoiceView.fxml");
            mainWindowController.setTabName("Lịch Sử Hóa Đơn");
        } else if (btnKhachHang.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/CustomerManagementView.fxml");
            mainWindowController.setTabName("Khách Hàng");
        } else if (btnNhanVien.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/EmployeeManagementView.fxml");
            mainWindowController.setTabName("Nhân Viên");
        } else if (btnThongKe.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/StatisticView.fxml");
            mainWindowController.setTabName("Thống Kê");
        } else if (btnCauHinh.isSelected()) {
            mainWindowController.loadContentView("/com/example/hotelmanagement/Views/ConfigView.fxml");
            mainWindowController.setTabName("Cấu Hình");
        }
    }
}
