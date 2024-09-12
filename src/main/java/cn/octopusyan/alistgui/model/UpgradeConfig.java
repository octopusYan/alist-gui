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
    private AList aList = new AList();
    private Gui gui = new Gui();
}
