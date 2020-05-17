package org.self.learn.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * 面向通道与缓冲区读写
 * <p>
 * BIO,NIO,AIO更多的是针对网络Socket IO, 存在阻塞
 * <p>
 * 对于文件读取，不存在阻塞，读取不管是不是有结果都是立即返回
 *
 * @date 2020-05-18.
 */
public class FileNIOSample {
    public static void main(String[] args) throws IOException {
        try (FileChannel fileChannel = FileChannel.open(Paths.get("java/src/main/resources/test.properties"),
                StandardOpenOption.APPEND)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put("\nappend=value".getBytes());
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
        }

        try (FileChannel fileChannel = FileChannel.open(Paths.get("java/src/main/resources/test.properties"),
                StandardOpenOption.READ)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
        }
    }


    /**
     * 使用FileChannel复制更快
     */
    static class FileCopy {

        public static void main(String[] args) throws IOException {
            //使用nio channel复制
            long start = System.currentTimeMillis();
            Path path0 = Paths.get("/Users/jiahao/Downloads/mysql-5.7.26-macos10.14-x86_64_copy.dmg");
            boolean exists = Files.exists(path0);
            if (!exists) {
                path0 = Files.createFile(path0);
            }
            File source = new File("/Users/jiahao/Downloads/mysql-5.7.26-macos10.14-x86_64.dmg");
            fileCopy(source, path0.toFile());
            long ending = System.currentTimeMillis();
            System.out.println(ending - start);

            //使用jdk1.7 工具Files.copy复制
            start = System.currentTimeMillis();
            path0 = Paths.get("/Users/jiahao/Downloads/mysql-5.7.26-macos10.14-x86_64_copy2.dmg");
            Files.copy(source.toPath(), path0, StandardCopyOption.REPLACE_EXISTING);
            ending = System.currentTimeMillis();
            System.out.println(ending - start);

            //使用输入输出流复制
            start = System.currentTimeMillis();
            path0 = Paths.get("/Users/jiahao/Downloads/mysql-5.7.26-macos10.14-x86_64_copy1.dmg");
            exists = Files.exists(path0);
            if (!exists) {
                path0 = Files.createFile(path0);
            }
            fileStreamCopy(new File("/Users/jiahao/Downloads/mysql-5.7.26-macos10.14-x86_64.dmg"), path0.toFile());
            ending = System.currentTimeMillis();
            System.out.println(ending - start);
        }

        //通过通道复制文件
        private static void fileCopy(File source, File target) {
            try (FileChannel sourceChannel = FileChannel.open(source.toPath(), StandardOpenOption.READ);
                 FileChannel targetChannel = FileChannel.open(target.toPath(), StandardOpenOption.WRITE)) {
                sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //常规文件复制
        private static void fileStreamCopy(File source, File target) {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(source));
                 OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(target))) {
                byte[] bytes = new byte[1024];
                int read;
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
