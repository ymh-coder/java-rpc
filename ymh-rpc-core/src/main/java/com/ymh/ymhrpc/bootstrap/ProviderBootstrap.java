package com.ymh.ymhrpc.bootstrap;

import com.ymh.ymhrpc.RpcApplication;
import com.ymh.ymhrpc.config.RegistryConfig;
import com.ymh.ymhrpc.config.RpcConfig;
import com.ymh.ymhrpc.model.ServiceMetaInfo;
import com.ymh.ymhrpc.model.ServiceRegisterInfo;
import com.ymh.ymhrpc.registry.LocalRegistry;
import com.ymh.ymhrpc.registry.Registry;
import com.ymh.ymhrpc.registry.RegistryFactory;
import com.ymh.ymhrpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者启动类 （初始化）
 */
public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        //Rpc 框架初始化 (配置和注册中心)
        RpcApplication.init();
        //全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName+"服务注册失败",e);
            }
        }

        //启动TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }

}
