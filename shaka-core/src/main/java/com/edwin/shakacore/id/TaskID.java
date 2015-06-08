package com.edwin.shakacore.id;

import com.google.common.base.Strings;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class TaskID extends ID {

    public TaskID(int jobId, int inc) {
        super(inc);
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(jobId).append(SEPARATOR).append(hostIP).append(SEPARATOR).append(format.format(inc));
        return sb.toString();
    }

    public TaskID toTaskID(String taskId) {

        if (Strings.isNullOrEmpty(taskId)) {
            return null;
        }

        try {

            String[] parts = taskId.split(SEPARATOR);

            return new TaskID(Integer.parseInt(parts[0]), Integer.parseInt(parts[2]));
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of taskId is not correct. ", e);
        }
    }
}
