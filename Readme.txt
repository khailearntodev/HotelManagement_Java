Cấu trúc package MVVM:
- src.main.java: chỉ chứ source code, ko chứa resources như Images hay các file fxml,
             folder Views chứa các controller tương ứng, lưu ý phân biệt Controller và ViewModels:
                        + Controller: link với fxml, xử lý các thao tác với components
                        + ViewModels: cài đặt các hàm x lý logic, binding CSDL, làm việc với CSDL
                        *Tham khảo thêm ở ChatGPT
- src.main.resources.Views: Bỏ hêết các fxml UI vào, vì quy ước của java ưu tiên để các file tĩnh ở resoures
- Folder CSS chứa các file CSS nếu dùng
- Phần hibernate.cfg.xml nhớ thay đổi CSDL, mỗi mapping sẽ là 1 Models (tạm thời giai đoạn này chưa cần quan tâm)
- Có cập nhật gì ở file pom hay hệ thong nhớ báo laại vào nhóm
- Kich thuoc:
+ UserControl: Height="660" Width="1060"