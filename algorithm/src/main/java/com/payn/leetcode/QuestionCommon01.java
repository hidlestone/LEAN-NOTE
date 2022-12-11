package com.payn.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuestionCommon01 {

	public static List<Integer> genRandomNum() {
		Random random = new Random();
		List nums = new ArrayList();
		for (int i = 0; i < 5; i++) {
			int n = random.nextInt(10);
			nums.add(n);
		}
		System.out.println(nums);
		return nums;
	}

	/*
	7, 0, 6, 8, 1
	相邻的两个数比较，交换，每次循环找出最大值。
	* */
	public static void bubbleSort(Integer[] nums) {
		int len = nums.length;
		while (len > 0) {
			for (int i = 0; i < len - 1; i++) {
				int next = i + 1;
				if (nums[i] > nums[next]) {
					int temp = nums[next];
					nums[next] = nums[i];
					nums[i] = temp;
				}
			}
			len--;
		}
	}

	/*
	https://blog.csdn.net/zhuxian1277/article/details/112466047
	8,9,1,7,2,3,5,4,6,0
	↑				  ↑
	[8] 9 1 7 2 3 5 4 6 0  基准点
						<-- 
	[0] 9 1 7 2 3 5 4 6 |0
	-->
	0 |9 1 7 2 3 5 4 6 9
	0 6 1 7 2 3 5 4 |6 9
	
	>>>
	0 6 1 7 2 3 5 4 8 9
	* */
	public static int partition(int[] arr, int left, int right) {
		// 基准点
		int pivot = arr[left];
		while (left < right) {
			// 从右往左移动，知道遇到小于pivot的元素
			while (right > left && arr[right] >= pivot) {
				right--;
			}
			arr[left] = arr[right];         // 记录小于 pivot 的值

			/* 再从左往右移动，直到遇见大于 pivot 的元素 */
			while (left < right && arr[left] <= pivot) {
				left++;
			}
			arr[right] = arr[left];         // 记录大于 pivot 的值
		}
		arr[left] = pivot;                    // 记录基准元素到当前指针指向的区域
		System.out.println(Arrays.toString(arr));
		return left;                        // 返回基准元素的索引
	}

	/*
	8,9,1,7,2,3,5,4,6,0
	
	一趟快速排序：將序列分片，基准元素左边的都是小于它的，右边的都是大于它的
	
	* */
	public static void quickSort(int[] nums, int left, int right) {
		if (left < right) {
			int index = partition(nums, left, right);
			// 基准元素左边递归
			quickSort(nums, left, index - 1);
			// 基准元素右边递归
			quickSort(nums, index + 1, right);
		}
	}

	/*
	7, 0, 6, 8, 1
	找出数组中的最小值，然后与第一位交换，然后再找出第二小的值与第二位交换
	0 7 6 8 1
	* */
	public static void selectSort(int[] arr) {
		for (int i = 0; i < arr.length - 1; i++) {
			int minIdx = i;
			int min = arr[minIdx];

			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] < min) {
					min = arr[j];
					minIdx = j;
				}
			}
			if (minIdx != i) {
				arr[minIdx] = arr[i];
				arr[i] = min;
			}
		}
	}

	/*
		4 6 3 5 9
				 4
			  6     3
		   5     9
		最后一个非叶子节点的索引就是 arr.len / 2 -1
		对于此图数组长度为5，最后一个非叶子节点为5/2-1=1，即为6这个节点
		
		
		
		

		https://blog.csdn.net/weixin_51609435/article/details/122982075
		
		
		从第一个非叶子结点从下至上，从右至左调整结构
		
		public static void adjustHeap(int[] arr, int i, int length) {
        int temp = arr[i];//先取出当前元素i
        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {//从i结点的左子结点开始，也就是2i+1处开始
            if (k + 1 < length && arr[k] < arr[k + 1]) {//如果左子结点小于右子结点，k指向右子结点
                k++;
            }
            if (arr[k] > temp) {//如果子节点大于父节点，将子节点值赋给父节点（不用进行交换）
                arr[i] = arr[k];
                i = k;
            } else {
                break;
            }
        }
        arr[i] = temp;//将temp值放到最终的位置
    }
	 * */
	// 调整堆结构
	// https://blog.csdn.net/y3over/article/details/86145291
	public static void adjustHeap(int[] arr, int i, int length) {
		// 取出当前元素
		int temp = arr[i]; // 这里即第一个非叶子节点的值
		for (int k = 2 * i + 1; k < length; k = 2 * k + 1) {// 如果左子节点小于右子节点，则k指向右子节点
			if (k + 1 < length && arr[k] < arr[k + 1]) {
				k++;
			}
			if (arr[k] > temp) {
				arr[i] = arr[k];
				i = k;
			} else {
				break;
			}
		}
		arr[i] = temp;// 将temp值放到最终的位置
	}

	public static void heapSort(int[] arr) {
		// 1、构建大根堆
		for (int i = arr.length / 2 - 1; i >= 0; i--) {
			adjustHeap(arr, i, arr.length);
		}
		System.out.println("------");
		// 2、调整堆结构+交换堆顶元素
		for (int j = arr.length - 1; j > 0; j--) {
			// 将堆顶的元素和末尾的元素进行交换
			int temp = arr[0];
			arr[0] = arr[j];
			arr[j] = temp;
			// 重新对堆进行调整
			adjustHeap(arr, 0, j);
		}
		System.out.println(Arrays.toString(arr));
	}

	/*
	7, 0, 6, 8, 1
	
	
	
	* */
	public static void insertSort(int[] arr) {
		// 从第二个元素开始
		for (int i = 0; i < arr.length; i++) {
			int insertVal = arr[i];// 待插入的值
			// 待插入数前一个数的下标
			int insertIdx = i - 1;
			while (insertIdx >= 0 && insertVal < arr[insertIdx]) {
				arr[insertIdx + 1] = arr[insertIdx];
				insertIdx--;// 为了找到插入的位置吗，不断向前遍历
			}
			// 把之前存起来的要插入的数插入到对应的位置
			// insertIndex+1:顺序正确时，+1保持值不变 | 顺序不正确时（已经进了while循环减过1了）：+1后就是要插入的位置
			arr[insertIdx + 1] = insertVal;
			System.out.println(Arrays.toString(arr));
		}
	}



	public static void exchangeShellSort(int arr[]) {
		int temp;// 临时数据
		boolean flag = false;// 是否交换
		int count = 1;
		// 分而治之，将数组分组排序，i为步长
		for (int i = arr.length / 2; i > 0; i /= 2) {
			// 遍历分治的每一个分组
			for (int j = i; j < arr.length; j++) {
				// 遍历分治的每一个分组,的每一个值
				for (int k = j - i; k >= 0; k -= i) {
					if (arr[k + i] < arr[k]) {
						// 交换
						temp = arr[k + i];
						arr[k + i] = arr[k];
						arr[k] = temp;
						flag = true;
					}
					if (!flag) {
						break;
					} else {
						// 为了下次判断
						flag = false;
					}
				}
			}
			System.out.println("希尔排序交换法第" + (count++) + "次排序后" + Arrays.toString(arr));
		}
	}

	public static void insertShellSort(int[] arr) {
		int count = 1;//计数
		// 分而治之，循环为每次总数除二
		for (int i = arr.length / 2; i > 0; i /= 2) {
			for (int j = i; j < arr.length; j++) {
				int index = j;
				int temp = arr[index];
				// 比较每一组的值
				if (arr[index] < arr[index - i]) {
					// 如果后面的比前面的小，则将前面的数往后移
					while (index - i > 0 && temp < arr[index - i]) {
						arr[index] = arr[index - i];
						index -= i;
					}
					arr[index] = temp;
				}
			}
			System.out.println("希尔排序插入法第" + (count++) + "次排序后" + Arrays.toString(arr));
		}
	}

	public static void main(String[] args) throws Exception {
		int[] arr = new int[]{1, 4, 6, 3, 8, 9, 2, 23};
		mergeSort(arr);
//		insertShellSort(arr);
	}
	
	public static int[] mergeSort(int[] sourceArray) throws Exception {
		// 拷贝数组
		int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);
		if (arr.length < 2) {
			return arr;
		}
		int middle = (int) Math.floor(arr.length / 2);

		int[] left = Arrays.copyOfRange(arr, 0, middle);
		int[] right = Arrays.copyOfRange(arr, middle, arr.length);

		return merge(mergeSort(left), mergeSort(right));
	}

	protected static int[] merge(int[] left, int[] right) {
		// 初始化新的数组
		int[] result = new int[left.length + right.length];
		int i = 0;
		while (left.length > 0 && right.length > 0) {
			if (left[0] <= right[0]) {
				result[i++] = left[0];
				left = Arrays.copyOfRange(left, 1, left.length);
			} else {
				result[i++] = right[0];
				right = Arrays.copyOfRange(right, 1, right.length);
			}
		}
		while (left.length > 0) {
			result[i++] = left[0];
			left = Arrays.copyOfRange(left, 1, left.length);
		}
		while (right.length > 0) {
			result[i++] = right[0];
			right = Arrays.copyOfRange(right, 1, right.length);
		}
		return result;
	}


}
