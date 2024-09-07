package cn.octopusyan.alistgui.model;

import cn.octopusyan.alistgui.model.upgrade.AList;
import cn.octopusyan.alistgui.model.upgrade.Gui;
import lombok.Data;

/**
 * 更新配置
 *
 * @author octopus_yan
 */
@Data
public class UpgradeConfig {
    public static final String RELEASE_API = "https://api.github.com/repos/{}/{}/releases/latest";
    public static final String DOWNLOAD_API = "https://github.com/{}/{}/releases/download/{}/{}";
    private AList aList = new AList();
    private Gui gui = new Gui();
}
