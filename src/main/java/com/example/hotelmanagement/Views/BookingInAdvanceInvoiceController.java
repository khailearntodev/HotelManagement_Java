package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.ViewModels.BookingInAdvanceInvoiceViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class BookingInAdvanceInvoiceController implements Initializable {
    @FXML private ImageView QRCodeImage;
    @FXML private Label codeLabel;
    @FXML private TextArea customerInfoTextArea;
    @FXML private Label totalLabel;
    @FXML private Label checkinDateLabel;
    @FXML private Label checkoutDateLabel;
    @FXML private Label priceLabel;
    @FXML private Label invoiceDateLabel;
    @FXML private Label roomTypeName;
    @FXML private Label roomNumberLabel;
    @FXML private VBox bookingInAdvanceInvoiceVBox;

    BookingInAdvanceInvoiceViewModel viewModel;

    public void setViewModel(BookingInAdvanceInvoiceViewModel viewModel) {
        this.viewModel = viewModel;
        codeLabel.textProperty().bind(viewModel.getPreBookingInvoiceDisplay().codeProperty());

        Bindings.bindBidirectional(
                roomNumberLabel.textProperty(),
                viewModel.getPreBookingInvoiceDisplay().roomNumberProperty(),
                new NumberStringConverter()
        );
        roomTypeName.textProperty().bindBidirectional(viewModel.getPreBookingInvoiceDisplay().roomTypeNameProperty());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        invoiceDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatter.format(viewModel.getPreBookingInvoiceDisplay().getInvoiceDate()),
                        viewModel.getPreBookingInvoiceDisplay().invoiceDateProperty()
                )
        );

        NumberFormat numFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        priceLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> numFormatter.format(viewModel.getPreBookingInvoiceDisplay().getPrice()) + " VNĐ/đêm",
                        viewModel.getPreBookingInvoiceDisplay().priceProperty()
                )
        );

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        checkinDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatter2.format(viewModel.getPreBookingInvoiceDisplay().getStartDate()),
                        viewModel.getPreBookingInvoiceDisplay().startDateProperty()
                )
        );

        checkoutDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatter2.format(viewModel.getPreBookingInvoiceDisplay().getEndDate()),
                        viewModel.getPreBookingInvoiceDisplay().endDateProperty()
                )
        );

        totalLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> numFormatter.format(viewModel.getPreBookingInvoiceDisplay().getTotalPrice()) + " VNĐ",
                        viewModel.getPreBookingInvoiceDisplay().totalPriceProperty()
                )
        );

        customerInfoTextArea.textProperty().bind(
                Bindings.concat(viewModel.getPreBookingInvoiceDisplay().getName(), "\n", viewModel.getPreBookingInvoiceDisplay().getAddress(), "\nSĐT: ", viewModel.getPreBookingInvoiceDisplay().getPhoneNumber())
        );

        try {
            String qrData = createVietQR(
                    "970422",
                    "56500824100585",
                    "TRUONG DUC HUY",
                    Long.parseLong(viewModel.getPreBookingInvoiceDisplay().getTotalPrice().toString()),
                    "Thanh toan dat truoc phong" + viewModel.getPreBookingInvoiceDisplay().getRoomNumber()
            );
            BufferedImage qrImage = generateQRCodeImage(qrData, 200, 200);
            Image fxImage = SwingFXUtils.toFXImage(qrImage, null);
            QRCodeImage.setImage(fxImage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) bookingInAdvanceInvoiceVBox.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleConfirmInvoice(MouseEvent mouseEvent) {
        viewModel.saveInvoice();
        Stage stage = (Stage) bookingInAdvanceInvoiceVBox.getScene().getWindow();
        stage.close();
    }

    private String buildTLV(String tag, String value) {
        return tag + String.format("%02d", value.length()) + value;
    }

    private String createVietQR(String bankBin, String accountNumber, String name, long amount, String addInfo) {
        StringBuilder qrData = new StringBuilder();

        qrData.append(buildTLV("00", "01"));
        qrData.append(buildTLV("01", "12"));

        // Merchant Account Information - VietQR
        String guid = buildTLV("00", "A000000727");
        String service = buildTLV("01", "1");
        String binTag = buildTLV("02", bankBin);
        String accTag = buildTLV("03", accountNumber);
        String info38 = guid + service + binTag + accTag;
        qrData.append(buildTLV("38", info38));

        qrData.append(buildTLV("52", "QRIBFTTA"));
        qrData.append(buildTLV("53", "704"));
        qrData.append(buildTLV("54", String.valueOf(amount)));
        qrData.append(buildTLV("58", "VN"));
        qrData.append(buildTLV("59", name.toUpperCase()));
        qrData.append(buildTLV("60", "HANOI"));

        String purpose = buildTLV("05", addInfo.replace(" ", ""));
        qrData.append(buildTLV("62", purpose));

        String dataWithoutCRC = qrData.toString() + "6304";
        String crc = calculateCRC16(dataWithoutCRC);
        return dataWithoutCRC + crc;
    }

    private String calculateCRC16(String str) {
        byte[] bytes = str.getBytes();
        int crc = 0xFFFF;
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0)
                    crc = (crc << 1) ^ 0x1021;
                else
                    crc <<= 1;
            }
        }
        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }

    private BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
        return image;
    }
}
