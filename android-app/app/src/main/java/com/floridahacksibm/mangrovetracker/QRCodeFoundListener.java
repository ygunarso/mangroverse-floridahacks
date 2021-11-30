package com.floridahacksibm.mangrovetracker;

public interface QRCodeFoundListener {
    void onQRCodeFound(String QRCode);
    void onQRCodeNotFound();
}
