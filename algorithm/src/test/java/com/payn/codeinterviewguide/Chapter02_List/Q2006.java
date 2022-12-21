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
	在由n个节点组成的环中，这个幸存节点的编号。--> Num(n)
	已知：Num(1) = 1
	如果再确定Num(i-1)和Num(i)的关系，就可以通过递归过程求出Num(n)。 
	
	---------------
	A    B
	1    1
	2    2
	...  ...
	i    i
	i+1  1
	i+2  2
	
	报A的编号B的节点，则A和B的对应关系：
	B=(A-1)%i+1
	
	---------------
	如果编号为s的节点删除，环的节点数自然从i编程i-1。那么原来再大小为i的环中，每个节点的编号会发生什么变化。
	old   new
	...  ...
	s-2  i-2
	s-1  i-1
	s    无编号是因为被删除
	s+1  1
	s+2  2
	
	假设环大小为i的节点编号记为old，环大小为i-1的每个节点编号记为new。
	则old与new关系的数学表达式：old=(new+s-1)%i+1
	
	因为每次都是报数到m的节点被杀，所以根据步骤1的表达式：B=(A-1)%i+1 , A=m 。
	被杀的节点编号 (m-1)%i+1 ,即 s=(m-1)%i+1 ,带入到步骤2的表达式 old=(new+s-1)%i+1 。
	old=(new+m-1)%i+1
	至此，得到了Num(i-1) new ,Num(i) old 的关系
	
	
	  
	  
	**/
	public static Node josephusKill2(Node head, int m) {
		if (head == null || head.next == head || m < 1) {
			return head;
		}
		Node cur = head.next;
		int tmp = 1; // 单链表长度
		while (cur != head) {
			tmp++;
			cur = cur.next;
		}


		return head;
	}

	public static int getLive(int i, int m) {
		if (i == 1) {
			return 1;
		}
		return (getLive(i - 1, m) + m - 1) % i + 1;
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
