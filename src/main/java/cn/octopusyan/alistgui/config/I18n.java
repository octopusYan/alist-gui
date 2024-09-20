package cn.octopusyan.alistgui.config;

import java.lang.annotation.*;

/**
 * 显示文本绑定
 *
 * @author octopus_yan
 */
@Documented
@Target({ElementType.FIELD})//用此注解用在属性上。
@Retention(RetentionPolicy.RUNTIME)
public @interface I18n {
    String key() default "";
}
