package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.RevenueReport;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RevenueReportDAO {

    // Lấy tất cả báo cáo doanh thu chưa bị xóa
    public List<RevenueReport> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM RevenueReport WHERE isDeleted = false", RevenueReport.class).list();
        }
    }

    // Tìm báo cáo doanh thu theo ID
    public RevenueReport findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(RevenueReport.class, id);
        }
    }

    // Thêm báo cáo doanh thu mới
    public boolean save(RevenueReport report) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(report);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật báo cáo doanh thu
    public boolean update(RevenueReport report) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(report);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm (soft delete) báo cáo doanh thu
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            RevenueReport report = session.get(RevenueReport.class, id);
            if (report != null) {
                report.setIsDeleted(true);
                session.update(report);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
