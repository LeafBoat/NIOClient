package com.example.administrator.nioclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientHandler implements Runnable {

    private InetSocketAddress address;
    private Selector selector;
    SocketChannel sc = null;
    private ConnectListener connectListener;
    private static ReadListener readListener;

    public ClientHandler(String hostname, int port) {
        address = new InetSocketAddress(hostname, port);
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public static void setReadListener(ReadListener readListener) {
        ClientHandler.readListener = readListener;
    }

    @Override
    public void run() {
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(false);
            selector = Selector.open();
            sc.connect(address);
            sc.register(selector, SelectionKey.OP_CONNECT);
            synchronized (this) {
                while (!sc.finishConnect()) {
                    Thread.sleep(500);
                }
            }
            connect(sc);
            while (true) {
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isWritable()) {
                        System.out.println("客户端可写");
                    }
                    if (key.isReadable()) {
                        read(key);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (sc != null && sc.isOpen()) {
                try {
                    sc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        // 创建ByteBuffer，并开辟一个1M的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 读取请求码流，返回读取到的字节数
        int readBytes = sc.read(buffer);
        // 读取到字节，对字节进行编解码
        if (readBytes > 0) {
            // 将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
            buffer.flip();
            // 根据缓冲区可读字节数创建字节数组
            byte[] bytes = new byte[buffer.remaining()];
            // 将缓冲区可读字节数组复制到新建的数组中
            buffer.get(bytes);
            String result = new String(bytes, "UTF-8");
            System.out.println("客户端收到消息：" + result);
            if (readListener != null) {
                readListener.onReadListener(result);
            }
        }
    }

    private void connect(SocketChannel sc) throws IOException {
        if (sc.isConnected()) {
            MessageParser.save(sc);
            if (connectListener != null)
                connectListener.onConnectSuccesss();
            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        }
    }

    // 异步发送消息
    private void doWrite(SocketChannel sc, String message) throws IOException {
        MessageParser.sendMessage(sc, message);
    }

    public void sendMsg(String msg) throws Exception {
        if (sc.isConnected()) {
            sc.register(selector, SelectionKey.OP_READ);
            doWrite(sc, msg);
        } else {
            System.out.println("还未连接成功");
        }
    }

    public interface ConnectListener {
        void onConnectSuccesss();

        void onConnectFailed();
    }

    public interface ReadListener {
        void onReadListener(String msg);
    }
}
