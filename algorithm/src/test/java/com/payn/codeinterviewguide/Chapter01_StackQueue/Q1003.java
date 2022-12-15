package com.payn.codeinterviewguide.Chapter01_StackQueue;

import java.util.Stack;

/**
 * @author zhuangpf
 * @date 2022-12-11
 */
public class Q1003 {

	/*
	题目：
	一个栈依次压人1,2,3,4,5，那么从栈顶到栈底分别为5,4,3,2,1。将这个站转置后，从栈顶到栈底为1,2,3,4,5，也就是实现栈中元素的逆序，但是只能用递归函数来实现，不能用其他数据结构。
	**/
	public static int getAndRemoveLstElement(Stack<Integer> stack) {
		Integer result = stack.pop();
		if (stack.isEmpty()) {
			return result;
		} else {
			int last = getAndRemoveLstElement(stack);
			stack.push(result);
			return last;
		}
	}

	public static void reverse(Stack<Integer> stack) {
		if (stack.isEmpty()) {
			return;
		}
		int i = getAndRemoveLstElement(stack);
		reverse(stack);
		stack.push(i);
	}

}
