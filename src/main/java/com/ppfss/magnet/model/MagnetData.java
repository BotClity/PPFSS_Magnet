// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.model;

public record MagnetData(
        int radius,
        double strength,
        int limit
) {
    public MagnetData compare(MagnetData magnetData) {
        return strength > magnetData.strength ?  this : magnetData;
    }
}