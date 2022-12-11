package com.payn.leetcode;

import java.util.HashMap;

/**
 * @author zhuangpf
 * @date 2022-10-23
 */
public class Question03 {

	/*
	题目：
	给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。
	**/
	public int lengthOfLongestSubstring(String s) {
		HashMap<Character, Integer> map = new HashMap<>();
		int maxlen = 0;
		int left = 0;

		for (int i = 0; i < s.length(); i++) {
			if (map.containsKey(s.charAt(i))) {
				left = Math.max(left, map.get(s.charAt(i)) + 1);
			}
			map.put(s.charAt(i), i);
			maxlen = Math.max(maxlen, i - left + 1);
		}
		return maxlen;
	}

}
