package com.payn.codeinterviewguide.Chapter02_List;

/**
 * @author zhuangpf
 * @date 2022-12-20
 */
public class Q2006 {
	
	/*
	题目：
	环形链表的约瑟夫问题
	
	据说著名犹太历史学家Josephus有过以下故事：在罗马人占领乔塔帕特后，39个犹太人与Josephus及他的朋友躲到一个洞中，
	39个犹太人决定宁愿死也不要被敌人抓到，于是决定了一个自杀方式，41个人排成个圆圈，由第l个人开始报数，报数到3的人就自杀，
	然后再由下一个人重新报l，报数到3的人再自杀，这样依次下去，直到剩下最后一个人时，那个人可以自由选择自己的命运。
	这就是著名的约瑟夫问题。现在请用单向环形链表 描述该结构并呈现整个自杀过程。
	**/

	public static class Node {
		public int value;
		public Node next;

		public Node(int data) {
			this.value = data;
		}
	}

	// 普通解法
	public static Node josephusKill1(Node head, int m) {
		if (null == head || head.next == head || m < 1) {
			return head;
		}
		Node last = head;// 找到第一个节点的前一个
		while (last.next != head) {
			last = last.next;
		}
		int count = 0;
		while (head != last) {
			if (++count == m) {
				last.next = head.next;
				count = 0;
			} else {
				last = last.next;
			}
			head = last.next;
		}
		return head;
	}

	/*
	进阶解法：
	 
	**/
	public static Node josephusKill2(Node head, int m) {
		if (head == null || head.next == head || m < 1) {
			return head;
		}

		return head;
	}

	public static void printCircularList(Node head) {
		if (head == null) {
			return;
		}
		System.out.print("Circular List: " + head.value + " ");
		Node cur = head.next;
		while (cur != head) {
			System.out.print(cur.value + " ");
			cur = cur.next;
		}
		System.out.println("-> " + head.value);
	}

	public static void main(String[] args) {
		Node head1 = new Node(1);
		head1.next = new Node(2);
		head1.next.next = new Node(3);
		head1.next.next.next = new Node(4);
		head1.next.next.next.next = new Node(5);
		head1.next.next.next.next.next = head1;
		printCircularList(head1);
		head1 = josephusKill1(head1, 3);
		printCircularList(head1);
	}

}
