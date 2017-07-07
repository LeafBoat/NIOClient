package com.example.administrator.nioclient.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientHandler {

    private InetSocketAddress address;
    private Selector selector;
    SocketChannel sc = null;
    private ConnectionCallBack connectListener;
    private static ReadListener readListener;

    public ClientHandler(String hostname, int port) {
        address = new InetSocketAddress(hostname, port);
    }

    public void setReadListener(ReadListener readListener) {
        ClientHandler.readListener = readListener;
    }

    /**
     * 登录服务器，将用户账号信息传递给服务器
     *
     * @param header
     * @param ccb
     */
    void login(final Header header, ConnectionCallBack ccb) {
        this.connectListener = ccb;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sc = SocketChannel.open();
                    sc.configureBlocking(false);
                    selector = Selector.open();
                    sc.register(selector, SelectionKey.OP_CONNECT);
                    sc.connect(address);
                    synchronized (this) {
                        while (!sc.finishConnect()) {
                            Thread.sleep(500);
                        }
                    }
                    sc.write(ByteBuffer.wrap(header.toString().getBytes()));
                    if (connectListener != null)
                        connectListener.onConnect();
                    sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    while (true) {
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectedKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            if (key.isReadable()) {
                                read(key);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    void sendMsg(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sc.isConnected()) {
                    try {
                        sc.register(selector, SelectionKey.OP_READ);
                        sc.write(ByteBuffer.wrap(msg.getBytes()));
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("还未连接成功");
                }
            }
        }).start();
    }

    public interface ReadListener {
        void onReadListener(String msg);
    }
}
