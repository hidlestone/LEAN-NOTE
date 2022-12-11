package com.payn.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangpf
 * @date 2022-11-29
 */
public class Question30 {

	public static List<Integer> findSubstring(String s, String[] words) {
		List<Integer> res = new ArrayList<Integer>();
		int m = words.length, n = words[0].length(), ls = s.length();
		for (int i = 0; i < n; i++) {
			if (i + m * n > ls) {
				break;
			}
			Map<String, Integer> differ = new HashMap<String, Integer>();
			for (int j = 0; j < m; j++) {
				String word = s.substring(i + j * n, i + (j + 1) * n);
				differ.put(word, differ.getOrDefault(word, 0) + 1);
			}
			for (String word : words) {
				differ.put(word, differ.getOrDefault(word, 0) - 1);
				if (differ.get(word) == 0) {
					differ.remove(word);
				}
			}
			for (int start = i; start < ls - m * n + 1; start += n) {
				if (start != i) {
					String word = s.substring(start + (m - 1) * n, start + m * n);
					differ.put(word, differ.getOrDefault(word, 0) + 1);
					if (differ.get(word) == 0) {
						differ.remove(word);
					}
					word = s.substring(start - n, start);
					differ.put(word, differ.getOrDefault(word, 0) - 1);
					if (differ.get(word) == 0) {
						differ.remove(word);
					}
				}
				if (differ.isEmpty()) {
					res.add(start);
				}
			}
		}
		return res;
	}

	public static void main(String[] args) {
		String[] words = {"foo", "bar"};
		List<Integer> idxList = findSubstring2("barfoothefoobarman", words);
		System.out.println(idxList);
	}

	public static List<Integer> findSubstring2(String s, String[] words) {
		List<Integer> res = new ArrayList<Integer>();
		int m = words.length;
		int n = words[0].length();
		int ls = s.length();
		for (int i = 0; i < n; i++) {// 即不同的起始位置
			if (i + n * m > ls) {// 长度不足直接退出循环
				break;
			}
			Map<String, Integer> differ = new HashMap<>();
			// 在窗口范围内统计
			for (int j = 0; j < m; j++) {
				// 窗口范围内截取单词
				String word = s.substring(i + j * n, i + (j + 1) * n);
				differ.put(word, differ.getOrDefault(word, 0) + 1);
			}
			for (String word : words) {
				differ.put(word, differ.getOrDefault(word, 0) - 1);
				if (0 == differ.get(word)) {
					differ.remove(word);
				}
			}
			for (int start = i; start < ls - m * n + 1; start += n) {
				if (start != i) {
					// 右侧移入n，新进入的单词
					String word = s.substring(start + (m - 1) * n, start + m * n);
					differ.put(word, differ.getOrDefault(word, 0) + 1);
					if (0 == differ.get(word)) {
						differ.remove(word);
					}
					// 左侧移除n，移除的单词
					word = s.substring(start - n, start);
					differ.put(word, differ.getOrDefault(word, 0) - 1);
					if (differ.get(word) == 0) {
						differ.remove(word);
					}
				}
				if (differ.isEmpty()) {
					res.add(start);
				}
			}
		}
		return res;
	}

}
