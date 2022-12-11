package com.payn.leetcode;

/**
 * @author zhuangpf
 * @date 2022-10-23
 */
public class Question02 {
	/*
	题目：
	给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。
	请你将两个数相加，并以相同形式返回一个表示和的链表。
	你可以假设除了数字 0 之外，这两个数都不会以 0 开头。

	Definition for singly-linked list.
	public class ListNode {
		int val;
		ListNode next;
		ListNode() {}
		ListNode(int val) { this.val = val; }
		ListNode(int val, ListNode next) { this.val = val; this.next = next; }
	}
	*/
	public class ListNode {
		int val;
		ListNode next;

		ListNode(int x) {
			val = x;
		}
	}

	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		// 结果的第一个节点
		ListNode pre = new ListNode(0);
		// 当前结点
		ListNode cur = pre;
		// 进位
		int carry = 0;
		while (null != l1 || null != l2) {
			int x = null == l1 ? 0 : l1.val;
			int y = null == l2 ? 0 : l2.val;
			int sum = x + y + carry;
			// 进位
			carry = sum / 10;
			// 取余，实际结果
			sum = sum % 10;
			// 设置下一节点
			cur.next = new ListNode(sum);
			// 当前节点指针移动
			cur = cur.next;
			// l1、l2指针下移
			if (null != l1) {
				l1 = l1.next;
			}
			if (null != l2) {
				l2 = l2.next;
			}
		}
		if (1 == carry) {
			cur.next = new ListNode(carry);
		}
		return pre.next;
	}

}
