package com.payn.codeinterviewguide.Chapter01_StackQueue;

import java.util.Stack;

/**
 * @author zhuangpf
 * @date 2022-12-14
 */
public class Q1008 {
	
	/*
	题目：
	给定一个整数矩阵map,其中的值只有0和1两种，求其中全是1的所有矩形区域中，最大的矩形区域为1的数量。
	例如：
	1 1 1 0
	其中，最大的矩形区域有3个1，所以返回3。
	再如：
	1 0 1 1
	1 1 1 1
	1 1 1 0
	其中，最大的矩形区域有6个1，所以返回6。
	**/

	/*
	分析：
	map = 1 0 1 1
		  1 1 1 1
		  1 1 1 0
	
	1、矩阵的函数为N，以每一行做切割，统计当前行作为底的情况下，每个位置往上的1的数量。使用高度数组height表示。
	
	第1行左切割：
	1 0 1 1		height={1 0 1 1}
	第2行左切割：
	1 1 1 1		height={2 1 2 2}
	第3行左切割：
	1 1 1 0		height={3 2 3 0}
	
	2、对于每次切割，都利用更新后的height数组来求出以每一行为底的情况，最大的矩形是什么。那么在这么多次的切割中，最大的举行就是我们要的。

	**/
	public static int maxRecSize(int[][] map) {
		if (map == null || map.length == 0 || map[0].length == 0) {
			return 0;
		}
		int maxArea = 0;
		int[] height = new int[map[0].length];
		for (int i = 0; i < map.length; i++) {
			// 构造height
			for (int j = 0; j < map[0].length; j++) {
				height[j] = map[i][j] == 0 ? 0 : height[j] + 1;
			}
			maxArea = Math.max(maxRecFromBottom(height), maxArea);
		}
		return maxArea;
	}

	public static int maxRecFromBottom(int[] height) {
		if (null == height || height.length == 0) {
			return 0;
		}
		int maxArea = 0;// 结果
		Stack<Integer> stack = new Stack<>();
		for (int i = 0; i < height.length; i++) {
			// 当前的值，小于等于栈顶位置的值
			while (!stack.isEmpty() && height[i] <= height[stack.peek()]) {
				int j = stack.pop();// 栈顶弹出，记作j
				int k = stack.isEmpty() ? -1 : stack.peek();// 当前栈顶的值，记作k
				int curArea = (i - k - 1) * height[j];
				maxArea = Math.max(maxArea, curArea);
				System.out.print("i::" + i + ",j:" + j + ",k:" + k + ",curArea" + curArea + "  ");
			}
			// 当前遍历，大于栈顶的值才压入栈
			stack.push(i);// 将位置压入栈
			System.out.print("  " + stack.toString());
			System.out.println();
		}
		while (!stack.isEmpty()) {
			int j = stack.pop();
			int k = stack.isEmpty() ? -1 : stack.peek();
			int curArea = (height.length - k - 1) * height[j];
			maxArea = Math.max(maxArea, curArea);
			System.out.print("j:" + j + ",k:" + k + ",curArea" + curArea + "  ");
		}
		return maxArea;
	}

	public static void main(String[] args) {
		int[] height = {3, 4, 5, 4, 3, 6};
		maxRecFromBottom(height);
		/*
		[0]
		[0, 1]
		[0, 1, 2]
		i::3,j:2,k:1,curArea:5  i::3,j:1,k:0,curArea:8    [0, 3]
		i::4,j:3,k:0,curArea:12  i::4,j:0,k:-1,curArea:12    [4]
		[4, 5]
		j:5,k:4,curArea:6  j:4,k:-1,curArea:18   
		**/
		int[][] map = {{1, 0, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 0},};
		System.out.println(maxRecSize(map));
	}

}
