package com.payn.codeinterviewguide.Chapter02_List;

import java.util.Stack;

/**
 * @author zhuangpf
 * @date 2022-12-21
 */
public class Q2007 {
	
	/*
	题目：判断一个链表是否为回文结构
	给定一个链表的头节点head,请判断该链表是否为回文结构。
	1->2->1, true;
	1->2->2->1, true;
	1->2->3, false;
	
	——————————————
	方法一：
	利用栈的结构。
	
	方法二：
	就是将整个链表的右半部分，压入完成后，再检查栈顶到栈底值出现的顺序是否和左半部分的值相对应。
	
	**/

	public static class Node {
		public int value;
		public Node next;

		public Node(int data) {
			this.value = data;
		}
	}

	public static boolean isPalindrome1(Node head) {
		// 利用栈的结构。
		Stack<Node> stack = new Stack<>();
		Node cur = head;
		while (null != cur) {
			stack.push(cur);
			cur = cur.next;
		}
		while (null != head) {
			if (head.value != stack.pop().value) {
				return false;
			}
			head = head.next;
		}
		return true;
	}

	public static boolean isPalindrome2(Node head) {
		return true;
	}

}
