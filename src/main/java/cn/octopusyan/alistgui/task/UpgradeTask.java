package cn.octopusyan.alistgui.task;

import cn.hutool.core.util.StrUtil;
import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.model.UpgradeConfig;
import cn.octopusyan.alistgui.model.upgrade.UpgradeApp;
import cn.octopusyan.alistgui.util.JsonUtil;
import cn.octopusyan.alistgui.viewModel.SetupViewModel;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

/**
 * 检查更新任务
 *
 * @author octopus_yan
 */
public class UpgradeTask extends BaseTask {
    private final SetupViewModel vm;
    private final UpgradeApp app;

    public UpgradeTask(SetupViewModel viewModel, UpgradeApp app) {
        this.vm = viewModel;
        this.app = app;
    }

    @Override
    protected void task() throws Exception {
        String releaseApi = StrUtil.format(UpgradeConfig.RELEASE_API, app.getOwner(), app.getRepo());
        String responseStr = HttpUtil.getInstance().get(releaseApi, null, null);
        JsonNode response = JsonUtil.parseJsonObject(responseStr);

        String newVersion = response.get("tag_name").asText();
        String downloadUrl = StrUtil.format(UpgradeConfig.DOWNLOAD_API,
                app.getOwner(),
                app.getRepo(),
                newVersion,
                app.getReleaseFile()
        );

        runLater(() -> {
            vm.aListUpgradeProperty().setValue(StringUtils.equals(app.getVersion(), newVersion));
            vm.aListNewVersionProperty().setValue(newVersion);
        });
    }
}
