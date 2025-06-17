package tech.architechsolutions.plantuml.lambda.logging;

/**
 * Contains static constants that define the event types for remote service execution.
 */
public final class RemoteServicesEventTypes {

    // Prevents instantiation of this utility class, as it only contains constants.
    private RemoteServicesEventTypes() {}

    public static final EventType RemoteServicesExecutionStarted = new EventType("RemoteServicesExecutionStarted", "Ports.RemoteServices.Execution.Started", 43310000L);
    
    public static final EventType RemoteServicesExecutionInProgress = new EventType("RemoteServicesExecutionInProgress", "Ports.RemoteServices.Execution.InProgress", 43320000L);
    
    public static final EventType RemoteServicesExecutionSucceeded = new EventType("RemoteServicesExecutionSucceeded", "Ports.RemoteServices.Execution.Succeeded", 43330000L);
    
    public static final EventType RemoteServicesExecutionAborted = new EventType("RemoteServicesExecutionAborted", "Ports.RemoteServices.Execution.Aborted", 43340000L);
    
    public static final EventType RemoteServicesExecutionFailed = new EventType("RemoteServicesExecutionFailed", "Ports.RemoteServices.Execution.Failed", 43350000L);

}