package com.payn.codeinterviewguide.Chapter01_StackQueue;

import java.util.LinkedList;

/**
 * @author zhuangpf
 * @date 2022-12-14
 */
public class Q1009 {

	/*
	题目： 
	给定数组arr和整数num,共返回有多少个子数组满足如下情况：
	max(arr[i..j])-min(arr[i..j])<=num
	max(arr[i..j])表示子数组arr[i..j]中的最大值，min(arr[i..j])表示子数组arr[i..j]中的最小值。
	
	如果数组长度为N,请实现时间复杂度为O(N)的解法。
	**/
	
	/*
	分析：
	普通解法：
	找到arr的所有的子数组，一共有O(N^2)个，然后对每个子数组做遍历，找到其中的最大值和最小最，
	这个过程的时间复杂度O(N)，统计所有满足的子数组即可。

	**/

	public static int getNum(int[] arr, int num) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		LinkedList<Integer> qmin = new LinkedList<Integer>();
		LinkedList<Integer> qmax = new LinkedList<Integer>();
		int i = 0;
		int j = 0;
		int res = 0;
		while (i < arr.length) {
			while (j < arr.length) {
				while (!qmin.isEmpty() && arr[qmin.peekLast()] >= arr[j]) {
					qmin.pollLast();
				}
				qmin.addLast(j);
				while (!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[j]) {
					qmax.pollLast();
				}
				qmax.addLast(j);
				if (arr[qmax.getFirst()] - arr[qmin.getFirst()] > num) {
					break;
				}
				j++;
			}
			if (qmin.peekFirst() == i) {
				qmin.pollFirst();
			}
			if (qmax.peekFirst() == i) {
				qmax.pollFirst();
			}
			res += j - i;
			i++;
		}
		return res;
	}

	// for test
	public static int[] getRandomArray(int len) {
		if (len < 0) {
			return null;
		}
		int[] arr = new int[len];
		for (int i = 0; i < len; i++) {
			arr[i] = (int) (Math.random() * 10);
		}
		return arr;
	}

	// for test
	public static void printArray(int[] arr) {
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				System.out.print(arr[i] + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		int[] arr = getRandomArray(8);
		int num = 5;
		printArray(arr);
		System.out.println(getNum(arr, num));
	}

}
