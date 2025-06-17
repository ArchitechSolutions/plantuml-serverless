package tech.architechsolutions.plantuml.lambda.logging;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

/**
 * Represents a logger that provides methods to log messages with different log levels and traceability IDs.
 */
public class Logger {
    private final LambdaLogger lambdaLogger; 
    private final int appId;
    private final long rootRequestNumber;
    private long callingRequestNumber;
    private static final Gson gson = new Gson();
    private final String appName;

    public Logger(int appId, String appName, long rootRequestNumber, long callingRequestNumber, LambdaLogger lambdaLogger) {
        this.appId = appId;
        this.rootRequestNumber = rootRequestNumber;
        this.callingRequestNumber = callingRequestNumber;
        this.lambdaLogger = lambdaLogger;
        this.appName = appName;
        log(LogLevel.Info, "Logger initialized", RemoteServicesEventTypes.RemoteServicesExecutionSucceeded.getValue());
    }
    public void logInfo(String message) {
        log(LogLevel.Info, message, RemoteServicesEventTypes.RemoteServicesExecutionInProgress.getValue());
    }
    public void logSucceeded() {
        log(LogLevel.Info, appName + " execution succeeded", RemoteServicesEventTypes.RemoteServicesExecutionSucceeded.getValue());
    }
    public void logWarn(String message) {
        log(LogLevel.Warning, message, RemoteServicesEventTypes.RemoteServicesExecutionInProgress.getValue());
    }
    public void logError(String message) {
        log(LogLevel.Error, message, RemoteServicesEventTypes.RemoteServicesExecutionFailed.getValue());
    }
    public void logCritical(String message) {
        log(LogLevel.Critical, message, RemoteServicesEventTypes.RemoteServicesExecutionFailed.getValue());
    }

    public void log(LogLevel logLevel, String message, long eventTypeNumber) {
        LogRecord record = new LogRecord();
        record.setAppId(this.appId);
        record.setEventTypeNumber(eventTypeNumber);
        record.setRootRequestNumber(this.rootRequestNumber);
        record.setCallingRequestNumber(this.callingRequestNumber);
        record.setLogLevel(logLevel);
        record.setMessage(message);        
        log(record);
    }
    
   
    public void log(LogRecord logRecord) {
        String jsonLog = gson.toJson(logRecord);
        this.lambdaLogger.log(jsonLog);
    }
}
