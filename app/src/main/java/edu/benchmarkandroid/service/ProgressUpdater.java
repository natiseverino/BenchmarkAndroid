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

    ProgressUpdater(ContextWrapper contextWrapper, String updateAction, String endAction, String variant, Logger logger) {
        this.contextWrapper = contextWrapper;
        this.updateAction = updateAction;
        this.endAction = endAction;
        this.variant = variant;
        this.logger = logger;
    }

    public void update(String msg) {
        Intent intent = new Intent();
        intent.setAction(updateAction);
        String message = specificUpdateMessage(msg);
        intent.putExtra("msg", message);
        contextWrapper.sendBroadcast(intent);
        logger.write(msg);
    }

    public void end(String payload) {
        Intent intent = new Intent();
//        intent.putExtra("payload", payload);
        intent.putExtra("variant", variant);
        intent.putExtra("file", logger.getFileName());
        intent.setAction(endAction);
        contextWrapper.sendBroadcast(intent);
        try {
            logger.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    abstract String specificUpdateMessage(String value);
}
