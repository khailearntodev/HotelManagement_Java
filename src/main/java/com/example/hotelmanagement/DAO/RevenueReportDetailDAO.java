package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.RevenueReportDetail;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class RevenueReportDetailDAO {

    // Lấy tất cả bản ghi chưa bị xóa
    public List<RevenueReportDetail> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM RevenueReportDetail WHERE isDeleted = false", RevenueReportDetail.class).list();
        }
    }

    // Tìm theo ID
    public RevenueReportDetail findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(RevenueReportDetail.class, id);
        }
    }

    // Thêm mới
    public boolean save(RevenueReportDetail detail) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(detail);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật
    public boolean update(RevenueReportDetail detail) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(detail);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm (soft delete)
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            RevenueReportDetail detail = session.get(RevenueReportDetail.class, id);
            if (detail != null) {
                detail.setIsDeleted(true);
                session.update(detail);
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
    public List<RevenueReportDetail> getByReportId(int reportId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            String sql = "FROM RevenueReportDetail rd JOIN FETCH rd.roomTypeID WHERE rd.reportID.id = :reportId AND rd.isDeleted = false";
            Query<RevenueReportDetail> query = session.createQuery(sql, RevenueReportDetail.class);
            query.setParameter("reportId", reportId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
