package com.cenzer.InterfaceModule;

import com.cenzer.EncodeDecodeModule.ByteQueue;

public interface ReceiverResultInterface
{

    void onScanSuccess(int successCode, ByteQueue byteQueue);
    void onScanFailed(int errorCode);


}
