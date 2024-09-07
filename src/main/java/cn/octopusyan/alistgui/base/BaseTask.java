package cn.octopusyan.alistgui.base;

import cn.octopusyan.alistgui.manager.thread.ThreadPoolManager;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * @author octopus_yan
 */
public abstract class BaseTask extends Task<Void> {
    private final ThreadPoolManager Executor = ThreadPoolManager.getInstance();

    @Override
    protected Void call() throws Exception {
        task();
        return null;
    }

    protected void runLater(Runnable runnable) {
        Platform.runLater(runnable);
    }

    protected abstract void task() throws Exception;

    public void onListen(Listener listener) {
        if (listener == null) return;

        setOnRunning(_ -> listener.onRunning());
        setOnCancelled(_ -> listener.onCancelled());
        setOnFailed(_ -> listener.onFailed(getException()));
        setOnSucceeded(_ -> listener.onSucceeded());
    }

    public void execute() {
        Executor.execute(this);
    }

    public interface Listener {
        default void onRunning() {
        }

        default void onCancelled() {
        }

        default void onFailed(Throwable throwable) {
        }

        void onSucceeded();
    }
}
