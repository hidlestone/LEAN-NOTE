package com.payn.codeinterviewguide.Chapter02_List;

/**
 * @author zhuangpf
 * @date 2022-12-20
 */
public class Q2005 {
	
	/*
	题目：
	反转部分单向链表
	
	给定一个单向链表的头节点head，以及两个链表from和to，在单向链表上把第from个节点到第to个节点这一部分进行反转。
	1->2->3->4->5->null,from=2,to=4
	调整结果为：1->4->3->2->5->null
	
	
	分析：
	1、先判断是否满足，1<=from<=to<=N，如果不满足，则直接返回原来的头节点
	2、找到第from-1个节点fpre和to+1个节点tpos，fpre即反转部分的前一个节点，tpos即反转部分的后一个节点。
	   把需要反转的部分先反转，再正确连接。
	3、如果fpre为null，说明反转的部分是包含头节点的，则返回新的头节点。也就是没反转之前的反转部分的最后一个节点。
	**/

	public static class Node {
		public int value;
		public Node next;

		public Node(int data) {
			this.value = data;
		}
	}

	public static Node reversePart(Node head, int from, int to) {
		int len = 0;
		Node node1 = head;
		Node fpre = null;
		Node tpos = null;
		while (null != node1) {
			len++;
			fpre = len == from - 1 ? node1 : fpre;
			tpos = len == to + 1 ? node1 : tpos;
			node1 = node1.next;
		}
		if (from > to || from < 1 || to > len) {
			return head;
		}
		node1 = fpre == null ? head : fpre.next;
		Node node2 = node1.next;
		node1.next = tpos;
		Node next = null;
		while (node2 != tpos) {
			next = node2.next;
			node2.next = node1;
			node1 = node2;
			node2 = next;
		}
		if (fpre != null) {
			fpre.next = node1;
			return head;
		}
		return node1;
	}

	public static void printLinkedList(Node head) {
		System.out.print("Linked List: ");
		while (head != null) {
			System.out.print(head.value + " ");
			head = head.next;
		}
		System.out.println();
	}

	public static void main(String[] args) {
		Node head = null;
		printLinkedList(head);
		head = reversePart(head, 1, 1);
		printLinkedList(head);

		head = new Node(1);
		printLinkedList(head);
		head = reversePart(head, 1, 1);
		printLinkedList(head);

		head = new Node(1);
		head.next = new Node(2);
		printLinkedList(head);
		head = reversePart(head, 1, 2);
		printLinkedList(head);

		head = new Node(1);
		head.next = new Node(2);
		head.next.next = new Node(3);
		printLinkedList(head);
		head = reversePart(head, 2, 3);
		printLinkedList(head);

		head = new Node(1);
		head.next = new Node(2);
		head.next.next = new Node(3);
		printLinkedList(head);
		head = reversePart(head, 1, 3);
		printLinkedList(head);

	}
	
}
