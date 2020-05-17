package org.self.learn.interview;

/**
 * comment
 *
 * @date 2019-11-04.
 */
public class Interview {
    public static void main(String[] args) {
        System.out.println(Integer.valueOf(512) == Integer.valueOf(512));
        System.out.println(Integer.valueOf("127") == Integer.valueOf("127"));
        System.out.println(new Integer("512") == Integer.parseInt("512"));
        System.out.println(Integer.valueOf("512") == Integer.parseInt("512"));


        System.out.println(increase());

        System.out.println(cycleEqual("abc", "abcabcabcab"));

        System.out.println("===========");

        System.out.println(5 & ((1 << 29) - 1));
        System.out.println(5 & -(1 << 29));
        System.out.println((-1 << 29));

        System.out.println(-536870910 & ((1 << 29) - 1));

        System.out.println(-536870907 & ((1 << 29) - 1));

        System.out.println(((1 << 29) - 1));

        System.out.println((-536870912) & ((1 << 29) - 1));


    }

    private static boolean cycleEqual(String abc, String bcd) {
        char[] chars = abc.toCharArray();
        char[] chars1 = bcd.toCharArray();
        if (chars1.length == 0 || chars.length == 0) {
            return false;
        }
        int startIndex = -1;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == chars1[0]) {
                startIndex = i;
                break;
            }
        }
        if (startIndex == -1) {
            return false;
        }

        for (int i = 0; i < chars1.length; i++) {
            if (chars1[i] != chars[(i + startIndex) % chars.length]) {
                return false;
            }
        }
        return true;
    }

    private static int increase() {
        int temp = 1;

        try {
            System.out.println(temp);
            return temp++;
        } finally {
            System.out.println(temp);
            return temp++;
        }
    }
}
