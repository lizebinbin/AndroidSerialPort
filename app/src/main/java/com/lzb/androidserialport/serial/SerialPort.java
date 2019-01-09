package com.lzb.androidserialport.serial;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lzb on 2018/1/10.
 */
public class SerialPort {
    private static final String           TAG = "SerialPort";
    private              FileDescriptor   mFd;
    private              FileInputStream  mFileInputStream;
    private              FileOutputStream mFileOutputStream;

    private ReadThread            readThread;
    private OnReceiveDataListener listener;

    /**
     * 打开串口，得到串口输入输出流
     *
     * @param device   串口物理路径
     * @param baudrate 波特率
     * @param flags    打开方式
     * @throws SecurityException
     * @throws IOException
     */
    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/xbin/su");
                String str = "chmod 666";
                String cmd = str + " " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    //获取输入流
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    //获取输出流
    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * 向串口发送数据
     *
     * @param data
     */
    public void sendBufferToSerial(byte[] data) {
        try {
            mFileOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始接收串口数据
     *
     * @param onReceiveDataListener 数据回调
     */
    public void startReceiveData(OnReceiveDataListener onReceiveDataListener) {
        readThread = new ReadThread();
        readThread.start();
        listener = onReceiveDataListener;
    }

    /**
     * 停止接收数据
     */
    public void stopReceiveData() {
        if (readThread != null) {
            if (readThread.isInterrupted())
                readThread.interrupt();
        }
        readThread = null;
        listener = null;
    }

    /**
     * 接收数据线程
     */
    class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                final byte[] buffer;
                try {
                    buffer = new byte[128];
                    if (mFileInputStream == null) return;
                    size = mFileInputStream.read(buffer);

                    if (size > 0) {
                        //回调
                        if (listener != null)
                            listener.receiveData(size, buffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface OnReceiveDataListener {
        void receiveData(int len, byte[] receiveData);
    }

    // jni打开
    private native static FileDescriptor open(String path, int baudrate, int flags);

    // jni关闭
    public native void close();

    //加载库
    static {
        System.loadLibrary("SerialPort");
    }
}