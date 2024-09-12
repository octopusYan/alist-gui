package cn.octopusyan.alistgui.task;

import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.model.upgrade.AList;
import cn.octopusyan.alistgui.model.upgrade.UpgradeApp;
import cn.octopusyan.alistgui.util.JsonUtil;
import cn.octopusyan.alistgui.viewModel.AboutViewModule;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

/**
 * 检查更新任务
 *
 * @author octopus_yan
 */
public class UpgradeTask extends BaseTask {
    private final AboutViewModule viewModule;
    private final UpgradeApp app;

    public UpgradeTask(AboutViewModule viewModel, UpgradeApp app) {
        this.viewModule = viewModel;
        this.app = app;
    }

    @Override
    protected void task() throws Exception {
        String responseStr = HttpUtil.getInstance().get(app.getReleaseApi(), null, null);
        JsonNode response = JsonUtil.parseJsonObject(responseStr);

        // TODO 校验返回内容
        String newVersion = response.get("tag_name").asText();

        runLater(() -> {
            if (app instanceof AList) {
                viewModule.aListUpgradeProperty().setValue(!StringUtils.equals(app.getVersion(), newVersion));
                viewModule.aListNewVersionProperty().setValue(newVersion);
            } else {
                viewModule.guiUpgradeProperty().setValue(!StringUtils.equals(app.getVersion(), newVersion));
                viewModule.guiNewVersionProperty().setValue(newVersion);
            }
        });
    }
}
