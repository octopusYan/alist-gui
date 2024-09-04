package cn.octopusyan.alistgui.util;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 命令工具类
 *
 * @author octopus_yan@foxmail.com
 */
public class ProcessesUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProcessesUtil.class);
    private static final String NEW_LINE = System.lineSeparator();
    private static final DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();

    public static boolean exec(String command) {
        try {
            exec(command, msg -> {});
            handler.waitFor();
        } catch (Exception e) {
            logger.error("", e);
        }
        return 0 == handler.getExitValue();
    }

    public static void exec(String command, OnExecuteListener listener) {
        LogOutputStream logout = new LogOutputStream() {
            @Override
            protected void processLine(String line, int logLevel) {
                if (listener != null) listener.onExecute(line + NEW_LINE);
            }
        };

        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor executor = DefaultExecutor.builder().get();
        executor.setStreamHandler(new PumpStreamHandler(logout, logout));
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {
                if (listener != null) {
                    listener.onExecuteSuccess(exitValue);
                }
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                if (listener != null) {
                    listener.onExecuteError(e);
                }
            }
        };
        try {
            executor.execute(commandLine, handler);
        } catch (IOException e) {
            if (listener != null) listener.onExecuteError(e);
        }
    }

    public interface OnExecuteListener {
        void onExecute(String msg);
        default void onExecuteSuccess(int exitValue){}
        default void onExecuteError(Exception e){}
    }

    /**
     * Prevent construction.
     */
    private ProcessesUtil() {
    }
}
