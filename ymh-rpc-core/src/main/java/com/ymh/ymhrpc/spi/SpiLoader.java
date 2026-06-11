package com.ymh.ymhrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.ymh.ymhrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * SPI加载器
 * 自定义实现，支持键值对映射
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类：接口名=》（key=》实现类）
     */
    private static final Map<String, Map<String,Class<?>>> loaderMap=new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复new），类路径=>对象实例，单例模式
     */
    private static final Map<String,Object> instanceCache=new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR="META/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR="META/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS=new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST= Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll(){
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取某个接口的实例
     * @param tClazz
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> tClazz,String key) {
        String tClazzName = tClazz.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClazzName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型",tClazzName));
        }
        if(!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型",tClazzName,key));
        }

        Class<?> implClass = keyClassMap.get(key);

        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)){
            try {
                instanceCache.put(implClassName,implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg,e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载某个类型
     * @param loadClass
     * @return
     */
    public static Map<String,Class<?>> load(Class<?> loadClass){
        log.info("加载类型为{} 的SPI",loadClass.getName());
        // 扫描路径，用户自定义的SPI优先级高于系统SPI
        Map<String,Class<?>> keyClassMap=new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            //读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line=bufferedReader.readLine())!=null){
                        String[] strArray = line.split("=");
                        if(strArray.length>1){
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key,Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error",e);
                }
            }
        }
        loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;
    }

    public static void main(String[] args) {
        loadAll();
        System.out.println(loaderMap);
        Serializer jdk = getInstance(Serializer.class, "a");
        System.out.println(jdk);

        Serializer hessian = getInstance(Serializer.class, "b");
        System.out.println(hessian);

        Serializer json = getInstance(Serializer.class, "c");
        System.out.println(json);

        Serializer kryo = getInstance(Serializer.class, "d");
        System.out.println(kryo);
    }
}
