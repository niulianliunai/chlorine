package com.chlorine.base.mvc.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.BeansException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class SpringReflectUtils {


    /**
     * 对象名称获取spring bean对象
     * @param name
     * @return
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return SpringUtil.getBean(name);
    }

    /**
     * 根据 服务名称 ，方法名 反射调用  spring bean 中的 方法
     * @param serviceName 服务名
     * @param methodName 方法名
     * @param params 参数
     * @return
     * @throws Exception
     */
    public static Object springInvokeMethod(String serviceName, String methodName, Object[] params) throws Exception {
        Object service = getBean(serviceName);
        Class<? extends Object>[] paramClass = null;
        if (params != null) {
            int paramsLength = params.length;
            paramClass = new Class[paramsLength];
            for (int i = 0; i < paramsLength; i++) {
                paramClass[i] = params[i].getClass();
            }
        }
        // 找到方法
        Method method = ReflectionUtils.findMethod(service.getClass(), methodName, paramClass);
        // 执行方法
        return ReflectionUtils.invokeMethod(method, service, params);
    }
}
