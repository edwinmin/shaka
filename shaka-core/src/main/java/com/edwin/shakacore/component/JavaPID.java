package com.edwin.shakacore.component;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author jinming.wu
 * @date 2015-5-26
 */
public class JavaPID {

    public static int getPID() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        int index = name.indexOf("@");
        if (index != -1) {
            int pid = Integer.parseInt(name.substring(0, index));
            return pid;
        }

        throw new RuntimeException("Can't find java pid...");
    }
}
