package edu.benchmarkandroid.utils;

import android.widget.TextView;

public class LogGUI {

    private static TextView logTextView = null;
    private static StringBuilder stringBuilder;


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
            logTextView.setText(stringBuilder.toString());
        }

    }
}
