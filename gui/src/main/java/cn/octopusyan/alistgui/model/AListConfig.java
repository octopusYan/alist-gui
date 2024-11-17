package cn.octopusyan.alistgui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * alist 配置文件 model
 *
 * @author octopus_yan
 */
@NoArgsConstructor
@Getter
public class AListConfig {

    @JsonProperty("force")
    private Boolean force;
    @JsonProperty("site_url")
    private String siteUrl;
    @JsonProperty("cdn")
    private String cdn;
    @JsonProperty("jwt_secret")
    private String jwtSecret;
    @JsonProperty("token_expires_in")
    private Integer tokenExpiresIn;
    @JsonProperty("database")
    private Database database;
    @JsonProperty("meilisearch")
    private MeiliSearch meilisearch;
    @JsonProperty("scheme")
    private Scheme scheme;
    @JsonProperty("temp_dir")
    private String tempDir;
    @JsonProperty("bleve_dir")
    private String bleveDir;
    @JsonProperty("dist_dir")
    private String distDir;
    @JsonProperty("log")
    private Log log;
    @JsonProperty("delayed_start")
    private Integer delayedStart;
    @JsonProperty("max_connections")
    private Integer maxConnections;
    @JsonProperty("tls_insecure_skip_verify")
    private Boolean tlsInsecureSkipVerify;
    @JsonProperty("tasks")
    private Tasks tasks;
    @JsonProperty("cors")
    private Cors cors;
    @JsonProperty("s3")
    private S3 s3;

    @NoArgsConstructor
    @Getter
    public static class Database {
        @JsonProperty("type")
        private String type;
        @JsonProperty("host")
        private String host;
        @JsonProperty("port")
        private Integer port;
        @JsonProperty("user")
        private String user;
        @JsonProperty("password")
        private String password;
        @JsonProperty("name")
        private String name;
        @JsonProperty("db_file")
        private String dbFile;
        @JsonProperty("table_prefix")
        private String tablePrefix;
        @JsonProperty("ssl_mode")
        private String sslMode;
        @JsonProperty("dsn")
        private String dsn;
    }

    @NoArgsConstructor
    @Getter
    public static class MeiliSearch {
        @JsonProperty("host")
        private String host;
        @JsonProperty("api_key")
        private String apiKey;
        @JsonProperty("index_prefix")
        private String indexPrefix;
    }

    @NoArgsConstructor
    @Getter
    public static class Scheme {
        @JsonProperty("address")
        private String address;
        @JsonProperty("http_port")
        private Integer httpPort;
        @JsonProperty("https_port")
        private Integer httpsPort;
        @JsonProperty("force_https")
        private Boolean forceHttps;
        @JsonProperty("cert_file")
        private String certFile;
        @JsonProperty("key_file")
        private String keyFile;
        @JsonProperty("unix_file")
        private String unixFile;
        @JsonProperty("unix_file_perm")
        private String unixFilePerm;
    }

    @NoArgsConstructor
    @Getter
    public static class Log {
        @JsonProperty("enable")
        private Boolean enable;
        @JsonProperty("name")
        private String name;
        @JsonProperty("max_size")
        private Integer maxSize;
        @JsonProperty("max_backups")
        private Integer maxBackups;
        @JsonProperty("max_age")
        private Integer maxAge;
        @JsonProperty("compress")
        private Boolean compress;
    }

    @NoArgsConstructor
    @Getter
    public static class Tasks {
        @JsonProperty("download")
        private Download download;
        @JsonProperty("transfer")
        private Transfer transfer;
        @JsonProperty("upload")
        private Upload upload;
        @JsonProperty("copy")
        private Copy copy;

        @NoArgsConstructor
        @Getter
        public static class Download {
            @JsonProperty("workers")
            private Integer workers;
            @JsonProperty("max_retry")
            private Integer maxRetry;
            @JsonProperty("task_persistant")
            private Boolean taskPersistant;
        }

        @NoArgsConstructor
        @Getter
        public static class Transfer {
            @JsonProperty("workers")
            private Integer workers;
            @JsonProperty("max_retry")
            private Integer maxRetry;
            @JsonProperty("task_persistant")
            private Boolean taskPersistant;
        }

        @NoArgsConstructor
        @Getter
        public static class Upload {
            @JsonProperty("workers")
            private Integer workers;
            @JsonProperty("max_retry")
            private Integer maxRetry;
            @JsonProperty("task_persistant")
            private Boolean taskPersistant;
        }

        @NoArgsConstructor
        @Getter
        public static class Copy {
            @JsonProperty("workers")
            private Integer workers;
            @JsonProperty("max_retry")
            private Integer maxRetry;
            @JsonProperty("task_persistant")
            private Boolean taskPersistant;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Cors {
        @JsonProperty("allow_origins")
        private List<String> allowOrigins;
        @JsonProperty("allow_methods")
        private List<String> allowMethods;
        @JsonProperty("allow_headers")
        private List<String> allowHeaders;
    }

    @NoArgsConstructor
    @Getter
    public static class S3 {
        @JsonProperty("enable")
        private Boolean enable;
        @JsonProperty("port")
        private Integer port;
        @JsonProperty("ssl")
        private Boolean ssl;
    }
}
