package cn.octopusyan.alistgui.task;

import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.model.upgrade.UpgradeApp;
import cn.octopusyan.alistgui.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

/**
 * 检查更新任务
 *
 * @author octopus_yan
 */
public class CheckUpdateTask extends BaseTask {
    private final UpgradeApp app;

    public CheckUpdateTask(UpgradeApp app) {
        super(STR."check update \{app.getRepo()}");
        this.app = app;
    }

    @Override
    protected void task() throws Exception {
        String responseStr = HttpUtil.getInstance().get(app.getReleaseApi(), null, null);
        JsonNode response = JsonUtil.parseJsonObject(responseStr);

        // TODO 校验返回内容
        String newVersion = response.get("tag_name").asText();

        if (listener != null && listener instanceof UpgradeListener lis)
            lis.onChecked(!StringUtils.equals(app.getVersion(), newVersion), newVersion);
    }

    public interface UpgradeListener extends BaseTask.Listener {
        @Override
        default void onSucceeded() {
            // do nothing ...
        }

        void onChecked(boolean hasUpgrade, String version);
    }
}
