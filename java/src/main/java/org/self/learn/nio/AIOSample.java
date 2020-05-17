package org.self.learn.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Asynchronous IO : 异步IO Java1.7开始提供
 * <p>
 * 发起非阻塞方式的 I/O 操作(accept, read, write)。当 I/O 操作完成时通知
 * <p>
 * AIO 的 I/O 操作，有两种方式的 API 可以进行：Future 方式  和 CompleteHandler 方式。
 *
 * @date 2020-05-17.
 */
public class AIOSample {
    private AsynchronousServerSocketChannel server;
    private int                             port = 3333;

    private AIOSample() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind(
                new InetSocketAddress(port));
    }

    // 方式1: accept()获取Future<AsynchronousSocketChannel>, 配合get阻塞获取连接;
    public void startWithFuture() throws InterruptedException,
            ExecutionException, TimeoutException {
        while (true) {//
            Future<AsynchronousSocketChannel> future = server.accept();
            System.out.println("wait connect");
            AsynchronousSocketChannel socket = future.get();// get() 是为了确保 accept 到一个连接
            new Thread(() -> {
                try {
                    //获取连接以后，还是需要多线程执行
                    handleWithFuture(socket);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void handleWithFuture(AsynchronousSocketChannel channel) throws InterruptedException, ExecutionException {
        ByteBuffer readBuf = ByteBuffer.allocate(1024);

        while (true) {
            //get 是为了确保 read 完成，超时时间可以有效避免DOS攻击，如果客户端一直不发送数据，则进行超时处理
            Integer integer = channel.read(readBuf).get();
            if (integer == -1) {
                System.out.println("should be return");
                return;
            }
            readBuf.flip();
            System.out.println("received: " + Charset.forName("UTF-8").decode(readBuf));
            readBuf.clear();
        }
    }

    // 方式2: 即提交一个 I/O 操作请求，并且指定一个 CompletionHandler。
    // 当异步 I/O 操作完成时，便发送一个通知，此时这个 CompletionHandler 对象的 completed 或者 failed 方法将会被调用
    private void startWithCompletionHandler() {
        server.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Object>() {

                    public void completed(AsynchronousSocketChannel result, Object attachment) {
                        server.accept(null, this);// 再次接收客户端连接
                        handleWithCompletionHandler(result);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        System.out.println("internal accept error");
                        exc.printStackTrace();
                    }
                });
    }

    private void handleWithCompletionHandler(final AsynchronousSocketChannel channel) {
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(1024);
            final long timeout = 10L;
            channel.read(buffer, timeout, TimeUnit.SECONDS, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    if (result == -1) {
                        System.out.println("should be close");
                        try {
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    buffer.flip();
                    System.out.println("received message:" + Charset.forName("UTF-8").decode(buffer));
                    buffer.clear();
                    channel.read(buffer, timeout, TimeUnit.SECONDS, null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("internal read error");
                    exc.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        //方式1: accept后通过feature.get()主动获取请求，与BIO + 多线程实现将没有差别
        new AIOSample().startWithFuture();
        //方式2: accept获取请求后会通知CompletionHandler
        //        new AIOSample().startWithCompletionHandler();
        Thread.sleep(1000000);
    }

    static class Client {

        public static void main(String... args) throws Exception {
            //模拟多个客户端往服务端建立连接并发送消息
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                new Thread(() -> {
                    try (Socket socket = new Socket("127.0.0.1", 3333)) {
                        while (true) {
                            try {
                                System.out.println(new Date() + ": start send msg" + finalI);
                                socket.getOutputStream().write((new Date() + ": hello world" + finalI).getBytes());
                                Thread.sleep(8000);
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
