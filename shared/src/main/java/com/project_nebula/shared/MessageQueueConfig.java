package com.project_nebula.shared;

public final class MessageQueueConfig {
    public static final String GROUP_ID = "project-nebula";

    public static final String TOPIC_CREATE_VM_REQUEST = "vm-create-requests";
    public static final String TOPIC_CREATE_VM_RESPONSE = "vm-create-responses";

    public static final String TOPIC_START_VM_REQUEST = "vm-start-requests";
    public static final String TOPIC_START_VM_RESPONSE = "vm-start-responses";

    public static final String TOPIC_RESTART_VM_REQUEST = "vm-restart-requests";
    public static final String TOPIC_RESTART_VM_RESPONSE = "vm-restart-responses";

    public static final String TOPIC_DELETE_VM_REQUEST = "vm-delete-requests";
    public static final String TOPIC_DELETE_VM_RESPONSE = "vm-delete-responses";

    public static final String TOPIC_STOP_VM_REQUEST = "vm-stop-requests";
    public static final String TOPIC_STOP_VM_RESPONSE = "vm-stop-responses";

    public static final String TOPIC_VM_CONFIG_SAVE_REQUEST = "vm-config-save-requests";
    public static final String TOPIC_VM_CONFIG_SAVE_RESPONSE = "vm-config-save-responses";

    public static final String SPRING_JSON_VALUE_DEFAULT_TYPE_STRING = "spring.json.value.default.type";

    public static final String VM_CONFIG_SAVE_RESPONSE_TYPE = "com.project_nebula.shared.resource.VirtualMachineConfigurationResponse";
    public static final String VM_OPERATION_REQUEST_TYPE = "com.project_nebula.shared.resource.VirtualMachineRequest";

    public static final String VM_CONFIG_SAVE_RESPONSE_TYPE_MAPPING = SPRING_JSON_VALUE_DEFAULT_TYPE_STRING + "=" + VM_CONFIG_SAVE_RESPONSE_TYPE;
    public static final String VM_OPERATION_REQUEST_TYPE_MAPPING = SPRING_JSON_VALUE_DEFAULT_TYPE_STRING + "=" + VM_OPERATION_REQUEST_TYPE;
}
