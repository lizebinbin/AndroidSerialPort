package com.lzb.androidserialport;

import android.app.Application;
import android.widget.Toast;

import com.lzb.androidserialport.serial.SerialPort;
import com.lzb.androidserialport.serial.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * Created by lzb on 2019/1/9.
 */
public class MyApplication extends Application {
    public static MyApplication instance;

    public  SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort       mSerialPort       = null;
    private String[]         allDevices;
    private String[]         allDevicesPath;

    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //查找所有串口方法
        findSerialPorts();
        //打开串口
        try {
            getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findSerialPorts() {
        allDevices = mSerialPortFinder.getAllDevices();
        allDevicesPath = mSerialPortFinder.getAllDevicesPath();
        if (allDevices != null)
            Toast.makeText(this, "allDevices:" + Arrays.toString(allDevices), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "allDevices:  allDevices == null", Toast.LENGTH_LONG).show();
    }

    /**
     * 获取串口
     *
     * @return
     * @throws SecurityException
     * @throws IOException
     * @throws InvalidParameterException
     */
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            //这里是打开了/dev/ttyS3 串口，波特率9600,
            mSerialPort = new SerialPort(new File("/dev/ttyS3"), 9600, 0);
        }
        return mSerialPort;
    }

    /**
     * 关闭串口
     * 使用结束时记得关闭串口
     */
    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
