package cn.octopusyan.alistgui.model.upgrade;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author octopus_yan
 */
public interface UpgradeApp {
    @JsonIgnore
    default String getReleaseApi() {
        return STR."https://api.github.com/repos/\{getOwner()}/\{getRepo()}/releases/latest";
    }

    @JsonIgnore
    default String getDownloadUrl(String version) {
        return STR."https://github.com/\{getOwner()}/\{getRepo()}/releases/download/\{version}/\{getReleaseFile()}";
    }

    String getOwner();

    String getRepo();

    String getReleaseFile();

    String getVersion();
}
