package tech.architechsolutions.plantuml.lambda.logging;

/**
 * Represents an event type with a name, an identification key, and a number.
 * This class is immutable.
 */
public class EventType {

    private final String name;
    private final String key;
    private final long value;

    /**
     * Constructor to create a new event type.
     * @param name The human-readable name of the event (e.g., "RemoteServicesExecutionStarted").
     * @param key The unique string key for the event (e.g., "Ports.RemoteServices.Execution.Started").
     * @param eventNumber The number associated with the event.
     */
    public EventType(String name, String key, long value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    // Getter methods to access the values
    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EventType{" +
               "name='" + name + '\'' +
               ", key='" + key + '\'' +
               ", value=" + value +
               '}';
    }
}