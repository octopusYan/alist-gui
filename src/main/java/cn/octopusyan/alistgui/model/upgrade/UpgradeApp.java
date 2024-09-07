package cn.octopusyan.alistgui.model.upgrade;

/**
 * @author octopus_yan
 */
public interface UpgradeApp {
    String getOwner();

    String getRepo();

    String getReleaseFile();

    String getVersion();
}
