package cn.octopusyan.alistgui.enums;

import cn.octopusyan.alistgui.config.Context;
import javafx.beans.binding.StringBinding;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 代理类型
 *
 * @author octopus_yan
 */
@Getter
@RequiredArgsConstructor
public enum ProxySetup {
    NO_PROXY("no_proxy"),
    SYSTEM("system"),
    MANUAL("manual");

    private final String name;

    @Override
    public String toString() {
        return getBinding().get();
    }

    public StringBinding getBinding() {
        return Context.getLanguageBinding(STR."proxy.setup.label.\{getName()}");
    }
}
