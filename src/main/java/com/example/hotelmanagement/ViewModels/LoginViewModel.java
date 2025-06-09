package com.example.hotelmanagement.ViewModels;
import com.example.hotelmanagement.DAO.UserAccountDAO;
import com.example.hotelmanagement.Models.Useraccount;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import lombok.Getter;

import java.util.List;
import java.util.regex.Pattern;

public class LoginViewModel
{
    @Getter
    private Useraccount loggedInUser;
    public static int employeeId;
    public static String employeeName;

    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final BooleanProperty loginDisabled = new SimpleBooleanProperty(true);
    private final StringProperty loginMessage = new SimpleStringProperty();

    private final UserAccountDAO userAccountDAO = new UserAccountDAO();

    // Regex ví dụ: username 3-20 ký tự, password ít nhất 6 ký tự
    private final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    //private final Pattern passwordPattern = Pattern.compile("^.{6,}$");
    private final Pattern passwordPattern = Pattern.compile("^.+$");

    public LoginViewModel() {
        loginDisabled.bind(
                Bindings.createBooleanBinding(() ->
                                username.get().trim().isEmpty() || password.get().trim().isEmpty(),
                        username, password)
        );
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public BooleanProperty loginDisabledProperty() {
        return loginDisabled;
    }

    public StringProperty loginMessageProperty() {
        return loginMessage;
    }

    public void login() {
        loginMessage.set("");

        String inputUser = username.get().trim();
        String inputPass = password.get();

        // Validate với regex
        if (!usernamePattern.matcher(inputUser).matches()) {
            loginMessage.set("Định dạng tài khoản chưa đúng. \nLưu ý: Username chỉ bao gồm các kí tự chữ và số, \nkhông có kí tự đặc biệt!");
            return;
        }

        if (!passwordPattern.matcher(inputPass).matches()) {
            loginMessage.set("Mật khẩu phải có ít nhất 6 kí tự.");
            return;
        }

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    List<Useraccount> users = userAccountDAO.getAll();
                    for (Useraccount user : users) {
                        if (user.getUsername().equals(inputUser)) {
                            if (user.getPassword().equals(inputPass)) {
                                return true;
                            }
                            return false;
                        }
                    }
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };

        loginTask.setOnSucceeded(evt -> {
            if (loginTask.getValue()) {
                loginMessage.set("Đăng nhập thành công!");

                Useraccount matchedUser = userAccountDAO.findByUsername(username.get().trim());
                if (matchedUser != null) {
                    loggedInUser = matchedUser;

                    if (matchedUser.getEmployeeID() != null) {
                        employeeId = matchedUser.getEmployeeID().getId();
                        employeeName = matchedUser.getEmployeeID().getFullName();
                    } else {
                        employeeId = -1;
                        employeeName = "Không xác định";
                    }
                } else {
                    loginMessage.set("Không tìm thấy thông tin tài khoản.");
                }
            } else {
                loginMessage.set("Tài khoản hoặc mật khẩu không chính xác.");
            }
        });


        loginTask.setOnFailed(evt -> {
            loginMessage.set("Hệ thống đang bận. Vui lòng thử lại sau.");
        });

        new Thread(loginTask).start();
    }
}
