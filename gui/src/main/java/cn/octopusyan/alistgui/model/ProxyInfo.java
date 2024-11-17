package cn.octopusyan.alistgui.model;

import lombok.Data;

/**
 * 代理信息
 *
 * @author octopus_yan
 */
@Data
public class ProxyInfo {
    private String host = "";
    private String port = "";
    private String username = "";
    private String password = "";
}
