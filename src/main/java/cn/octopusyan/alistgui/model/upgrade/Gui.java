package cn.octopusyan.alistgui.model.upgrade;

import cn.octopusyan.alistgui.util.PropertiesUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author octopus_yan
 */
@Data
public class Gui implements UpgradeApp {
    @JsonIgnore
    private final String owner = "octopusYan";

    @JsonIgnore
    private final String repo = "alist-gui";

    private String releaseFile = "alist-gui-windows-nojre.zip";

    private String version = PropertiesUtils.getInstance().getProperty("app.version");
}
