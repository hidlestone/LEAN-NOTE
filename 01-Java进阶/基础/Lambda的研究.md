# Lambda的研究

**1、filer**
```java
public void test01() {
	List<Integer> list = Arrays.asList(7, 6, 9, 3, 8, 2, 1);
	// 遍历输出符合条件的元素
	list.stream().filter(x -> x > 6).forEach(System.out::println);
	// 匹配第一个
	Optional<Integer> findFirst = list.stream().filter(x -> x > 6).findFirst();
	// 匹配任意（适用于并行流）
            // 串行流：适合存在线程安全问题、阻塞任务、重量级任务，以及需要使用同一事务的逻辑。
	// 并行流：适合没有线程安全问题、较单纯的数据处理任务。
	Optional<Integer> findAny = list.parallelStream().filter(x -> x > 6).findAny();
	// 是否包含符合特定条件的元素
	boolean anyMatch = list.stream().anyMatch(x -> x < 6);
	System.out.println("匹配第一个值：" + findFirst.get());
	System.out.println("匹配任意一个值：" + findAny.get());
	System.out.println("是否存在大于6的值：" + anyMatch);
}
```

**2、to list**
```java
public class Person {
    private String name;  	// 姓名
    private int salary; 	// 薪资
    private int age; 		// 年龄
    private String sex; 	// 性别
    private String area;  	// 地区
}

public void test03() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	personList.add(new Person("Anni", 8200, 24, "female", "New York"));
	personList.add(new Person("Owen", 9500, 25, "male", "New York"));
	personList.add(new Person("Alisa", 7900, 26, "female", "New York"));
	List<String> fiterList = personList.stream().filter(x -> x.getSalary() > 8000).map(Person::getName)
			.collect(Collectors.toList());
	System.out.print("高于8000的员工姓名：" + fiterList);
}
```

**3、max**
```java
public void test04() {
	List<String> list = Arrays.asList("adnm", "admmt", "pot", "xbangd", "weoujgsd");
	Optional<String> max = list.stream().max(Comparator.comparing(String::length));
	System.out.println("最长的字符串：" + max.get());
}
```

**4、sort**
```java
public void test05() {
	List<Integer> list = Arrays.asList(7, 6, 9, 4, 11, 6);
	// 自然排序
	Optional<Integer> max = list.stream().max(Integer::compareTo);
	// 自定义排序
	Optional<Integer> max2 = list.stream().max(new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	});
	System.out.println("自然排序的最大值：" + max.get());
	System.out.println("自定义排序的最大值：" + max2.get());
}

public void test06() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	personList.add(new Person("Anni", 8200, 24, "female", "New York"));
	personList.add(new Person("Owen", 9500, 25, "male", "New York"));
	personList.add(new Person("Alisa", 7900, 26, "female", "New York"));
	Optional<Person> max = personList.stream().max(Comparator.comparingInt(Person::getSalary));
	System.out.println("员工工资最大值：" + max.get().getSalary());
}
```

**5、count**
```java
public void test07() {
	List<Integer> list = Arrays.asList(7, 6, 4, 8, 2, 11, 9);
	long count = list.stream().filter(x -> x > 6).count();
	System.out.println("list中大于6的元素个数：" + count);
}
```

**6、对象转换**
```java
public void test09() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	personList.add(new Person("Anni", 8200, 24, "female", "New York"));
	personList.add(new Person("Owen", 9500, 25, "male", "New York"));
	personList.add(new Person("Alisa", 7900, 26, "female", "New York"));
	// 不改变原来员工集合的方式
	List<Person> personListNew = personList.stream().map(person -> {
		Person personNew = new Person(person.getName(), 0, 0, null, null);
		personNew.setSalary(person.getSalary() + 10000);
		return personNew;
	}).collect(Collectors.toList());
	System.out.println("一次改动前：" + personList.get(0).getName() + "-->" + personList.get(0).getSalary());
	System.out.println("一次改动后：" + personListNew.get(0).getName() + "-->" + personListNew.get(0).getSalary());
	// 改变原来员工集合的方式
	List<Person> personListNew2 = personList.stream().map(person -> {
		person.setSalary(person.getSalary() + 10000);
		return person;
	}).collect(Collectors.toList());
	System.out.println("二次改动前：" + personList.get(0).getName() + "-->" + personListNew.get(0).getSalary());
	System.out.println("二次改动后：" + personListNew2.get(0).getName() + "-->" + personListNew.get(0).getSalary());
}
一次改动前：Tom-->8900
一次改动后：Tom-->18900
二次改动前：Tom-->18900
二次改动后：Tom-->18900
```
**7、转stream**
```java
public void test10() {
	List<String> list = Arrays.asList("m,k,l,a", "1,3,5,7");
	List<String> listNew = list.stream().flatMap(s -> {
		// 将每个元素转换成一个stream
		String[] split = s.split(",");
		Stream<String> s2 = Arrays.stream(split);
		return s2;
	}).collect(Collectors.toList());
	System.out.println("处理前的集合：" + list);
	System.out.println("处理后的集合：" + listNew);
}
```

