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
    private final String owner = "alist-org";
    @JsonIgnore
    private final String repo = "alist";

    private String releaseFile = "alist-gui-{version}-windows.zip";
    private String version = PropertiesUtils.getInstance().getProperty("app.version");

    public String getReleaseFile() {
        return getReleaseFile(version);
    }

    public String getReleaseFile(String version) {
        return releaseFile.replace("{version}", version);
    }
}
