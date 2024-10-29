package cn.octopusyan.alistgui.model.upgrade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private StringProperty version = new SimpleStringProperty("unknown");

    public StringProperty versionProperty() {
        return version;
    }

    public void setVersion(String version) {
        this.version.set(version);
    }

    public String getVersion() {
        return version.get();
    }
}
