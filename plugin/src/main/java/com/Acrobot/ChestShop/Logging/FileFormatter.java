package com.Acrobot.ChestShop.Logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Acrobot
 */
public class FileFormatter extends Formatter {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuilder message = new StringBuilder(getDateAndTime());

        if (record.getLevel() != Level.INFO) {
            message.append(' ').append(record.getLevel().getLocalizedName());
        }

        message.append(' ').append(record.getMessage());

        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            message.append(sw.toString());
        }

        return message.append('\n').toString();
    }

    private String getDateAndTime() {
        Date date = new Date();

        return dateFormat.format(date);
    }
}
