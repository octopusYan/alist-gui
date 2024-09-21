package cn.octopusyan.alistgui.task;

import cn.octopusyan.alistgui.base.BaseTask;
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
    private final String savePath;

    public DownloadTask(String downloadUrl, String savePath) {
        super(STR."Download \{downloadUrl}");
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
    }

    public void onListen(DownloadListener listener) {
        super.onListen(listener);
    }

    @Override
    protected void task() throws Exception {
        HttpUtil.getInstance().download(
                downloadUrl,
                savePath,
                listener instanceof DownloadListener ? ((DownloadListener) listener)::onProgress : null
        );
    }

    public interface DownloadListener extends BaseTask.Listener {
        void onProgress(Long total, Long progress);
    }
}
