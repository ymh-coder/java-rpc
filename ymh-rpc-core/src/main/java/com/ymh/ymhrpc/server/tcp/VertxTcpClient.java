package com.ymh.ymhrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.ymh.ymhrpc.RpcApplication;
import com.ymh.ymhrpc.model.RpcRequest;
import com.ymh.ymhrpc.model.RpcResponse;
import com.ymh.ymhrpc.model.ServiceMetaInfo;
import com.ymh.ymhrpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * TCP 请求客户端
 */
public class VertxTcpClient {

    /**
     * 发送请求
     *
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws InterruptedException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.out.println("Failed to connect to TCP server");
                        return;
                    }
                    NetSocket socket = result.result();

                    // 发送消息
                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    //生成全局请求ID
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    try {
                        Buffer encodeBuffer= ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    }catch (IOException e){
                        throw new RuntimeException("协议消息编码错误");
                    }

                    //接受响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);
                });

        //阻塞，直到响应完成，才会继续向下执行
        RpcResponse rpcResponse = responseFuture.get();

        netClient.close();
        return rpcResponse;
    }

    public void start(){
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888,"localhost",result -> {
            if(result.succeeded()){
                System.out.println("Connected to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();

                for (int i = 0; i < 1000; i++) {
//                    socket.write("Hello,server! Hello,server! Hello,server!");

                    Buffer buffer = Buffer.buffer();
                    String str="Hello,server! Hello,server! Hello,server!";

                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }

                socket.handler(buffer -> {
                    System.out.println("Received data from server"+buffer.toString());
                });
            }else {
                System.out.println("Failed to connect to TCP server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }

}
