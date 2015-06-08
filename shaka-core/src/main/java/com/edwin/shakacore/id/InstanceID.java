package com.edwin.shakacore.id;

import com.google.common.base.Strings;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class InstanceID extends ID {

    private String taskId;

    public InstanceID(String taskId, int inc) {
        super(inc);
        this.taskId = taskId;
    }

    public InstanceID(TaskID taskId, int inc) {
        super(inc);
        this.taskId = taskId.toString();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(taskId).append(SEPARATOR).append(format.format(inc));
        return sb.toString();
    }

    public InstanceID toInstanceID(String instanceId) {
        if (Strings.isNullOrEmpty(instanceId)) {
            return null;
        }

        try {

            String[] parts = instanceId.split(SEPARATOR);

            return new InstanceID(parts[0] + SEPARATOR + parts[1] + SEPARATOR + parts[2], Integer.parseInt(parts[3]));
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of instanceId is not correct. ", e);
        }
    }
}
