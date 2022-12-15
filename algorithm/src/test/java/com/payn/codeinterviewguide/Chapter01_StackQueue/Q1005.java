package com.payn.codeinterviewguide.Chapter01_StackQueue;

/**
 * @author zhuangpf
 * @date 2022-12-12
 */
public class Q1005 {

	/*
	题目：
	汉诺塔问题比较经典，这里修改一下游戏规则：现在限制不能从最左侧的塔直接移动到最右侧，也不能从最右侧直接移动到最左侧，而是必须经过中间。求当塔有N层的时候，打印最优移动过程和最优移动总步数。
	例如，当塔数为两层时，最上层的塔记为1，最下层的塔记为2，则打印：
	Move 1 from left to mid
	Move 1 from mid to right
	Move 2 from left to mid
	Move 1 from right to mid
	Move 1 from mid to left
	Move 2 from mid to right
	Move 1 from left to mid
	Move 1 from mid to right
	It will move 8 steps
	**/

	public static int hanoiProblem1(int num, String left, String mid,
									String right) {
		if (num < 1) {
			return 0;
		}
		return process(num, left, mid, right, left, right);
	}

	public static int process(int num, String left, String mid, String right,
							  String from, String to) {
		if (num == 1) {
			if (from.equals(mid) || to.equals(mid)) {
				System.out.println("Move 1 from " + from + " to " + to);
				return 1;
			} else {
				System.out.println("Move 1 from " + from + " to " + mid);
				System.out.println("Move 1 from " + mid + " to " + to);
				return 2;
			}
		}
		if (from.equals(mid) || to.equals(mid)) {
			String another = (from.equals(left) || to.equals(left)) ? right : left;
			int part1 = process(num - 1, left, mid, right, from, another);
			int part2 = 1;
			System.out.println("Move " + num + " from " + from + " to " + to);
			int part3 = process(num - 1, left, mid, right, another, to);
			return part1 + part2 + part3;
		} else {
			int part1 = process(num - 1, left, mid, right, from, to);
			int part2 = 1;
			System.out.println("Move " + num + " from " + from + " to " + mid);
			int part3 = process(num - 1, left, mid, right, to, from);
			int part4 = 1;
			System.out.println("Move " + num + " from " + mid + " to " + to);
			int part5 = process(num - 1, left, mid, right, from, to);
			return part1 + part2 + part3 + part4 + part5;
		}
	}

}
