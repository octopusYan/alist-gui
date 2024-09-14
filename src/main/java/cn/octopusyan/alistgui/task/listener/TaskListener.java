package cn.octopusyan.alistgui.task.listener;

import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import cn.octopusyan.alistgui.task.CheckUpdateTask;
import cn.octopusyan.alistgui.task.DownloadTask;
import cn.octopusyan.alistgui.view.alert.AlertUtil;
import cn.octopusyan.alistgui.view.alert.builder.ProgressBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务监听器默认实现
 *
 * @author octopus_yan
 */
@Slf4j
public abstract class TaskListener implements BaseTask.Listener {
    private final BaseTask task;
    // 加载弹窗
    final ProgressBuilder progress = AlertUtil.progress();

    public TaskListener(BaseTask task) {
        this.task = task;
        progress.onCancel(task::cancel);
    }

    @Override
    public void onStart() {
        log.info(STR."\{task.getName()} start ...");
        ConsoleLog.info(task.getName(), "start ...");
    }

    @Override
    public void onRunning() {
        progress.show();
    }

    @Override
    public void onCancelled() {
        progress.close();
        log.info(STR."\{task.getName()} cancel ...");
        ConsoleLog.info(task.getName(), "cancel ...");
    }

    @Override
    public void onFailed(Throwable throwable) {
        progress.close();
        log.error(STR."\{task.getName()} fail ...", throwable);
        ConsoleLog.error(task.getName(), STR."fail : \{throwable.getMessage()}");
        onFail(throwable);
    }

    @Override
    public void onSucceeded() {
        progress.close();
        log.info(STR."\{task.getName()} success ...");
        ConsoleLog.info(task.getName(), "success ...");
        onSucceed();
    }

    protected abstract void onSucceed();

    protected void onFail(Throwable throwable) {
    }

    /**
     * 下载任务监听默认实现
     */
    public static abstract class DownloadListener extends TaskListener implements DownloadTask.DownloadListener {

        private volatile int lastProgress = 0;

        public DownloadListener(BaseTask task) {
            super(task);
        }

        @Override
        public void onProgress(Long total, Long progress) {
            int a = (int) (((double) progress / total) * 100);
            if (a % 10 == 0) {
                if (a != lastProgress) {
                    lastProgress = a;
                    ConsoleLog.info(STR."\{lastProgress} %");
                }
            }
        }
    }

    /**
     * 检查更新监听默认实现
     */
    public static abstract class UpgradeUpgradeListener extends TaskListener implements CheckUpdateTask.UpgradeListener {
        public UpgradeUpgradeListener(BaseTask task) {
            super(task);
        }

        @Override
        protected void onSucceed() {
            // do nothing ...
        }
    }
}
