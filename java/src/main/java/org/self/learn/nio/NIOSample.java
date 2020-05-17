package org.self.learn.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Non-Blocking IO
 * <p>
 * Java1.4开始提供非阻塞IO Api
 * <p>
 * NIO的三大组件:
 * 1, Buffer
 * 2, Channel
 * 3, Selector
 * <p>
 * 先开启selector选择器，将新建的连接通过channel注册到selector，
 * 然后通过selector轮询去获取那些准备好的channel, 在channel上进行读写
 * <p>
 * （不用创建多个线程去处理多个客户端的连接，如果设置阻塞模式，accept接收连接将不再被阻塞）
 * <p>
 * Reactor 模式: 事件驱动
 * 通过一个或或多个数据源（或者连接）进入Reactor后，分解分发到不同的应用程序进行处理
 * <p>
 * 核心原理: 底层是采用操作系统的IO多路复用实现的
 *
 * 每个选择器，通过select/epoll去拉取（注册的socket文件描述符的IO）准备进行IO操作的socket
 * 然后进行相应的操作，这个过程也是同步，是因为拉取到的socket只表明了它准备好了，可以被（用户进程）接受处理
 * 具体后续操作（比如读写文件）还是需要用户进程处理。
 *
 * Linux操作系统的IO模型
 * BIO , NIO (非Java nio) , IO多路复用 , 异步IO , 信号驱动IO???
 *
 * 设备 -> 内核缓冲 -> 用户进程空间
 *
 * @date 2020-05-17.
 */
public class NIOSample {

    public static void main(String[] args) throws IOException {
        // 1. serverSelector负责轮询是否有新的连接，服务端监测到新的连接之后，不再创建一个新的线程，
        // 而是直接将新连接分发并绑定到clientSelector上
        Selector serverSelector = Selector.open();

        // 2. clientSelector负责轮询连接是否有数据可读
        Selector clientSelector = Selector.open();

        new Thread(() -> {
            try {
                // 服务端启动
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                listenerChannel.socket().bind(new InetSocketAddress(3333));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
                while (true) {
                    // 监测是否有新的连接，这里的1指的是阻塞的时间为 1ms
                    if (serverSelector.select(1) > 0) {
                        System.out.println("acquire new connect");
                        Set<SelectionKey> set = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();

                            if (key.isAcceptable()) {
                                try {
                                    // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    // (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为 1ms
                    if (clientSelector.select(1) > 0) {
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isReadable()) {
                                try {
                                    SocketChannel clientChannel = (SocketChannel) key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    // (3) 面向 Buffer, 这个操作不会被阻塞
                                    clientChannel.read(byteBuffer);
                                    // 读写模式切换
                                    byteBuffer.flip();
                                    System.out.println(
                                            Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
                                } finally {
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    static class Client {
        public static void main(String[] args) throws InterruptedException {
            //模拟多个客户端往服务端建立连接并发送消息
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                new Thread(() -> {
                    try (Socket socket = new Socket("127.0.0.1", 3333)) {
                        while (true) {
                            try {
                                System.out.println(new Date() + ": start send msg" + finalI);
                                socket.getOutputStream().write((new Date() + ": hello world" + finalI).getBytes());
                                Thread.sleep(10000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
            Thread.sleep(100000);
        }
    }
}
