package cn.octopusyan.alistgui.manager;

import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.util.ProcessesUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

/**
 * AList 管理
 *
 * @author octopus_yan
 */
public class AListManager {
    public static final String START_COMMAND = STR."\{Constants.ALIST_FILE} server";
    private static final ProcessesUtil util;
    private static final BooleanProperty running = new SimpleBooleanProperty(false);

    static {
        util = ProcessesUtil.init(Constants.BIN_DIR_PATH);
    }

    public static BooleanProperty runningProperty() {
        return running;
    }

    public static boolean isRunning() {
        return running.get();
    }

    public static void start() {
        ConsoleLog.info(getText("alist.status.start"));
        if (running.get()) {
            ConsoleLog.warning(getText("alist.status.start.running"));
            return;
        }

        running.set(true);
        util.exec(START_COMMAND, new ProcessesUtil.OnExecuteListener() {
            @Override
            public void onExecute(String msg) {
                if (ConsoleLog.isInit())
                    ConsoleLog.msg(msg);
            }

            @Override
            public void onExecuteSuccess(int exitValue) {
                running.set(false);
            }

            @Override
            public void onExecuteError(Exception e) {
                running.set(false);
            }
        });
    }

    public static void stop() {
        ConsoleLog.info(getText("alist.status.stop"));
        if (!running.get()) {
            ConsoleLog.warning(getText("alist.status.stop.stopped"));
            return;
        }
        util.destroy();
    }

    static ChangeListener<Boolean> changeListener;

    public static void restart() {
        stop();
        changeListener = (_, _, run) -> {
            if (run) return;
            start();
            if (changeListener != null) {
                running.removeListener(changeListener);
            }
        };
        running.addListener(changeListener);
    }

    private static String getText(String code) {
        return Context.getLanguageBinding(code).get();
    }
}
