package com.ymh.ymhrpc.fault.tolerant;

import com.ymh.ymhrpc.loadbalancer.LoadBalancer;
import com.ymh.ymhrpc.loadbalancer.RoundRobinLoadBalancer;
import com.ymh.ymhrpc.spi.SpiLoader;

/**
 * 容错策略工厂 （工厂模式，用于获取容错策略对象）
 */
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY=new FailFastTolerantStrategy();

    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class,key);
    }
}