**8、规约**
归约，也称缩减，顾名思义，是把一个流缩减成一个值，能实现对集合求和、求乘积和求最值操作。
```java
public void test11() {
	List<Integer> list = Arrays.asList(1, 3, 2, 8, 11, 4);
	// 求和方式1
	Optional<Integer> sum = list.stream().reduce((x, y) -> x + y);
	// 求和方式2
	Optional<Integer> sum2 = list.stream().reduce(Integer::sum);
	// 求和方式3
	Integer sum3 = list.stream().reduce(0, Integer::sum);
	// 求乘积
	Optional<Integer> product = list.stream().reduce((x, y) -> x * y);
	// 求最大值方式1
	Optional<Integer> max = list.stream().reduce((x, y) -> x > y ? x : y);
	// 求最大值写法2
	Integer max2 = list.stream().reduce(12, Integer::max);
	System.out.println("list求和：" + sum.get() + "," + sum2.get() + "," + sum3);
	System.out.println("list求积：" + product.get());
	System.out.println("list求和：" + max.get() + "," + max2);
}

 public void test12() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	personList.add(new Person("Anni", 8200, 24, "female", "New York"));
	personList.add(new Person("Owen", 9500, 25, "male", "New York"));
	personList.add(new Person("Alisa", 7900, 26, "female", "New York"));
	// 求工资之和方式1：
	Optional<Integer> sumSalary = personList.stream().map(Person::getSalary).reduce(Integer::sum);
	// 求工资之和方式2：
	Integer sumSalary2 = personList.stream().reduce(0, (sum, p) -> sum += p.getSalary(),
			(sum1, sum2) -> sum1 + sum2);
	// 求工资之和方式3：
	Integer sumSalary3 = personList.stream().reduce(0, (sum, p) -> sum += p.getSalary(), Integer::sum);
	// 求最高工资方式1：
	Integer maxSalary = personList.stream().reduce(0, (max, p) -> max > p.getSalary() ? max : p.getSalary(),
			Integer::max);
	// 求最高工资方式2：
	Integer maxSalary2 = personList.stream().reduce(0, (max, p) -> max > p.getSalary() ? max : p.getSalary(),
			(max1, max2) -> max1 > max2 ? max1 : max2);
	System.out.println("工资之和：" + sumSalary.get() + "," + sumSalary2 + "," + sumSalary3);
	System.out.println("最高工资：" + maxSalary + "," + maxSalary2);
}
```

**9、归集**
即toList/toSet/toMap
```java
public void test13() {
	List<Integer> list = Arrays.asList(1, 6, 3, 4, 6, 7, 9, 6, 20);
	List<Integer> listNew = list.stream().filter(x -> x % 2 == 0).collect(Collectors.toList());
	Set<Integer> set = list.stream().filter(x -> x % 2 == 0).collect(Collectors.toSet());
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person(null, 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", null));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	personList.add(new Person("Anni", 8200, 24, "female", "New York"));
	Map<?, Person> map = personList.stream().filter(p -> p.getSalary() > 8000)
			.collect(Collectors.toMap(Person::getName, p -> p));
	System.out.println("toList:" + listNew);
	System.out.println("toSet:" + set);
	System.out.println("toMap:" + map);
	Map<String, String> collect2 = personList.stream().collect(Collectors.toMap(Person::getName, Person::getArea));
	System.out.println(collect2);
	Map<String, String> collect = personList.stream().collect(Collectors.toMap(Person::getName, Person::getArea));
	System.out.println(collect);
}
```

**10、统计**
count/averaging
```java
public void test14() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	// 求总数
	Long count = personList.stream().collect(Collectors.counting());
	// 求平均工资
	Double average = personList.stream().collect(Collectors.averagingDouble(Person::getSalary));
	// 求最高工资
	Optional<Integer> max = personList.stream().map(Person::getSalary).collect(Collectors.maxBy(Integer::compare));
	// 求工资之和
	Integer sum = personList.stream().collect(Collectors.summingInt(Person::getSalary));
	// 一次性统计所有信息
	DoubleSummaryStatistics collect = personList.stream().collect(Collectors.summarizingDouble(Person::getSalary));
	System.out.println("员工总数：" + count);
	System.out.println("员工平均工资：" + average);
	System.out.println("员工工资总和：" + sum);
	System.out.println("员工工资所有统计：" + collect);
}
```

