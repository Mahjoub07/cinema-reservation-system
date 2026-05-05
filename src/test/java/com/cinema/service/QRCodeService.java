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

        String content = String.format(
            "CINEMA BOOKING\nBooking ID: %d\nUser: %s\nMovie: %s\nSeats: %d",
            bookingId, userName, movieTitle, seats
        );

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
            content, BarcodeFormat.QR_CODE, 200, 200
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}