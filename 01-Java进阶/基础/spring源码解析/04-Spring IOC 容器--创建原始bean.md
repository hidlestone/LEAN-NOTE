## 04-Spring IOC 容器--创建原始bean

### 一、简介
上文了解了 doCreateBean 方法的全过程。本文分析 coCreateBean 方法中的一个重要的调用，即 createBeanInstance 方法。
会看到三种不同的构造 bean 对象的方法，了解构造 bean 对象的两种策略。

### 二、源码分析
#### 2.1、创建 bean 对象的过程
```
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
    Class<?> beanClass = this.resolveBeanClass(mbd, beanName, new Class[0]);
    
    /*
       检测类的访问权限，默认情况下非public 的类，是允许访问的
       若禁止访问，这里会抛出异常
     */
    if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        
       /*
          如果工厂方法不为空，则通过工厂方法构建bean对象
        */ 
    } else if (mbd.getFactoryMethodName() != null) {
        // 通过工厂方法的方式构建bean对象
        return this.instantiateUsingFactoryMethod(beanName, mbd, args);
    } else {
        /*
         * 当多次构建同一个 bean 时，可以使用此处的快捷路径，即无需再次推断应该使用哪种方式构造实例，
         * 以提高效率。比如在多次构建同一个 prototype 类型的 bean 时，就可以走此处的捷径。
         * 这里的 resolved 和 mbd.constructorArgumentsResolved 将会在 bean 第一次实例
         * 化的过程中被设置，在后面的源码中会分析到，先继续往下看。
         */
        boolean resolved = false;
        boolean autowireNecessary = false;
        if (args == null) {
            Object var7 = mbd.constructorArgumentLock;
            synchronized(mbd.constructorArgumentLock) {
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }

        if (resolved) {
            // 通过 构造方法自动注入 的方式构造 bean 对象
            return autowireNecessary ? this.autowireConstructor(beanName, mbd, (Constructor[])null, (Object[])null) : this.instantiateBean(beanName, mbd);
        } else {
            // 通过 默认构造方法 的方式构造 bean 对象
            Constructor<?>[] ctors = this.determineConstructorsFromBeanPostProcessors(beanClass, beanName);
            
            /*
             * 下面的条件分支条件用于判断使用什么方式构造 bean 实例，有两种方式可选 - 构造方法自动
             * 注入和默认构造方法。判断的条件由4部分综合而成，如下：
             * 
             *    条件1：ctors != null -> 后置处理器返回构造方法数组是否为空
             *    
             *    条件2：mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR 
             *              -> bean 配置中的 autowire 属性是否为 constructor    
             *    条件3：mbd.hasConstructorArgumentValues() 
             *              -> constructorArgumentValues 是否存在元素，即 bean 配置文件中
             *                 是否配置了 <construct-arg/>
             *    条件4：!ObjectUtils.isEmpty(args) 
             *              -> args 数组是否存在元素，args 是由用户调用 
             *                 getBean(String name, Object... args) 传入的
             * 
             * 上面4个条件，只要有一个为 true，就会通过构造方法自动注入的方式构造 bean 实例
             */
            return ctors == null && mbd.getResolvedAutowireMode() != 3 && !mbd.hasConstructorArgumentValues() && ObjectUtils.isEmpty(args) ? this.instantiateBean(beanName, mbd) : this.autowireConstructor(beanName, mbd, ctors, args);
        }
    }
}
```
执行流程：
- 检测类的访问权限，若禁止访问，则抛出异常
- 若工厂方法不为空，则通过工厂方法构建 bean 对象，并返回结果
- 若构造方式已解析过，则走快捷路径构建 bean 对象，并返回结果
- 如第三步不满足，则通过组合条件决定使用哪种方式构建 bean 对象

这里又三种构造 bean 对象的方式，如下：
- 通过“工厂方法”的方式构造 bean 对象
- 通过“构造方法自动注入”的方式构造 bean 对象
- 通过“默认构造方法”的方式构造 bean 对象


