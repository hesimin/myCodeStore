package cn.hesimin.log;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 操作日志切面
 *
 * @author hesimin 2017-07-07
 */
@Aspect
@Component
public class SysOperationLogAspect {


    @Around(value = "@annotation(sysOperationLog)")
    public Object afterAround(ProceedingJoinPoint pjp, SysOperationLog sysOperationLog) throws Throwable {
        // 调用方法的参数
        Object[] args = pjp.getArgs();
        // 调用的方法名
        String method = pjp.getSignature().getName();
        // 获取目标对象(形如：com.action.admin.LoginAction@1a2467a)
        Object target = pjp.getTarget();
        //获取目标对象的类名(形如：com.action.admin.LoginAction)
        String targetName = pjp.getTarget().getClass().getName();
        // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
        Object result = null;//result的值就是被拦截方法的返回值
        boolean success = false;

        try {
            result = pjp.proceed();
            success = true;
        } finally {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            //从获取RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            String url = request.getRequestURI();

            if (sysOperationLog.args()) {
                if (args != null && args.length > 0) {
                    List argList = new ArrayList(args.length);
                    for (Object arg : args) {
                        if (!isExcludeArgsClass(sysOperationLog, arg)) {
                            argList.add(arg);
                        }
                    }
                    System.out.println(JSON.toJSONString(argList));
                }
            }

            if (sysOperationLog.result()) {
                System.out.println(JSON.toJSONString(result));
            }

            // 描述里的参数处理
            if (args == null || args.length == 0) {
                System.out.println(sysOperationLog.discription());
            } else {
                try {
                    System.out.println(getDiscription(sysOperationLog.discription(), args));
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
        return result;
    }

    private boolean isExcludeArgsClass(SysOperationLog sysOperationLog, Object arg) {
        // spring 的这几个类不需要记录
        if (ServletRequest.class.isInstance(arg) || ServletResponse.class.isInstance(arg) || Model.class.isInstance(arg)) {
            return true;
        }
        if (sysOperationLog.excludeArgsClass() != null && sysOperationLog.excludeArgsClass().length > 0) {
            for (Class eClass : sysOperationLog.excludeArgsClass()) {
                if (eClass.isInstance(arg)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final Pattern GROUP_PATTERN = Pattern.compile("(?<=\\{)[^\\}]+");
    private static final Pattern ARRAY_PATTERN = Pattern.compile("(?<=\\[)[^\\]]+");

    public static String getDiscription(String oper, Object[] param) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Matcher m = GROUP_PATTERN.matcher(oper);
        // 以大括号为分组，每次处理一组
        while (m.find()) {
            String group = m.group();

            Object target = null;// 取值目标对象

            boolean first = true;// 如果是第一段，需要考虑第一段的第一个是从方法参数中获取
            groupEnd:
            // 数组、集合、map使用中括号[],属性值用点 “.”，这里按属性点拆成独立段
            for (String s : group.split("[.]")) {
                if (!first && target == null) {
                    // 该参数为null，不能拿到值
                    break;
                }

                // aName、aName[0][1] 只可能是这些情况，所以先取属性值，然后取集合。另外还需要考虑第一个节点 0[1]、[0][1]
                String attrName = s.replaceAll("\\[.*", "");
                if (!"".equals(attrName)) {
                    if (first) {// 0[1]
                        target = get(param, Integer.parseInt(attrName));
                        first = false;
                    } else {// aName[1]
                        // 通过反射获取属性值
                        PropertyDescriptor pd = new PropertyDescriptor(attrName, target.getClass());
                        Method method = pd.getReadMethod();
                        method.setAccessible(true);
                        target = method.invoke(target);
                    }
                }

                Matcher child = ARRAY_PATTERN.matcher(s);
                while (child.find()) {
                    String cv = child.group();
                    if (first) {
                        target = get(param, Integer.parseInt(cv));
                        first = false;
                        continue;
                    }

                    // 判断集合类型，然后获取值
                    if (target.getClass().isArray()) {
                        target = get((Object[]) target, Integer.parseInt(cv));
                    } else if (target instanceof List) {
                        target = get((List) target, Integer.parseInt(cv));
                    } else if (target instanceof Map) {
                        // key必须是字符串，否则就拿不到
                        target = ((Map) target).get(cv);
//                    } else if(target instanceof Set){
//                        // Set 只能通过迭代器取出，是无法指定取哪个
//                        throw new RuntimeException("Not support Set.");
                    } else {
                        // 不能支持的类型，把该对象用json替换
                        System.err.println("Not support class. group = " + s);
                        target = JSON.toJSON(target);
                        break groupEnd;
                    }
                }
            }

            // 对该组进行值替换处理
            String targetS = "";
            if (target != null) {
                if (target instanceof Date) {
                    targetS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(target);
                } else {
                    targetS = target.toString();
                }
            }
            oper = oper.replace("{" + group + "}", targetS);
        }
        return oper;
    }

    private static Object get(Object[] arr, int index) {
        if (arr == null || arr.length <= index) {
            return null;
        }
        return arr[index];
    }

    private static Object get(List arr, int index) {
        if (arr == null || arr.size() <= index) {
            return null;
        }
        return arr.get(index);
    }
}
