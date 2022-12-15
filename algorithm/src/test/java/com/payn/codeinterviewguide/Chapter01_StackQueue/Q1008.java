package com.payn.codeinterviewguide.Chapter01_StackQueue;

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

	public static int maxRecSize(int[][] map) {
		if (map == null || map.length == 0 || map[0].length == 0) {
			return 0;
		}
		int maxArea = 0;
		int[] height = new int[map[0].length];
		for (int i = 0; i < map.length; i++) {


		}
		return 0;
	}

}
