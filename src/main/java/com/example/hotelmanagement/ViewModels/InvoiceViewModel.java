package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.Models.Invoice;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.Instant;

public class InvoiceViewModel {
    private final ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();
    private final ObservableList<Invoice> fullList = FXCollections.observableArrayList();

    private int limit = -1;
    private String currentKeyword = "";
    private int currentInvoiceType = -1;
    private String currentPaymentStatus = "Tất cả trạng thái";
    private BigDecimal minAmount = BigDecimal.ZERO;
    private BigDecimal maxAmount = new BigDecimal("100000000");

    private final InvoiceDAO dao = new InvoiceDAO();

    public InvoiceViewModel() {
        loadInvoices();
    }

    public void loadInvoices() {
        fullList.setAll(dao.getAll());
        applyAllFilters();
    }

    public void applyAllFilters() {
        ObservableList<Invoice> filtered = fullList.filtered(inv -> {
            boolean match = true;
            match &= currentInvoiceType == -1 || inv.getInvoiceType() == currentInvoiceType;
            match &= "Tất cả trạng thái".equals(currentPaymentStatus) || currentPaymentStatus.equals(inv.getPaymentStatus());
            match &= inv.getCustomerName().toLowerCase().contains(currentKeyword.toLowerCase());
            match &= inv.getTotalAmount().compareTo(minAmount) >= 0 && inv.getTotalAmount().compareTo(maxAmount) <= 0;
            return match;
        });

        if (limit > 0 && limit < filtered.size()) {
            invoiceList.setAll(filtered.subList(0, limit));
        } else {
            invoiceList.setAll(filtered);
        }
    }

    public void filterByInvoiceType(int type) {
        this.currentInvoiceType = type;
        applyAllFilters();
    }

    public void filterByPaymentStatus(String status) {
        this.currentPaymentStatus = status;
        applyAllFilters();
    }

    public void filterByAmountRange(BigDecimal min, BigDecimal max) {
        this.minAmount = min;
        this.maxAmount = max;
        applyAllFilters();
    }

    public void filterByKeyword(String keyword) {
        this.currentKeyword = keyword;
        applyAllFilters();
    }

    public void setLimit(int limit) {
        this.limit = limit;
        applyAllFilters();
    }

    public ObservableList<Invoice> getInvoiceList() {
        return invoiceList;
    }
}