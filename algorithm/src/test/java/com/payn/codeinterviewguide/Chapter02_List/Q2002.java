package com.payn.codeinterviewguide.Chapter02_List;

/**
 * @author zhuangpf
 * @date 2022-12-18
 */
public class Q2002 {

	/*
	题目：
	分别实现两个函数，一个可以删除单链表中倒数第k个结点，另一个可以删除双链表中倒数第K个函数

	要求：
	如果链表长度为N,时间复杂度达到O(N),额外空间复杂度达到O(1)。
	**/

	/*
	分析：
	1 -> 2 -> 3
	
	**/

	public static class Node {
		public int value;
		public Node next;

		public Node(int data) {
			this.value = data;
		}
	}

	// 删除单链表中倒数第k个结点
	public static Node removeLastKthNode(Node head, int lastKth) {
		if (null == head || lastKth < 1) {
			return head;
		}
		Node cur = head;
		while (cur != null) {
			lastKth--;
			cur = cur.next;
		}
		if (lastKth == 0) {
			head = head.next;
		}
		if (lastKth < 0) {
			cur = head;
			while (++lastKth != 0) {
				cur = cur.next;
			}
			cur.next = cur.next.next;
		}
		return head;
	}

	public static class DoubleNode {
		public int value;
		public DoubleNode last;
		public DoubleNode next;

		public DoubleNode(int data) {
			this.value = data;
		}
	}

	public static DoubleNode removeLastKthNode(DoubleNode head, int lastKth) {
		if (null == head || lastKth < 1) {
			return head;
		}
		DoubleNode cur = head;
		while (null != cur) {
			lastKth--;
			cur = cur.next;
		}
		if (lastKth == 0) {
			head = head.next;
			head.last = null;
		}
		if (lastKth < 0) {
			cur = head;
			while (++lastKth != 0) {
				cur = cur.next;
			}
			DoubleNode newNext = cur.next.next;
			cur.next = newNext;
			if (newNext != null) {
				newNext.last = cur;
			}
		}
		return head;
	}

	public static void printLinkedList(Node head) {
		System.out.print("Linked List: ");
		while (head != null) {
			System.out.print(head.value + " ");
			head = head.next;
		}
		System.out.println();
	}

	public static void printDoubleLinkedList(DoubleNode head) {
		System.out.print("Double Linked List: ");
		DoubleNode end = null;
		while (head != null) {
			System.out.print(head.value + " ");
			end = head;
			head = head.next;
		}
		System.out.print("| ");
		while (end != null) {
			System.out.print(end.value + " ");
			end = end.last;
		}
		System.out.println();
	}

	public static void main(String[] args) {
		Node head1 = new Node(1);
		head1.next = new Node(2);
		head1.next.next = new Node(3);
		head1.next.next.next = new Node(4);
		head1.next.next.next.next = new Node(5);
		head1.next.next.next.next.next = new Node(6);
		printLinkedList(head1);
		head1 = removeLastKthNode(head1, 3);
		// head1 = removeLastKthNode(head1, 6);
		// head1 = removeLastKthNode(head1, 7);
		printLinkedList(head1);

		DoubleNode head2 = new DoubleNode(1);
		head2.next = new DoubleNode(2);
		head2.next.last = head2;
		head2.next.next = new DoubleNode(3);
		head2.next.next.last = head2.next;
		head2.next.next.next = new DoubleNode(4);
		head2.next.next.next.last = head2.next.next;
		head2.next.next.next.next = new DoubleNode(5);
		head2.next.next.next.next.last = head2.next.next.next;
		head2.next.next.next.next.next = new DoubleNode(6);
		head2.next.next.next.next.next.last = head2.next.next.next.next;
		printDoubleLinkedList(head2);
		head2 = removeLastKthNode(head2, 3);
		// head2 = removeLastKthNode(head2, 6);
		// head2 = removeLastKthNode(head2, 7);
		printDoubleLinkedList(head2);
	}

}
