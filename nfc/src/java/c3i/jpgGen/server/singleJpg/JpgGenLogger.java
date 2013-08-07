package c3i.jpgGen.server.singleJpg;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JpgGenLogger {

    public static Logger createLogger() {
        return createLogger1();
    }

    public static Logger createLogger1() {
        Logger logger = Logger.getLogger("JpgGen");
        return logger;
    }


    public static Logger createLogger2() {
        Logger logger = Logger.getLogger("JpgGen");
        FileHandler handler = createHandler();
        logger.addHandler(handler);
        return logger;
    }

    private static FileHandler createHandler() {
        try {
            String catBase = System.getProperty("catalina.base");
            File catBaseDir = new File(catBase);
            File logBaseDir = new File(catBaseDir, "logs");
            File f = new File(logBaseDir, "jpg-gen.log.txt");
            FileHandler handler = new FileHandler(f.toString());
            MyFormatter formatter = new MyFormatter();
            handler.setFormatter(formatter);
            return handler;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class MyFormatter extends Formatter {
        Date dat = new Date();
        private final static String format = "{0,date} {0,time}";
        private MessageFormat formatter;

        private Object args[] = new Object[1];

        // Line separator string.  This is the value of the line.separator
        // property at the moment that the SimpleFormatter was created.
        private String lineSeparator = (String) java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("line.separator"));

        /**
         * Format the given LogRecord.
         * @param record the log record to be formatted.
         * @return a formatted log record
         */
        public synchronized String format(LogRecord record) {
            StringBuffer sb = new StringBuffer();
            // Minimize memory allocations here.
            dat.setTime(record.getMillis());
            args[0] = dat;
            StringBuffer text = new StringBuffer();
            if (formatter == null) {
                formatter = new MessageFormat(format);
            }
            formatter.format(args, text, null);
            sb.append(text);
            sb.append(" ");

            String message = record.getMessage();
            sb.append(message);
            sb.append(lineSeparator);

            return sb.toString();
        }
    }
}
