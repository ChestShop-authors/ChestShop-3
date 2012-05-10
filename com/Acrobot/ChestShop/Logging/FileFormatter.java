package com.Acrobot.ChestShop.Logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Acrobot
 */
public class FileFormatter extends Formatter {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        return getDateAndTime() + ' ' + record.getMessage() + '\n';
    }

    private String getDateAndTime() {
        Date date = new Date();

        return dateFormat.format(date);
    }
}
