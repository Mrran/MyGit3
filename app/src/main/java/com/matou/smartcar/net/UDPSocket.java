package com.matou.smartcar.net;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.matou.smartcar.bean.Users;
import com.matou.smartcar.event.BaseDataEvent;
import com.matou.smartcar.protocol.Icdc2HmiOuterClass;
import com.matou.smartcar.util.DeviceUtil;
import com.matou.smartcar.util.WifiUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPSocket {

    private static final String TAG = "UDPSocket";

    // 单个CPU线程池大小
    private static final int POOL_SIZE = 5;

    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];

    private static final String BROADCAST_IP = "192.168.112.1";//112.110

    // 端口号
//    public static final int CLIENT_PORT = 0x0971;
    public static final int CLIENT_PORT = 4410;//4410

    private boolean isThreadRunning = false;

    private Context mContext;
    private DatagramSocket client;
    private DatagramPacket receivePacket;

    private long lastReceiveTime = 0;
    private static final long TIME_OUT = 120 * 1000;
    private static final long HEARTBEAT_MESSAGE_DURATION = 10 * 1000;

    private ExecutorService mThreadPool;
    private Thread clientThread;
    private HeartbeatTimer timer;
    private Users localUser;
    private Users remoteUser;

    private static UDPSocket instance;

    // 静态内部类
    private static class UDPSocketHolder {
        private static UDPSocket instance;
    }
    public static UDPSocket getInstance(Context context) {
        if(UDPSocketHolder.instance == null){
            UDPSocketHolder.instance = new UDPSocket(context);
        }
        return UDPSocketHolder.instance;
    }


    private UDPSocket(Context context) {

        mContext = context;

        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();

//        createUser();
    }

    /**
     * 创建本地用户信息
     */
    private void createUser() {
        if (localUser == null) {
            localUser = new Users();
        }
        if (remoteUser == null) {
            remoteUser = new Users();
        }

        localUser.setImei(DeviceUtil.getDeviceId());
        localUser.setSoftVersion(DeviceUtil.getPackageVersionCode(mContext));

        if (WifiUtil.getInstance(mContext).isWifiApEnabled()) {// 判断当前是否是开启热点方
            localUser.setIp("192.168.43.1");
        } else {// 当前是开启 wifi 方
            localUser.setIp(WifiUtil.getInstance(mContext).getLocalIPAddress());
            remoteUser.setIp(WifiUtil.getInstance(mContext).getServerIPAddress());
        }
    }


    public void startUDPSocket() {
        if (client != null) return;
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            client = new DatagramSocket(CLIENT_PORT);

            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }

            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启发送数据的线程
     */
    private void startSocketThread() {
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "clientThread is running...");
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();

//        startHeartbeatTimer();
    }


    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (client != null) {
                    client.receive(receivePacket);
                }
                lastReceiveTime = System.currentTimeMillis();
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                e.printStackTrace();
                return;
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            byte[] data = receivePacket.getData();
//            byte[] decode = Base64.decode(data, Base64.DEFAULT);


            try {
                byte[] bytes = Arrays.copyOf(data, receivePacket.getLength());
                Icdc2HmiOuterClass.Icdc2Hmi parseFrom = Icdc2HmiOuterClass.Icdc2Hmi.parseFrom(bytes);
//                Icdc2HmiOuterClass.Icdc2Hmi icdc2Hmi = SerializeUtils.deSerialize(bytes, Icdc2HmiOuterClass.Icdc2Hmi.class);
                EventBus.getDefault().post(new BaseDataEvent(parseFrom));
//                String strReceive = new String(data, 0, receivePacket.getLength());
//                Log.d(TAG, strReceive + " from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
//                writeTxtToFile(Base64.encodeToString(bytes,Base64.DEFAULT));

            }catch (Exception e){
                String message = e.getMessage();
            }

            //解析接收到的 json 信息

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    public void writeTxtToFile(String json) {
        String tempJson = json + "\r\n";
        File file = new File(Environment.getExternalStorageDirectory(),
                "/smartcarData_dt.txt");
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(tempJson.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (client != null) {
            client.close();
            client = null;
        }
        if (timer != null) {
            timer.exit();
        }
    }

    /**
     * 启动心跳，timer 间隔十秒
     */
    private void startHeartbeatTimer() {
        timer = new HeartbeatTimer();
        timer.setOnScheduleListener(new HeartbeatTimer.OnScheduleListener() {
            @Override
            public void onSchedule() {
                Log.d(TAG, "timer is onSchedule...");
                long duration = System.currentTimeMillis() - lastReceiveTime;
                Log.d(TAG, "duration:" + duration);
                if (duration > TIME_OUT) {//若超过两分钟都没收到我的心跳包，则认为对方不在线。
                    Log.d(TAG, "超时，对方已经下线");
                    // 刷新时间，重新进入下一个心跳周期
                    lastReceiveTime = System.currentTimeMillis();
                } else if (duration > HEARTBEAT_MESSAGE_DURATION) {//若超过十秒他没收到我的心跳包，则重新发一个。
                    String string = "hello,this is a heartbeat message";
                    sendMessage(string);
                }
            }

        });
        timer.startTimer(0, 1000 * 10);
    }

    /**
     * 发送心跳包
     *
     * @param message
     */
    public void sendMessage(final String message) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress targetAddress = InetAddress.getByName(BROADCAST_IP);

                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress, CLIENT_PORT);

                    client.send(packet);

                    // 数据发送事件
                    Log.d(TAG, "数据发送成功");

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}