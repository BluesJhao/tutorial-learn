package org.self.learn.nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * 阻塞IO, JDK1.4之前提供的网络IO
 * <p>
 * Java提供的Linux底层的BIO模型的封装
 * <p>
 * 早期的CS服务多采用BIO+多线程（线程池）来解决网络通信
 *
 * @date 2020-05-17.
 */
public class BIOSample {

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
                                Thread.sleep(20000);
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

    static class Server {
        public static void main(String[] args) throws InterruptedException {
            try (ServerSocket serverSocket = new ServerSocket(3333)) {
                // 接收到客户端连接请求之后为每个客户端创建一个新的工作线程进行链路处理
                new Thread(() -> {
                    while (true) {
                        try {
                            System.out.println("wait accept a connect");

                            // 阻塞方法获取新的连接
                            // 如果获取到一个连接（客户端）之后，其他连接将会一直被阻塞，
                            // 直到这个连接结束，才能通过while (true)重新获取连接，如此以往
                            Socket clientSocket = serverSocket.accept();

                            // 每一个新的连接(客户端)都创建一个工作线程，负责读取数据
                            // 是为了不要阻塞获取连接的线程（inputStream.read()是阻塞的）
                            // 这些线程作为工作线程，各自去阻塞各自客户端连接，并进行业务处理
                            new Thread(() -> {
                                try {
                                    int len;
                                    byte[] data = new byte[1024];
                                    InputStream inputStream = clientSocket.getInputStream();
                                    // 按字节流方式读取数据，
                                    // ***注意: SocketInputStream的read()方法是阻塞的****
                                    while ((len = inputStream.read(data)) != -1) {
                                        System.out.println(
                                                Thread.currentThread().getName() + "::" + new String(data, 0, +len));
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
                //防止Socket流被关闭
                Thread.sleep(10000000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}