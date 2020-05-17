package org.self.learn.design.pattern.structure;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 适配器模式：
 * <p>
 * 在不改变原始对象与目标对象前提下，将原始对象转化成目标对象的中间对象简称适配器对象，
 *
 * @date 2020-04-18.
 */
public class AdapterSample {

    public static void main(String[] args) throws IOException {
        USB2 usb2 = new USB2();
        TypeC typeC = new TypeCAdapter(usb2);
        Mac.charging(typeC);

        //JDK中的例子, 将字节流转化为字符流，inputStreamReader就是适配器
        InputStream input = Files.newInputStream(
                Paths.get("java/src/main/java/org/self/learn/design/pattern/document"));
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        char[] chats = new char[1024];
        while (reader.read(chats) > 0) {
            System.out.println(new String(chats));
        }
    }

    private static class USB2 {
        String connect() {
            return "usb battery charging...";
        }
    }

    private static abstract class TypeC {
        public String connect() {
            return "typec battery charging...";
        }
    }

    static class Mac {
        static void charging(TypeC typeC){
            System.out.println(typeC.connect());
        }
    }

    private static class TypeCAdapter extends TypeC {

        private USB2 usb2;

        TypeCAdapter(USB2 usb2) {
            this.usb2 = usb2;
        }

        @Override
        public String connect() {
            return usb2.connect();
        }
    }
}
