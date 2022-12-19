package com.payn.codeinterviewguide.Chapter02_List;

/**
 * @author zhuangpf
 * @date 2022-12-18
 */
public class Q2003 {
	
	/*
	题目：
	删除链表的中间节点和a/b处的节点 
	
	给定链表的头结点head，实现删除链表的中间节点的函数。
	例如：
	不删除任何节点；
	1->2,           删除节点1
	1->2->3,        删除节点2
	1->2->3->4,     删除节点2
	1->2->3->4->5,  删除节点3

	给定链表的头节点head,整数a和整数b，实现删除位于a/b处节点的函数。
	1->2->3->4->5，假设a/b的值为r。
	如果r等于0，不删除任何节点；
	如果r在区间(0,1/5]上，删除节点1；
	如果r在区间(1/5,2/5]上，删除节点2；
	如果r在区间(2/5,3/5]上，删除节点3；
	如果r在区间(3/5,4/5]上，删除节点4；
	如果r在区间(4/5,1]上，删除节点5；
	如果r大于1，不删除任何节点。
	
	分析：
	中间节点
	链表长度每增加2，要删除的节点就后移一位。
	
	**/

	public static class Node {
		public int value;
		public Node next;

		public Node(int data) {
			this.value = data;
		}
	}

	// 删除中间的节点
	public static Node removeMidNode(Node head) {
		if (null == head || null == head.next) {
			return head;
		}
		if (head.next.next == null) {
			return head.next;
		}
		// 1->2->3,        删除节点2
		Node pre = head;
		Node cur = head.next.next;
		while (null != cur.next && null != cur.next.next) {
			pre = pre.next;
			cur = cur.next.next;
		}
		return head;
	}

	// 删除a/b处的节点
	public static Node removeByRatio(Node head, int a, int b) {
		if (a < 1 || a > b) {
			return head;
		}
		// 节点总数
		int n = 0;
		Node cur = head;
		while (cur != null) {
			n++;
			cur = cur.next;
		}
		// 计算需要删除的节点的位置
		n = (int) Math.ceil((a * n) / b);
		if (n == 1) {
			head = head.next;
		}
		if (n > 1) {
			cur = head;
			while (--n != 1) {
				cur = cur.next;
			}
			cur.next = cur.next.next;
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

	public static void main(String[] args) {
		Node head = new Node(1);
		head.next = new Node(2);
		head.next.next = new Node(3);
		head.next.next.next = new Node(4);
		head.next.next.next.next = new Node(5);
		head.next.next.next.next.next = new Node(6);

		printLinkedList(head);
		head = removeMidNode(head);
		printLinkedList(head);
		head = removeByRatio(head, 2, 5);
		printLinkedList(head);
		head = removeByRatio(head, 1, 3);
		printLinkedList(head);
	}


}
