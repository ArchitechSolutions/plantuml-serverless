package tech.architechsolutions.plantuml.lambda.logging;

public class LogRecord {
    private int appId;
    private long eventTypeNumber;
    private long rootRequestNumber;
    private long relatedEventNumber;
    private long callingRequestNumber;
    private LogLevel logLevel;
    private String message;
    private String timestamp;


    public int getAppId() { return appId; }
    public void setAppId(int appId) { this.appId = appId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public long getEventTypeNumber() { return eventTypeNumber; }
    public void setEventTypeNumber(long eventTypeNumber) { this.eventTypeNumber = eventTypeNumber; }
    public long getRootRequestNumber() { return rootRequestNumber; }
    public void setRootRequestNumber(long rootRequestNumber) { this.rootRequestNumber = rootRequestNumber; }
    public long getRelatedEventNumber() { return relatedEventNumber; }
    public void setRelatedEventNumber(long relatedEventNumber) { this.relatedEventNumber = relatedEventNumber; }
    public long getCallingRequestNumber() { return callingRequestNumber; }
    public void setCallingRequestNumber(long callingRequestNumber) { this.callingRequestNumber = callingRequestNumber; }
    public LogLevel getLogLevel() { return logLevel; }
    public void setLogLevel(LogLevel logLevel) { this.logLevel = logLevel; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; } 
}
