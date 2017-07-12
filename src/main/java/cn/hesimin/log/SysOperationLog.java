package cn.hesimin.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hesimin 2017-07-07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SysOperationLog {
    // 是否记录参数
    boolean args() default true;

    // 是否记录结果
    boolean result() default true;

    // 描述：使用{}获取方法的参数，{}里面数组、集合、map使用中括号[],属性值用点 “.”
    String discription();
}
