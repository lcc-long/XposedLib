package z.houbin.xposed.lib.socket;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import z.houbin.xposed.lib.log.Logs;

public class SocketClient extends Thread {
    private Socket socket;
    private static final String TAG = "SocketClient";
    private String address;
    private int port;
    private SendThread sendThread;

    public SocketClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();
        try {
            Logs.e(TAG, "run");
            socket = new Socket(address, port);
            sendThread = new SendThread(socket);
            sendThread.start();

            ReceiveThread receiveThread = new ReceiveThread(socket, getSocketListener());
            receiveThread.start();
            Logs.e(TAG, "success");
        } catch (Exception e) {
            Logs.e(TAG, e.getMessage());
            stopTcp(true);
        }
    }

    //发送消息
    public void sendMessage(final String msg) {
        if (sendThread != null) {
            sendThread.send(msg);
        }
    }

    private SocketListener socketListener;

    private SocketListener getSocketListener() {
        return socketListener;
    }

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    //消息接收
    private class ReceiveThread extends Thread {
        private Socket socket;
        private SocketListener listener;

        ReceiveThread(Socket socket, SocketListener listener) {
            this.socket = socket;
            this.listener = listener;
        }

        @Override
        public void run() {
            super.run();
            while (!isStop) {
                try {
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        Logs.e(data);
                        if (listener != null) {
                            listener.onMessage(data);
                        }
                    }
                } catch (Exception e) {
                    Logs.e(e);
                }
            }
        }
    }

    private class SendThread extends Thread {
        private Socket socket;
        private String data;

        SendThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            while (!isStop) {
                if (data == null) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        Logs.e(e);
                    }
                } else if (socket.isConnected()) {
                    try {
                        socket.getOutputStream().write(data.getBytes());
                        socket.getOutputStream().flush();
                        data = null;
                    } catch (IOException e) {
                        Logs.e(e);
                    }
                }
            }
        }

        void send(String msg) {
            this.data = msg;
        }
    }

    private boolean isStop;

    private void stopTcp(boolean stop) {
        isStop = stop;
        if (isStop) {
            try {
                socket.close();
            } catch (IOException e) {
                Logs.e(TAG, e.getMessage());
            }
            socket = null;
        }
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isConnect() {
        boolean connect = true;
        if (isStop()) {
            connect = false;
        }

        if (socket.isConnected()) {
            connect = true;
        }
        return connect;
    }
}