#### 2.2、通过构造方法自动注入的方式创建bean实例

```
protected BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, Constructor<?>[] ctors, Object[] explicitArgs) {
    // 创建 ConstructorResolver 对象，并调用其 autowireConstructor 方法
    return (new ConstructorResolver(this)).autowireConstructor(beanName, mbd, ctors, explicitArgs);
}


public BeanWrapper autowireConstructor(final String beanName, final RootBeanDefinition mbd, Constructor<?>[] chosenCtors, Object[] explicitArgs) {
    // 创建 BeanWrapper 对象
    BeanWrapperImpl bw = new BeanWrapperImpl();
    this.beanFactory.initBeanWrapper(bw);
    final Constructor<?> constructorToUse = null;
    ConstructorResolver.ArgumentsHolder argsHolderToUse = null;
    final Object[] argsToUse = null;
    
    // 确定参数列表 argsToUse
    if (explicitArgs != null) {
        argsToUse = explicitArgs;
    } else {
        Object[] argsToResolve = null;
        Object var10 = mbd.constructorArgumentLock;
        synchronized(mbd.constructorArgumentLock) {
            // 获取已解析的构造方法
            constructorToUse = (Constructor)mbd.resolvedConstructorOrFactoryMethod;
            if (constructorToUse != null && mbd.constructorArgumentsResolved) {
                // 获取已解析的构造方法参数列表
                argsToUse = mbd.resolvedConstructorArguments;
                if (argsToUse == null) {
                    // 若 argsToUse 为空，则获取未解析的构造方法参数列表
                    argsToResolve = mbd.preparedConstructorArguments;
                }
            }
        }

        if (argsToResolve != null) {
            // 解析参数列表
            argsToUse = this.resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve);
        }
    }

    if (constructorToUse == null) {
        boolean autowiring = chosenCtors != null || mbd.getResolvedAutowireMode() == 3;
        ConstructorArgumentValues resolvedValues = null;
        int minNrOfArgs;
        if (explicitArgs != null) {
            minNrOfArgs = explicitArgs.length;
        } else {
            ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
            resolvedValues = new ConstructorArgumentValues();
            /*
             * 确定构造方法参数数量，比如下面的配置：
             *     <bean id="persion" class="xyz.coolblog.autowire.Person">
             *         <constructor-arg index="0" value="xiaoming"/>
             *         <constructor-arg index="1" value="1"/>
             *         <constructor-arg index="2" value="man"/>
             *     </bean>
             *
             * 此时 minNrOfArgs = maxIndex + 1 = 2 + 1 = 3，除了计算 minNrOfArgs，
             * 下面的方法还会将 cargs 中的参数数据转存到 resolvedValues 中
             */
            minNrOfArgs = this.resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
        }

        // 获取构造方法列表
        Constructor<?>[] candidates = chosenCtors;
        if (chosenCtors == null) {
            Class beanClass = mbd.getBeanClass();

            try {
                candidates = mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors();
            } catch (Throwable var25) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", var25);
            }
        }

        // 按照构造方法的访问权限级别和参数数量进行排序
        AutowireUtils.sortConstructors(candidates);
        int minTypeDiffWeight = 2147483647;
        Set<Constructor<?>> ambiguousConstructors = null;
        LinkedList<UnsatisfiedDependencyException> causes = null;
        Constructor[] var16 = candidates;
        int var17 = candidates.length;

        for(int var18 = 0; var18 < var17; ++var18) {
            Constructor<?> candidate = var16[var18];
            Class<?>[] paramTypes = candidate.getParameterTypes();
            
            /*
             * 下面的 if 分支的用途是：若匹配到到合适的构造方法了，提前结束 for 循环
             * constructorToUse != null 这个条件比较好理解，下面分析一下条件 argsToUse.length > paramTypes.length：
             * 前面说到 AutowireUtils.sortConstructors(candidates) 用于对构造方法进行
             * 排序，排序规则如下：
             *   1. 具有 public 访问权限的构造方法排在非 public 构造方法前
             *   2. 参数数量多的构造方法排在前面
             *
             * 假设现在有一组构造方法按照上面的排序规则进行排序，排序结果如下（省略参数名称）：
             *
             *   1. public Hello(Object, Object, Object)
             *   2. public Hello(Object, Object)
             *   3. public Hello(Object)
             *   4. protected Hello(Integer, Object, Object, Object)
             *   5. protected Hello(Integer, Object, Object)
             *   6. protected Hello(Integer, Object)
             *
             * argsToUse = [num1, obj2]，可以匹配上的构造方法2和构造方法6。由于构造方法2有
             * 更高的访问权限，所以没理由不选他（尽管后者在参数类型上更加匹配）。由于构造方法3
             * 参数数量 < argsToUse.length，参数数量上不匹配，也不应该选。所以 
             * argsToUse.length > paramTypes.length 这个条件用途是：在条件 
             * constructorToUse != null 成立的情况下，通过判断参数数量与参数值数量
             * （argsToUse.length）是否一致，来决定是否提前终止构造方法匹配逻辑。
             */
            if (constructorToUse != null && argsToUse.length > paramTypes.length) {
                break;
            }

            if (paramTypes.length >= minNrOfArgs) {
                ConstructorResolver.ArgumentsHolder argsHolder;
                if (resolvedValues != null) {
                    try {
                        /*
                         * 判断方法是否有 ConstructorProperties 注解，若有，则取注解中的
                         * 值。比如下面的代码：
                         * 
                         *  public class Persion {
                         *      private String name;
                         *      private Integer age;
                         *
                         *      @ConstructorProperties(value = {"coolblog", "20"})
                         *      public Persion(String name, Integer age) {
                         *          this.name = name;
                         *          this.age = age;
                         *      }
                         * }
                         */
                        String[] paramNames = ConstructorResolver.ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
                        if (paramNames == null) {
                            ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                            if (pnd != null) {
                                /*
                                 * 获取构造方法参数名称列表，比如有这样一个构造方法:
                                 *   public Person(String name, int age, String sex)
                                 *   
                                 * 调用 getParameterNames 方法返回 paramNames = [name, age, sex]
                                 */
                                paramNames = pnd.getParameterNames(candidate);
                            }
                        }
                        
                        /* 
                         * 创建参数值列表，返回 argsHolder 会包含进行类型转换后的参数值，比如下
                         * 面的配置:
                         *
                         *     <bean id="persion" class="xyz.coolblog.autowire.Person">
                         *         <constructor-arg name="name" value="xiaoming"/>
                         *         <constructor-arg name="age" value="1"/>
                         *         <constructor-arg name="sex" value="man"/>
                         *     </bean>
                         *
                         * Person 的成员变量 age 是 Integer 类型的，但由于在 Spring 配置中
                         * 只能配成 String 类型，所以这里要进行类型转换。
                         */
                        argsHolder = this.createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, this.getUserDeclaredConstructor(candidate), autowiring);
                    } catch (UnsatisfiedDependencyException var26) {
                        if (this.beanFactory.logger.isTraceEnabled()) {
                            this.beanFactory.logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + var26);
                        }

                        if (causes == null) {
                            causes = new LinkedList();
                        }

                        causes.add(var26);
                        continue;
                    }
                } else {
                    /*
                     * 构造方法参数数量低于配置的参数数量，则忽略当前构造方法，并重试。比如 
                     * argsToUse = [obj1, obj2, obj3, obj4]，上面的构造方法列表中，
                     * 构造方法1、2和3显然不是合适选择，忽略之。
                     */
                    if (paramTypes.length != explicitArgs.length) {
                        continue;
                    }

                    argsHolder = new ConstructorResolver.ArgumentsHolder(explicitArgs);
                }

                /*
                 * 计算参数值（argsHolder.arguments）每个参数类型与构造方法参数列表
                 * （paramTypes）中参数的类型差异量，差异量越大表明参数类型差异越大。参数类型差异
                 * 越大，表明当前构造方法并不是一个最合适的候选项。引入差异量（typeDiffWeight）
                 * 变量目的：是将候选构造方法的参数列表类型与参数值列表类型的差异进行量化，通过量化
                 * 后的数值筛选出最合适的构造方法。
                 * 
                 * 讲完差异量，再来说说 mbd.isLenientConstructorResolution() 条件。
                 * 官方的解释是：返回构造方法的解析模式，有宽松模式（lenient mode）和严格模式
                 * （strict mode）两种类型可选。具体的细节没去研究，就不多说了。
                 */
                int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                if (typeDiffWeight < minTypeDiffWeight) {
                    constructorToUse = candidate;
                    argsHolderToUse = argsHolder;
                    argsToUse = argsHolder.arguments;
                    minTypeDiffWeight = typeDiffWeight;
                    ambiguousConstructors = null;
                    
                  /* 
                   * 如果两个构造方法与参数值类型列表之间的差异量一致，那么这两个方法都可以作为
                   * 候选项，这个时候就出现歧义了，这里先把有歧义的构造方法放入 
                   * ambiguousConstructors 集合中
                   */  
                } else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
                    if (ambiguousConstructors == null) {
                        ambiguousConstructors = new LinkedHashSet();
                        ambiguousConstructors.add(constructorToUse);
                    }

                    ambiguousConstructors.add(candidate);
                }
            }
        }

        // 若上面未能筛选出合适的构造方法，这里将抛出异常
        if (constructorToUse == null) {
            if (causes == null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Could not resolve matching constructor (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
            }

            UnsatisfiedDependencyException ex = (UnsatisfiedDependencyException)causes.removeLast();
            Iterator var35 = causes.iterator();

            while(var35.hasNext()) {
                Exception cause = (Exception)var35.next();
                this.beanFactory.onSuppressedException(cause);
            }

            throw ex;
        }

        /*
         * 如果 constructorToUse != null，且 ambiguousConstructors 也不为空，表明解析
         * 出了多个的合适的构造方法，此时就出现歧义了。Spring 不会擅自决定使用哪个构造方法，
         * 所以抛出异常。
         */
        if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous constructor matches found in bean '" + beanName + "' (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousConstructors);
        }

        if (explicitArgs == null) {
            /*
             * 缓存相关信息，比如：
             *   1. 已解析出的构造方法对象 resolvedConstructorOrFactoryMethod
             *   2. 构造方法参数列表是否已解析标志 constructorArgumentsResolved
             *   3. 参数值列表 resolvedConstructorArguments 或 preparedConstructorArguments
             *
             * 这些信息可用在其他地方，用于进行快捷判断
             */
            argsHolderToUse.storeCache(mbd, constructorToUse);
        }
    }

    try {
        Object beanInstance;
        if (System.getSecurityManager() != null) {
            beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    return ConstructorResolver.this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, ConstructorResolver.this.beanFactory, constructorToUse, argsToUse);
                }
            }, this.beanFactory.getAccessControlContext());
        } else {
            /*
             * 调用实例化策略创建实例，默认情况下使用反射创建实例。如果 bean 的配置信息中
             * 包含 lookup-method 和 replace-method，则通过 CGLIB 增强 bean 实例
             */
            beanInstance = this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
        }
        
        // 设置 beanInstance 到 BeanWrapperImpl 对象中
        bw.setBeanInstance(beanInstance);
        return bw;
    } catch (Throwable var24) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via constructor failed", var24);
    }
}
```
这个方法的核心逻辑就是根据参数类型筛选合适的构造方法。
解析出合适的构造方法后，剩下的工作就是构建bean对象了，这个工作交给实例化策略去做。

