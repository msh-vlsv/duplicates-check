import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {

    private static MyLogger instance;

    private Logger logger;

    public MyLogger() {
        logger = LogManager.getLogger();
    }

    public static MyLogger getInstance() {
        if (instance == null) {
            instance = new MyLogger();
        }
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }
}
