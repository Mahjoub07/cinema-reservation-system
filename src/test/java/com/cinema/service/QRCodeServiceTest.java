package com.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}