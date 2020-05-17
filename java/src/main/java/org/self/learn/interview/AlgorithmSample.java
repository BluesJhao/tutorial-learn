package org.self.learn.interview;

import java.util.Arrays;

/**
 * comment
 *
 * @date 2019-10-30.
 */
public class AlgorithmSample {
    private static int mod;

    public static void main(String[] args) {
        //                int[] array = new int[]{1, 5, 8, 2, 9, 3, 0, 4, 6, 4, 7};
        int[] array = new int[]{72, 6, 57, 88, 60, 42, 83, 73, 48};
        //        int[] array = new int[]{48, 6, 57, 88, 60, 42, 83, 73, 48, 85};
        //        int[] array = new int[]{48, 6, 57, 42, 60, 72, 83, 73, 88, 85};
        //                int[] array = new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        //        int[] array = new int[]{0, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        //        int[] array = new int[]{0, 9, 8, 7, 6, 5, 4, 3, 2, 1, 10};
        //        upSort(array);
        //        selectSort(array);
        quickSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));
        System.out.println(binarySearch(array, 42));
        int[] array1 = new int[]{1, 5, -8, 2, 9, 3, 0, -4, 6, 4, 7};
        //        System.out.println(findStep(3));
        System.out.println(step(10));
                System.out.println(maxSubArray(array1));
    }

    /**
     * 选择排序
     * 每轮选出一个最小值
     * 时间复杂度：O(N2)
     */
    private static void selectSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j]) {
                    mod++;
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        System.out.println(mod);
    }


    /**
     * 冒泡排序
     * 每一轮冒出一个最大值
     * 时间复杂度：O(N2)
     */
    private static void upSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    mod++;
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        System.out.println(mod);
    }

    /**
     * 快速排序
     * 对冒泡排序的改进
     * 挖坑填数 + 分治
     * 时间复杂度：O(n*logn) 空间复杂度O(n)
     * <p>
     * 为了防止[9,8,7,6,5,4,3,2,1]这种倒序出现单边每次排一个的情况
     * 先对数组进行数组的首-尾比较，首-中，中-后比较并交换
     */
    private static void quickSort(int[] array, int le, int ri) {
        if (array == null || array.length < 2) {
            return;
        }
        if (le < ri) {
            int posIndex = getIndexAndSort(array, le, ri);
            quickSort(array, le, posIndex - 1);
            quickSort(array, posIndex + 1, ri);
        }
    }

    /**
     * 获取参考值索引位置
     */
    private static int getIndexAndSort(int[] array, int left, int right) {
        int temp = array[left];
        //随机值，将中间的值与第一个值互换位置
        while (true) {
            while (left < right && array[right] > temp)
                right--;

            if (left < right) {
                array[left++] = array[right];
            } else {
                break;
            }

            while (left < right && array[left] < temp)
                left++;

            if (left < right) {
                array[right--] = array[left];
            } else {
                break;
            }
        }
        array[left] = temp;
        return left;
    }

    static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (arr[mid] > target) {
                right = mid - 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    public static int findStep(int n) {
        if (n == 0 || n == 1 || n == 2) {
            return n;
        }

        return findStep(n - 1) + findStep(n - 2);
    }

    private static int step(int n) {
        if (n == 1)
            return 1;
        if (n == 2)
            return 2;
        else
            return step(n - 1) + step(n - 2);
    }

    /**
     * 有序子数组的和的最大值
     * 时间复杂度O(n)
     */
    private static int maxSubArray(int[] arr) {
        int maxSum = 0;
        for (int i = 0; i < arr.length; i++) {
            int current = 0;
            for (int j = i; j < arr.length; j++) {
                current += arr[j];
                if (current > maxSum) {
                    maxSum = current;
                }
            }
        }
        return maxSum;
    }
}
