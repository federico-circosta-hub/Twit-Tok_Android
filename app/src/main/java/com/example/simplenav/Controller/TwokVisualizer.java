package com.example.simplenav.Controller;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.simplenav.Model.TwokRepository;

public class TwokVisualizer {

     public void formatTwok(TwokRepository twok, TextView text, View cell) {
            if (twok.getFontcol() != null && twok.getFontcol().matches("-?[0-9a-fA-F]+") && twok.getFontcol().length() == 6) {
                text.setTextColor(Color.parseColor("#"+twok.getFontcol()));
            }
            if (twok.getBgcol() != null && twok.getBgcol().matches("-?[0-9a-fA-F]+") && twok.getBgcol().length() == 6) {
                cell.setBackgroundColor(Color.parseColor("#"+twok.getBgcol()));
            }
            text.setTextSize(selectSize(twok.getFontsize()));
            text.setTypeface(familyResolver(twok.getFonttype()));
            text.setGravity(selectHAlign(twok.getHalign())+selectVAlign(twok.getValign()));

        }

        private float selectSize(int fontsize) {
            switch (fontsize) {
                case 2:
                    return 54;
                case 1:
                    return 26;
                default:
                    return 16;
            }
        }

    private Typeface familyResolver(int fontfamily) {
        switch (fontfamily) {
            case 2:
                return Typeface.SERIF;
            case 1:
                return Typeface.MONOSPACE;
            default:
                return Typeface.DEFAULT;
        }
    }

        private int selectHAlign(int halign) {
            switch (halign) {
                case 2:
                    return Gravity.RIGHT;
                case 1:
                    return Gravity.CENTER_HORIZONTAL;
                default:
                    return Gravity.LEFT;
            }
        }

        private int selectVAlign(int valign) {
            switch (valign) {
                case 2:
                    return Gravity.BOTTOM;
                case 1:
                    return Gravity.CENTER_VERTICAL;
                default:
                    return Gravity.TOP;
            }
        }

}