工作流程：
- 创建 BeanWrapperImpl 对象
- 解析构造方法参数，并算出 minNrOfArgs
- 获取构造方法列表，并排序
- 遍历排序好的构造方法列表，筛选合适的构造方法
    - 获取构造方法参数列表中每个参数的名称
    - 再次解析参数，此次解析会将value 属性值进行类型转换，由 String 转为合适的类型。
    - 计算构造方法参数列表与参数值列表之间的类型差异量，以筛选出更为合适的构造方法
- 缓存已筛选出的构造方法以及参数值列表，若再次创建 bean 实例时，可直接使用，无需再次进行筛选
- 使用初始化策略创建 bean 对象
- 将 bean 对象放入 BeanWrapperImpl 对象中，并返回该对象


#### 2.3、通过默认构造方法创建 bean 对象
```
protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
    try {
        Object beanInstance;
        // if 条件分支里的一大坨是 Java 安全相关的代码，可以忽略，直接看 else 分支
        if (System.getSecurityManager() != null) {
            beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    return AbstractAutowireCapableBeanFactory.this.getInstantiationStrategy().instantiate(mbd, beanName, AbstractAutowireCapableBeanFactory.this);
                }
            }, this.getAccessControlContext());
        } else {
            /*
             * 调用实例化策略创建实例，默认情况下使用反射创建对象。如果 bean 的配置信息中
             * 包含 lookup-method 和 replace-method，则通过 CGLIB 创建 bean 对象
             */
            beanInstance = this.getInstantiationStrategy().instantiate(mbd, beanName, this);
        }
        
        // 创建 BeanWrapperImpl 对象
        BeanWrapper bw = new BeanWrapperImpl(beanInstance);
        this.initBeanWrapper(bw);
        return bw;
    } catch (Throwable var6) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", var6);
    }
}


public Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner) {
    // 检测 bean 配置中是否配置了 lookup-method 或 replace-method，若配置了，则需使用 CGLIB 构建 bean 对象
    if (bd.getMethodOverrides().isEmpty()) {
        Object var5 = bd.constructorArgumentLock;
        Constructor constructorToUse;
        synchronized(bd.constructorArgumentLock) {
            constructorToUse = (Constructor)bd.resolvedConstructorOrFactoryMethod;
            if (constructorToUse == null) {
                final Class<?> clazz = bd.getBeanClass();
                if (clazz.isInterface()) {
                    throw new BeanInstantiationException(clazz, "Specified class is an interface");
                }

                try {
                    if (System.getSecurityManager() != null) {
                        constructorToUse = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>() {
                            public Constructor<?> run() throws Exception {
                                return clazz.getDeclaredConstructor((Class[])null);
                            }
                        });
                    } else {
                        // 获取默认的构造方法
                        constructorToUse = clazz.getDeclaredConstructor((Class[])null);
                    }
                    // 设置 resolvedConstructorOrFactoryMethod
                    bd.resolvedConstructorOrFactoryMethod = constructorToUse;
                } catch (Throwable var9) {
                    throw new BeanInstantiationException(clazz, "No default constructor found", var9);
                }
            }
        }
        // 通过无参构造方法创建 bean 对象
        return BeanUtils.instantiateClass(constructorToUse, new Object[0]);
    } else {
        // 使用 GCLIB 创建 bean 对象
        return this.instantiateWithMethodInjection(bd, beanName, owner);
    }
}
```
上面就是通过默认构造方法创建 bean 对象的过程，比较简单，就不多说了。最后我们再来看看简单看看通过无参构造方法刚创建 bean 对象的代码（通过 CGLIB 创建 bean 对象的方式就不看了）

```
public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
    Assert.notNull(ctor, "Constructor must not be null");

    try {
        // 设置构造方法为可访问
        ReflectionUtils.makeAccessible(ctor);
        // 通过反射创建 bean 实例，这里 args 是一个没有元素的空数组
        return ctor.newInstance(args);
    } catch (InstantiationException var3) {
        throw new BeanInstantiationException(ctor, "Is it an abstract class?", var3);
    } catch (IllegalAccessException var4) {
        throw new BeanInstantiationException(ctor, "Is the constructor accessible?", var4);
    } catch (IllegalArgumentException var5) {
        throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", var5);
    } catch (InvocationTargetException var6) {
        throw new BeanInstantiationException(ctor, "Constructor threw exception", var6.getTargetException());
    }
}
```










