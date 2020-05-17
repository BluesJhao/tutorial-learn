package org.self.learn.interview;

import java.util.ArrayList;

public class Main {
    static Main arrayList1 = new Main();
    static ArrayList<Object> arrayList = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
//        for (int i = 1; i < 100; i++) {
//            Object temp =
//                    "123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123";
//            arrayList.add(temp);
//            if (i % 10000 == 0)
//                Thread.sleep(1);
//        }

        String s0 = "ab";
        String s1 = "b";
        String s2 = "a" + s1;
        String s3 = "a" + "b";
        String s4 = new String("ab");

        System.out.println("===========test9============");
        System.out.println((s0 == s2)); //result = false
        System.out.println((s0 == s3)); //result = true
        System.out.println((s0 == s4)); //result = false

        Main main = new Main();
        String s5 = "aaa";
        s5 = main.change(s5);
        System.out.println(s5);

    }

    String change(String temp){
        String replace = temp.replace("aa", "b");
        return replace;
    }
}
