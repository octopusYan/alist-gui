package cn.octopusyan.alistgui.util;

/**
 * 注册表编辑
 *
 * @author octopus_yan
 */
public class Registry {
    private static final ProcessesUtil util = ProcessesUtil.init(".");

    public static void setStringValue(Root root, String keyPath, String name, String value) {
        setValue(root, keyPath, name, DataType.REG_SZ, value);
    }

    public static void deleteValue(Root root, String keyPath, String name) {
        name = handleSpaces(name);
        util.exec(STR."reg \{Operation.DELETE} \{root.path}\\\{keyPath} /v \{name} /f");
    }

    public static void setValue(Root root, String keyPath, String name, DataType type, String value) {
        name = handleSpaces(name);
        value = handleSpaces(value);
        util.exec(STR."""
                reg \{Operation.ADD} \{root.path}\\\{keyPath} /v \{name} /t \{type} /d \{value}
                """);
    }

    private static String handleSpaces(String str) {
        if (str.contains(" "))
            str = STR."\"\{str}\"";
        return str;
    }

    public enum Operation {
        ADD,
        COMPARE,
        COPY,
        DELETE,
        EXPORT,
        IMPORT,
        LOAD,
        QUERY,
        RESTORE,
        SAVE,
        UNLOAD,
        ;
    }

    public enum Root {
        HKCR("HKEY_CLASSES_ROOT"),
        HKCU("HKEY_CURRENT_USER"),
        HKLM("HKEY_LOCAL_MACHINE"),
        HKU("HKEY_USERS"),
        HKCC("HKEY_CURRENT_CONFIG"),
        ;

        private final String path;

        Root(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    public enum DataType {
        REG_SZ,
        REG_MULTI_SZ,
        REG_DWORD_BIG_ENDIAN,
        REG_DWORD,
        REG_BINARY,
        REG_DWORD_LITTLE_ENDIAN,
        REG_LINK,
        REG_FULL_RESOURCE_DESCRIPTOR,
        REG_EXPAND_SZ,
        ;
    }
}