**11、分组**
partitioningBy/groupingBy
```java
public void test15() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 18, "male", "New York"));
	personList.add(new Person("Jack", 7000, 18, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 18, "female", "Washington"));
	personList.add(new Person("Anni", 8200, 18, "female", "New York"));
	personList.add(new Person("Owen", 9500, 18, "male", "New York"));
	personList.add(new Person("Alisa", 7900, 18, "female", "New York"));
	// 将员工按薪资是否高于8000分组
	Map<Boolean, List<Person>> part = personList.stream().collect(Collectors.partitioningBy(x -> x.getSalary() > 8000));
	// 将员工按性别分组
	Map<String, List<Person>> group = personList.stream().collect(Collectors.groupingBy(Person::getSex));
	// 将员工先按性别分组，再按地区分组
	Map<String, Map<String, List<Person>>> group2 = personList.stream().collect(Collectors.groupingBy(Person::getSex, Collectors.groupingBy(Person::getArea)));
	System.out.println("员工按薪资是否大于8000分组情况：" + part);
	System.out.println("员工按性别分组情况：" + group);
	System.out.println("员工按性别、地区：" + group2);
}
```

**12、joining**
joining可以将stream中的元素用特定的连接符（没有的话，则直接连接）连接成一个字符串。
```java
public void test16() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	String names = personList.stream().map(p -> p.getName()).collect(Collectors.joining(","));
	System.out.println("所有员工的姓名：" + names);
	List<String> list = Arrays.asList("A", "B", "C");
	String string = list.stream().collect(Collectors.joining("-"));
	System.out.println("拼接后的字符串：" + string);
}
```

**13、reducing**
Collectors类提供的reducing方法，相比于stream本身的reduce方法，增加了对自定义归约的支持。
```java
public void test17() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Tom", 8900, 23, "male", "New York"));
	personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
	// 每个员工减去起征点后的薪资之和（这个例子并不严谨，但一时没想到好的例子）
	Integer sum = personList.stream().collect(Collectors.reducing(0, Person::getSalary, (i, j) -> (i + j - 5000)));
	System.out.println("员工扣税薪资总和：" + sum);
	// stream的reduce
	Optional<Integer> sum2 = personList.stream().map(Person::getSalary).reduce(Integer::sum);
	System.out.println("员工薪资总和：" + sum2.get());
}
```

**14、排序**
sorted，中间操作。有两种排序：
sorted()：自然排序，流中元素需实现Comparable接口。
sorted(Comparator com)：Comparator排序器自定义排序。
```java
public void test18() {
	List<Person> personList = new ArrayList<Person>();
	personList.add(new Person("Sherry", 9000, 24, "female", "New York"));
	personList.add(new Person("Tom", 8900, 22, "male", "Washington"));
	personList.add(new Person("Jack", 9000, 25, "male", "Washington"));
	personList.add(new Person("Lily", 8800, 26, "male", "New York"));
	personList.add(new Person("Alisa", 9000, 26, "female", "New York"));
	long count = personList.stream().map(Person::getSalary).distinct().count();
	System.out.println(count);
	personList.stream().sorted(Comparator.comparing(Person::getSalary)).map(Person::getName);
	// 按工资升序排序（自然排序）
	List<String> newList = personList.stream().sorted(Comparator.comparing(Person::getSalary)).map(Person::getName)
			.collect(Collectors.toList());
	// 按工资倒序排序
	List<String> newList2 = personList.stream().sorted(Comparator.comparing(Person::getSalary).reversed())
			.map(Person::getName).collect(Collectors.toList());
	// 先按工资再按年龄升序排序
	List<String> newList3 = personList.stream()
			.sorted(Comparator.comparing(Person::getSalary).thenComparing(Person::getAge)).map(Person::getName)
			.collect(Collectors.toList());
	// 先按工资再按年龄自定义排序（降序）
	List<String> newList4 = personList.stream().sorted((p1, p2) -> {
		if (p1.getSalary() == p2.getSalary()) {
			return p2.getAge() - p1.getAge();
		} else {
			return p2.getSalary() - p1.getSalary();
		}
	}).map(Person::getName).collect(Collectors.toList());
	System.out.println("按工资升序排序：" + newList);
	System.out.println("按工资降序排序：" + newList2);
	System.out.println("先按工资再按年龄升序排序：" + newList3);
	System.out.println("先按工资再按年龄自定义降序排序：" + newList4);
}
```

