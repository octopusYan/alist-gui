package cn.octopusyan.alistgui.model.upgrade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author octopus_yan
 */
@Data
public class AList implements UpgradeApp {
    @JsonIgnore
    private final String owner = "alist-org";
    @JsonIgnore
    private final String repo = "alist";

    private String releaseFile = "alist-windows-amd64.zip";
    private String version = "unknown";
}
