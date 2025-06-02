// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Utils(){}

    public static List<String> getNumberRange(int n) {
        if (n <= 0) {
            return new ArrayList<>();
        }

        List<String> range = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            range.add(String.valueOf(i));
        }
        return range;
    }
}