**15、提取/组合**
流也可以进行合并、去重、限制、跳过等操作。
```java
public void test19() {
	String[] arr1 = {"a", "b", "c", "d"};
	String[] arr2 = {"d", "e", "f", "g"};
	Stream<String> stream1 = Stream.of(arr1);
	Stream<String> stream2 = Stream.of(arr2);
	// concat:合并两个流 distinct：去重
	List<String> newList = Stream.concat(stream1, stream2).distinct().collect(Collectors.toList());
	// limit：限制从流中获得前n个数据
	List<Integer> collect = Stream.iterate(1, x -> x + 2).limit(10).collect(Collectors.toList());
	// skip：跳过前n个数据
	List<Integer> collect2 = Stream.iterate(1, x -> x + 2).skip(1).limit(5).collect(Collectors.toList());
	System.out.println("流合并：" + newList);
	System.out.println("limit：" + collect);
	System.out.println("skip：" + collect2);
}
```

**16、collect详解**
```java
public class CollectTest {
	/**
	 * stream.collect() 的本质由三个参数构成,
	 * 1. Supplier 生产者, 返回最终结果
	 * 2. BiConsumer<R, ? super T> accumulator 累加器
	 * 第一个参数是要返回的集合, 第二个参数是遍历过程中的每个元素,
	 * 将流中每个被遍历的元素添加到集合中
	 * 3. BiConsumer<R, R> combiner 合并器, 在有并行流的时候才会有用, 一个流时代码不会走到这里
	 * 将第二步遍历得到的所有流形成的list都添加到最终的list中,
	 * 最后返回list1
	 */
	@Test
	public void Test() {
		Stream<String> stream = Stream.of("hello", "world", "helloworld");
		// 最原始和基础的方式
        /*
        List<String> list = stream.collect(
                ()->new ArrayList(),
                (theList, item) -> theList.add(item),
                (list1, list2) -> list1.addAll(list2)
        );
        */
		// 打印出更详尽的过程
		List<String> listDetail = stream.collect(
				() -> {
					ArrayList<String> arrayList = new ArrayList<>();
					System.out.println("第一个list诞生, size: " + arrayList.size());
					return arrayList;
				},
				(theList, item) -> {
					System.out.println("第二个list的size: " + theList.size());
					theList.add(item);
				},
				(list1, list2) -> {
					System.out.println("第三个list1的size: " + list1.size());
					System.out.println("第四个list2的size: " + list2.size());
					list1.addAll(list2);
				}
		);
        /* 输出
            第一个list诞生, size: 0
            第二个list的size: 0
            第二个list的size: 1
            第二个list的size: 2
        * */
		// 使用方法引用来传递行为, 更加清晰易懂, new(新建) -> add(累加) -> addAll(合并)
		List<String> list2 = stream.collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
		String concat = stream.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
		System.out.println(concat);
	}

	@Test
	public void Test2() {
		Stream<String> stream = Stream.of("hello", "world", "helloworld");
		// 这样的写法兼具灵活和简单
		ArrayList<String> list = stream.collect(Collectors.toCollection(ArrayList::new));
		TreeSet<String> treeSet = stream.collect(Collectors.toCollection(TreeSet::new));
		String s = stream.collect(Collectors.joining()); // 拼接成字符串
		HashMap<String, String> map = stream.collect(HashMap::new, (x, y) -> {
			x.put(y, y); // 自己做自己的key
		}, HashMap::putAll);
	}
}
```

**17、flatMap**
```java
public void test() {
	List<String> teamIndia = Arrays.asList("Virat", "Dhoni", "Jadeja");
	List<String> teamAustralia = Arrays.asList("Warner", "Watson", "Smith");
	List<String> teamEngland = Arrays.asList("Alex", "Bell", "Broad");
	List<List<String>> playersInWorldCup2016 = new ArrayList<>();
	playersInWorldCup2016.add(teamIndia);
	playersInWorldCup2016.add(teamAustralia);
	playersInWorldCup2016.add(teamEngland);
	// Let's print all players before Java 8
	List<String> listOfAllPlayers = new ArrayList<>();

	for (List<String> team : playersInWorldCup2016) {
		for (String name : team) {
			listOfAllPlayers.add(name);
		}
	}
	System.out.println("Players playing in world cup 2016");
	System.out.println(listOfAllPlayers);
	// Now let's do this in Java 8 using FlatMap
	List<String> flatMapList = playersInWorldCup2016.stream()
//				.flatMap(pList -> pList.stream())
			.flatMap(List::stream)
			.collect(Collectors.toList());

	System.out.println("List of all Players using Java 8");
	System.out.println(flatMapList);
}
```

