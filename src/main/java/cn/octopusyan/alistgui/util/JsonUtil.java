package cn.octopusyan.alistgui.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Jackson 封装工具类
 *
 * @author octopus_yan
 */
public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 时间日期格式
     */
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        //对象的所有字段全部列入序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //所有的日期格式都统一为以下的格式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
        //忽略 在json字符串中存在，但在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Json字符串 转 JavaBean
     *
     * @param jsonString Json字符串
     * @param clazz      Java类对象
     * @param <T>        Java类
     * @return JavaBean
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("失败：{}", e.getMessage());
        }
        return t;
    }

    /**
     * 读取Json文件 转 JavaBean
     *
     * @param file  Json文件
     * @param clazz Java类对象
     * @param <T>   Java类
     * @return JavaBean
     */
    public static <T> T parseObject(File file, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            log.error("失败：{}", e.getMessage());
        }
        return t;
    }

    /**
     * 读取Json字符串 转 JavaBean集合
     *
     * @param jsonArray Json字符串
     * @param reference 类型
     * @param <T>       JavaBean类型
     * @return JavaBean集合
     */
    public static <T> T parseJsonArray(String jsonArray, TypeReference<T> reference) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonArray, reference);
        } catch (JsonProcessingException e) {
            log.error("失败：{}", e.getMessage());
        }
        return t;
    }


    /**
     * JavaBean 转 Json字符串
     *
     * @param object JavaBean
     * @return Json字符串
     */
    public static String toJsonString(Object object) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("失败：{}", e.getMessage());
        }
        return jsonString;
    }

    /**
     * JavaBean 转 字节数组
     *
     * @param object JavaBean
     * @return 字节数组
     */
    public static byte[] toByteArray(Object object) {
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("失败：{}", e.getMessage());
        }
        return bytes;
    }

    /**
     * JavaBean序列化到文件
     *
     * @param file   写入文件对象
     * @param object JavaBean
     */
    public static void objectToFile(File file, Object object) {
        try {
            objectMapper.writeValue(file, object);
        } catch (Exception e) {
            log.error("失败：{}", e.getMessage());
        }
    }


    /**
     * Json字符串 转 JsonNode
     *
     * @param jsonString Json字符串
     * @return JsonNode
     */
    public static JsonNode parseJsonObject(String jsonString) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.error("失败：{}", e.getMessage());
        }
        return jsonNode;
    }

    /**
     * JavaBean 转 JsonNode
     *
     * @param object JavaBean
     * @return JsonNode
     */
    public static JsonNode parseJsonObject(Object object) {
        return objectMapper.valueToTree(object);
    }

    /**
     * JsonNode 转 Json字符串
     *
     * @param jsonNode JsonNode
     * @return Json字符串
     */
    public static String toJsonString(JsonNode jsonNode) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            log.error("失败：{}", e.getMessage());
        }
        return jsonString;
    }
}
