package com.edwin.shakaclient.util;

import com.edwin.shakaclient.entity.ExecuteResult;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;

/**
 * Created by shichao.liao on 15/6/9.
 */
public class CommonExecHelper {

    public static ExecuteResult executeCommand(String command,int timeOut) throws Exception {

        ExecuteResult result = new ExecuteResult();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        CommandLine commandline = CommandLine.parse(command);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setExitValues(null);
        if(timeOut>0){
            //利用监视狗来设置超时
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeOut);
            exec.setWatchdog(watchdog);
        }
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        exec.setStreamHandler(streamHandler);
        exec.execute(commandline);
        result.setOut(outputStream.toString("gbk"));
        result.setError(errorStream.toString("gbk"));
        return result;
    }

    public static void main (String[] args) throws Exception {
        System.out.println(executeCommand("ping127.0.0.1",0).getOut());
    }
}
