package com.ymh.example.provider;

import com.ymh.example.common.service.UserService;
import com.ymh.ymhrpc.RpcApplication;
import com.ymh.ymhrpc.registry.LocalRegistry;
import com.ymh.ymhrpc.server.HttpServer;
import com.ymh.ymhrpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
