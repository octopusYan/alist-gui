package cn.octopusyan.alistgui.base;

import cn.octopusyan.alistgui.manager.thread.ThreadPoolManager;
import javafx.concurrent.Task;
import lombok.Getter;

/**
 * @author octopus_yan
 */
public abstract class BaseTask extends Task<Void> {
    private final ThreadPoolManager Executor = ThreadPoolManager.getInstance();
    protected Listener listener;
    @Getter
    private final String name;

    protected BaseTask(String name) {
        this.name = name;
    }

    @Override
    protected Void call() throws Exception {
        if (listener != null) listener.onStart();
        task();
        return null;
    }

    protected abstract void task() throws Exception;

    public void onListen(Listener listener) {
        this.listener = listener;
        if (this.listener == null) return;

        setOnRunning(_ -> listener.onRunning());
        setOnCancelled(_ -> listener.onCancelled());
        setOnFailed(_ -> listener.onFailed(getException()));
        setOnSucceeded(_ -> listener.onSucceeded());
    }

    public void execute() {
        Executor.execute(this);
    }

    public interface Listener {
        default void onStart() {
        }

        default void onRunning() {
        }

        default void onCancelled() {
        }

        default void onFailed(Throwable throwable) {
        }

        void onSucceeded();
    }
}
