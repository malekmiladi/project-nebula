package com.project_nebula.shared;

public final class MessageQueueConfig {
    public static final String GROUP_ID = "project-nebula-virtual-machine";

    public static final String TOPIC_CREATE_VM_RESPONSE = "create-virtual-machine-responses";
    public static final String TOPIC_CREATE_VM_REQUEST = "create-virtual-machine-requests";

    public static final String TOPIC_START_VM_RESPONSE = "start-virtual-machine-responses";
    public static final String TOPIC_START_VM_REQUEST = "start-virtual-machine-requests";

    public static final String TOPIC_RESTART_VM_RESPONSE = "restart-virtual-machine-responses";
    public static final String TOPIC_RESTART_VM_REQUEST = "restart-virtual-machine-requests";

    public static final String TOPIC_DELETE_VM_RESPONSE = "delete-virtual-machine-responses";
    public static final String TOPIC_DELETE_VM_REQUEST = "delete-virtual-machine-requests";

    public static final String TOPIC_STOP_VM_RESPONSE = "stop-virtual-machine-responses";
    public static final String TOPIC_STOP_VM_REQUEST = "stop-virtual-machine-requests";
    public static final String TOPIC_VM_CONFIG_SAVE_RESPONSE = "save-virtual-machine-config-responses";

    public static final String SPRING_JSON_VALUE_DEFAULT_TYPE_STRING = "spring.json.value.default.type=";

    public static final String VM_CONFIG_SAVE_RESPONSE_TYPE = "com.project_nebula.shared.resource.VirtualMachineConfigurationResponse";

    public static final String VM_CONFIG_SAVE_RESPONSE_TYPE_MAPPING = SPRING_JSON_VALUE_DEFAULT_TYPE_STRING + "=" + VM_CONFIG_SAVE_RESPONSE_TYPE;
}
