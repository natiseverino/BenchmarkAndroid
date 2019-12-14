package edu.benchmarkandroid.utils;

import android.widget.TextView;

public class LogGUI {

    private static TextView logTextView = null;
    private static StringBuilder stringBuilder;
    private static final int MAX_STRING_BUILDER = 1_000_000;


    public static void init(TextView logTextView) {
        LogGUI.logTextView = logTextView;
        stringBuilder = new StringBuilder();
    }


    public static void log(String line) {
        if (logTextView != null) {

            String[] lines = line.split("\n");
            if (lines.length > 1)
                line = lines[lines.length - 1];

            stringBuilder.append(line).append("\n");
            checkStringBuilderLength();
            logTextView.setText(stringBuilder.toString());
        }

    }


    private static void checkStringBuilderLength() {
        if (stringBuilder != null)
            if (stringBuilder.length() >= MAX_STRING_BUILDER) {
                stringBuilder = stringBuilder.reverse();
                stringBuilder.setLength(MAX_STRING_BUILDER / 2);
                stringBuilder = stringBuilder.reverse();
            }
    }
}
