package com.payn.codeinterviewguide.Chapter01_StackQueue;

import java.util.Stack;

/**
 * @author zhuangpf
 * @date 2022-12-12
 */
public class Q1004 {

	/*
	题目：
	一个栈中元素的类型为整型，现在想将该栈从顶到底按从大到小的顺序排序，只许申请一个栈。除此之外，可以申请新的变量，但是不能申请额外的数据结构。如何完成排序。
	**/
	public static void sortStackByStack(Stack<Integer> stack) {
		Stack<Integer> help = new Stack<>();
		while (!stack.isEmpty()) {
			Integer cur = stack.pop();
			while (!help.isEmpty() && cur > help.peek()) {
				stack.push(help.pop());
			}
			help.push(cur);
		}
		while (!help.isEmpty()) {
			stack.push(help.pop());
		}
	}

}
