package com.cinema.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeService {

    public String generateQRCode(Long bookingId, String userName,
                                  String movieTitle, int seats)
                                  throws WriterException, IOException {
        return Base64.getEncoder().encodeToString(
            buildQRCodeBytes(bookingId)
        );
    }

    public byte[] generateQRCodeBytes(Long bookingId, String userName,
                                       String movieTitle, int seats)
                                       throws WriterException, IOException {
        return buildQRCodeBytes(bookingId);
    }

    private byte[] buildQRCodeBytes(Long bookingId)
                                     throws WriterException, IOException {
        String content = String.format(
            "https://mahjoub07.github.io/cinema-reservation-system/#/booking-confirmation/%d",
            bookingId
        );

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
            content, BarcodeFormat.QR_CODE, 200, 200
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }
}