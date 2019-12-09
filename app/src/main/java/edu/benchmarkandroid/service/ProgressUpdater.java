package edu.benchmarkandroid.service;

import android.content.ContextWrapper;
import android.content.Intent;

import edu.benchmarkandroid.utils.Logger;

public abstract class ProgressUpdater {
    private ContextWrapper contextWrapper;
    private String updateAction;
    private String endAction;
    private String variant;
    private Logger logger;
    private String fname;

    ProgressUpdater(ContextWrapper contextWrapper, String updateAction, String endAction, String variant, Logger logger) {
        this.contextWrapper = contextWrapper;
        this.updateAction = updateAction;
        this.endAction = endAction;
        this.variant = variant;
        this.logger = logger;
        this.fname = logger.getFileName();
    }

    public void update(String progress) {
        Intent intent = new Intent();
        intent.setAction(updateAction);
        String message = specificUpdateMessage(progress);
        intent.putExtra("progress", message);
        contextWrapper.sendBroadcast(intent);
        logger.write(progress);
    }

    public void end(String payload) {
        Intent intent = new Intent();
//        intent.putExtra("payload", payload);
        intent.putExtra("variant", variant);
        intent.putExtra("file", fname);
        intent.setAction(endAction);

        try {
            logger.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        contextWrapper.sendBroadcast(intent);
    }

    abstract String specificUpdateMessage(String value);
}
