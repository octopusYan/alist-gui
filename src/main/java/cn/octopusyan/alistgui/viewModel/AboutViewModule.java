package cn.octopusyan.alistgui.viewModel;

import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.task.UpgradeTask;
import cn.octopusyan.alistgui.util.alert.AlertUtil;
import javafx.beans.property.*;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;

/**
 * 关于
 *
 * @author octopus_yan
 */
@Slf4j
public class AboutViewModule {
    private final StringProperty aListVersion = new SimpleStringProperty(ConfigManager.aListVersion());
    private final StringProperty aListNewVersion = new SimpleStringProperty("");
    private final BooleanProperty aListUpgrade = new SimpleBooleanProperty(false);

    public AboutViewModule() {
        aListVersion.addListener((_, _, newValue) -> ConfigManager.aListVersion(newValue));
    }

    public Property<String> aListVersionProperty() {
        return aListVersion;
    }

    public BooleanProperty aListUpgradeProperty() {
        return aListUpgrade;
    }

    public StringProperty aListNewVersionProperty() {
        return aListNewVersion;
    }

    /**
     * 检查alist更新
     */
    public void checkAListUpdate() {
        var task = new UpgradeTask(this, ConfigManager.aList());
        task.onListen(new BaseTask.Listener() {
            @Override
            public void onSucceeded() {
                AlertUtil.confirm()
                        .content(STR."""
                    当前版本    :   \{aListVersion.get()}
                    最新版本    :   \{aListNewVersion.get()}
                    """)
                        .title(Context.getLanguageBinding("about.alist.update").getValue())
                        .show(new AlertUtil.OnChoseListener() {
                            @Override
                            public void confirm() {
                                log.info("========confirm==========");
                            }

                            @Override
                            public void cancelOrClose(ButtonType buttonType) {
                                log.info("========cancelOrClose==========");
                            }
                        });
            }

            @Override
            public void onFailed(Throwable throwable) {
                AlertUtil.exception(new Exception(throwable)).show();
            }
        });
        task.execute();
    }
}
