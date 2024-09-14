package cn.octopusyan.alistgui.task;

import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO 下载任务
 *
 * @author octopus_yan
 */
@Slf4j
public class DownloadTask extends BaseTask {
    private final String downloadUrl;

    public DownloadTask(String downloadUrl) {
        super(STR."Download \{downloadUrl}");
        this.downloadUrl = downloadUrl;
    }

    public void onListen(DownloadListener listener) {
        super.onListen(listener);
    }

    @Override
    protected void task() throws Exception {
        HttpUtil.getInstance().download(
                downloadUrl,
                Constants.BIN_DIR_PATH,
                listener instanceof DownloadListener ? ((DownloadListener) listener)::onProgress : null
        );
    }

    public interface DownloadListener extends BaseTask.Listener {
        void onProgress(Long total, Long progress);
    }
}
