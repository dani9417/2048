package hu.android.dani.game2048.model;


import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.orm.SugarRecord;

import hu.android.dani.game2048.R;

public class Tile extends SugarRecord {
    private int value;


    public Tile(int value) {
        this.value = value;
    }

    public Tile() {
        this(0);
    }

    public int getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getColor() {
        switch (value) {
            case 2:
                return Color.parseColor("#31697a");
            case 4:
                return Color.parseColor("#2eabd0");
            case 8:
                return Color.parseColor("#e7ca6e");
            case 16:
                return Color.parseColor("#c92b63");
            case 32:
                return Color.parseColor("#cf5353");
            case 64:
                return Color.parseColor("#85014a");
            case 128:
                return Color.parseColor("#6db22c");
            case 256:
                return Color.parseColor("#ea9f11");
            case 512:
                return Color.parseColor("#3cd6ac");
            case 1024:
                return Color.parseColor("#18896a");
            case 2048:
                return Color.parseColor("#876836");
            case 4096:
                return Color.parseColor("#3f7822");
            case 8192:
                return Color.parseColor("#e3ac57");
            case 16384:
                return Color.parseColor("#785464");
            case 32768:
                return Color.parseColor("#0ee19c");
            case 65536:
                return Color.parseColor("#ff93e5");
            default:
                return Color.parseColor("#CDC0B4");

        }

    }

    public int getValueLength() {
        int l = 0;
        int val = value;
        while(val > 0) {
            val /= 10;
            l++;
        }
        return l;

    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
