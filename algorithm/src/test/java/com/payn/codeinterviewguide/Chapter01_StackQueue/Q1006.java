package com.payn.codeinterviewguide.Chapter01_StackQueue;

import java.util.LinkedList;

/**
 * @author zhuangpf
 * @date 2022-12-12
 */
public class Q1006 {

	/*
	题目：
	有一个整数数组arr和一个大小为w的窗口从数组的最左边滑到最右边，窗口每次向右边滑到一个位置。
	例如，数组为[4,3,5,4,3,3,6,7],窗口大小为3时：
	[4 3 5] 4 3 3 6 7     窗口中最大值为5
	4 [3 5 4] 3 3 6 7     窗口中最大值为5
	4 3 [5 4 3] 3 6 7     窗口中最大值为5
	4 3 5 [4 3 3] 6 7     窗口中最大值为4
	4 3 5 4 [3 3 6] 7     窗口中最大值为6
	4 3 5 4 3 [3 6 7]     窗口中最大值为7
	
	如果数组长度为n,窗口大小为w,则一共产生n-w+1个窗口的最大值。
	请实现一个函数。
	- 输入：整数数组arr,窗口大小为w。
	- 输出：一个长度为n-w+1的数组res,res[i]表示每一种窗口状态下的最大值。
	**/
	public static int[] getMaxWindow(int[] arr, int w) {
		if (null == arr || w < 1 || arr.length < w) {
			return null;
		}
		// 存储在窗口范围内的最大值的索引值
		LinkedList<Integer> qmax = new LinkedList<Integer>();
		// 存储结果的数组
		int[] res = new int[arr.length - w + 1];
		int index = 0;
		for (int i = 0; i < arr.length; i++) {
			while (!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[i]) {
				qmax.pollLast();
			}
			qmax.addLast(i);
			if (qmax.peekFirst() == i - w) {
				qmax.pollFirst();
			}
			if (i >= w - 1) {
				res[index++] = arr[qmax.peekFirst()];
			}
		}
		return res;
	}

	// for test
	public static void printArray(int[] arr) {
		for (int i = 0; i != arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}

	public static void main(String[] args) {
		int[] arr = {4, 3, 5, 4, 3, 3, 6, 7};
		int w = 3;
		printArray(getMaxWindow(arr, w));
	}

}
