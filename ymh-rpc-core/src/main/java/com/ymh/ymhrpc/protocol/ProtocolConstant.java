package com.ymh.ymhrpc.protocol;

/**
 * 协议常量
 */
public interface ProtocolConstant {
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    byte PROTOCOL_VERSION = 0x1;
}
