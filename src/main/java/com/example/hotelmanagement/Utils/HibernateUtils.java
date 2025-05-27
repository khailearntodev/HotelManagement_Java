package com.example.hotelmanagement.Utils;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

    // Lấy SessionFactory
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Tạo sessionFactory từ file cấu hình hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Mở session mới
    public static Session getSession() {
        return sessionFactory.openSession();
    }

    // Đóng SessionFactory khi ứng dụng kết thúc
    public static void shutdown() {
        getSessionFactory().close();
    }
}
