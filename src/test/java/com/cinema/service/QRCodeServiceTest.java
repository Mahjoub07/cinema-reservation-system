package com.cinema.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class QRCodeServiceTest {

    private QRCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeService = new QRCodeService();
    }

    @Test
    void shouldGenerateQRCode() throws Exception {
        String qrCode = qrCodeService.generateQRCode(1L, "Mahjoub", "Inception", 2);

        assertNotNull(qrCode);
        assertFalse(qrCode.isEmpty());
    }

    @Test
    void shouldGenerateBase64EncodedQRCode() throws Exception {
        String qrCode = qrCodeService.generateQRCode(1L, "Mahjoub", "Inception", 2);

        // Base64 strings don't contain spaces
        assertFalse(qrCode.contains(" "));
        assertTrue(qrCode.length() > 100);
    }

    @Test
    void shouldGenerateDifferentQRCodesForDifferentBookings() throws Exception {
        String qrCode1 = qrCodeService.generateQRCode(1L, "Mahjoub", "Inception", 2);
        String qrCode2 = qrCodeService.generateQRCode(2L, "Yassine", "Avatar", 3);

        assertNotEquals(qrCode1, qrCode2);
    }

    @Test
    void shouldEncodeCorrectDeploymentUrl() throws Exception {
        byte[] qrBytes = qrCodeService.generateQRCodeBytes(29L, "Test", "Movie", 2);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(qrBytes));
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        String expected = "https://mahjoub07.github.io/cinema-reservation-system/#/booking-confirmation/29";

        assertEquals(expected, result.getText());
    }
}