package com.lzb.androidserialport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lzb.androidserialport.serial.SerialPort;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 以下方法可以在baseActivity或者BaseFragment中写，子类直接调用即可
     */

    //向串口写数据
    public void sendData2Serial(byte[] data) {
        try {
            MyApplication.getInstance().getSerialPort().sendBufferToSerial(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听串口数据
    public void startReceiveData(SerialPort.OnReceiveDataListener listener) {
        try {
            MyApplication.getInstance().getSerialPort().startReceiveData(listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭接收数据  退出时需要调用
    public void stopReceiveData() {
        try {
            MyApplication.getInstance().getSerialPort().stopReceiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
