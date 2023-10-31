package com.example.wifind;

import android.text.InputFilter;
import android.text.Spanned;

public class RangeInputFilter implements InputFilter {
    private final double min, max;

    public RangeInputFilter(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        try {
            String sourceStr = source.toString();
            String destStr = dest.toString();
            String result = destStr.substring(0, dStart) + sourceStr.substring(start, end) + destStr.substring(dEnd);
            if (result.equals("-")) return null;
            double input = Double.parseDouble(result);
            if (min <= input && input <= max) return null;
        } catch (NumberFormatException ignored) { }
        return "";
    }
}
