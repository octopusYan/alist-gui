package cn.octopusyan.alistgui.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 命令工具类
 *
 * @author octopus_yan@foxmail.com
 */
@Slf4j
public class ProcessesUtil {
    private static final String NEW_LINE = System.lineSeparator();
    private static final int EXIT_VALUE = 1;

    private final Executor executor = DefaultExecutor.builder().get();
    private final ShutdownHookProcessDestroyer processDestroyer;
    private OnExecuteListener listener;
    private CommandLine commandLine;

    private static final Set<ProcessesUtil> set = new HashSet<>();

    /**
     * Prevent construction.
     */
    private ProcessesUtil(String workingDirectory) {
        LogOutputStream logout = new LogOutputStream() {
            @Override
            protected void processLine(String line, int logLevel) {
                if (listener != null)
                    listener.onExecute(line + NEW_LINE);
            }
        };
        PumpStreamHandler streamHandler = new PumpStreamHandler(logout, logout);
        executor.setStreamHandler(streamHandler);
        executor.setWorkingDirectory(new File(workingDirectory));
        executor.setExitValue(EXIT_VALUE);
        processDestroyer = new ShutdownHookProcessDestroyer();
        executor.setProcessDestroyer(processDestroyer);
    }

    public static ProcessesUtil init(String workingDirectory) {
        ProcessesUtil util = new ProcessesUtil(workingDirectory);
        set.add(util);
        return util;
    }

    public boolean exec(String command) {
        commandLine = CommandLine.parse(command);
        int execute = 0;
        try {
            execute = executor.execute(commandLine);
        } catch (Exception e) {
            log.error("exec", e);
        }
        return execute == EXIT_VALUE;
    }

    public void exec(String command, OnExecuteListener listener) {
        this.listener = listener;
        commandLine = CommandLine.parse(command);
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {
                if (listener != null) {
                    listener.onExecuteSuccess(exitValue == EXIT_VALUE);
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
        } catch (Exception e) {
            if (listener != null) listener.onExecuteError(e);
        }
    }

    public void destroy() {
        if (processDestroyer.isEmpty()) return;
        processDestroyer.run();
    }

    public boolean isRunning() {
        return !processDestroyer.isEmpty();
    }

    public static void destroyAll() {
        set.forEach(ProcessesUtil::destroy);
    }

    public interface OnExecuteListener {
        void onExecute(String msg);

        default void onExecuteSuccess(boolean success) {
        }

        default void onExecuteError(Exception e) {
        }
    }
}
