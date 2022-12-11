## 03-Spring IOC 容器--创建单例bean的过程

### 一、简介
上文中，分析了获取 bean 的方法，也就是 getBean(String) 的实现逻辑。
对于已经实例化好的单例 bean，getBean(String) 并不会再一次去创建，而是重缓存中获取。
如果某个 bean 还未实例化，这时候无法命中缓存，此时就要根据 bean 的配置信息去创建这个 bean。
相较于 getBean(String) 方法的实现逻辑，创建 bean 的方法 createBean(String, RootBeanDefinition, Object[]) 及其所调用的方法逻辑上更为复杂一些。

本文会先大体上分析  createBean(String, RootBeanDefinition, Object[]) 方法的代码逻辑，至于所调用的方法会在后续文章中进行分析。


### 二、源码分析

#### 2.1、创建 bean 实例的入口
createBean 的调用位置：
```
public T doGetBean(...) {
    // 省略不相关代码
    if (mbd.isSingleton()) {
        sharedInstance = getSingleton(beanName, new ObjectFactory<Object>() {
            @Override
            public Object getObject() throws BeansException {
                try {
                    return createBean(beanName, mbd, args);
                }
                catch (BeansException ex) {
                    destroySingleton(beanName);
                    throw ex;
                }
            }
        });
        bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // 省略不相关代码
}
```
上面是 doGetBean 方法的代码片段，从中可以发现 createBean 方法。createBean 方法被匿名工厂类的 getObject 方法包裹，但这个匿名工厂类对象并未直接调用 getObject 方法。而是将自身作为参数传给了getSingleton(String, ObjectFactory)方法。

```
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    Assert.notNull(beanName, "'beanName' must not be null");
    Map var3 = this.singletonObjects;
    synchronized(this.singletonObjects) {
        // 从缓存中获取单例 bean，若不为空，则直接返回，不用再初始化
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            if (this.singletonsCurrentlyInDestruction) {
                throw new BeanCreationNotAllowedException(beanName, "Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
            }

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
            }
            
            /* 
             * 将 beanName 添加到 singletonsCurrentlyInCreation 集合中，
             * 用于表明 beanName 对应的 bean 正在创建中
             */
            this.beforeSingletonCreation(beanName);
            boolean newSingleton = false;
            boolean recordSuppressedExceptions = this.suppressedExceptions == null;
            if (recordSuppressedExceptions) {
                this.suppressedExceptions = new LinkedHashSet();
            }

            try {
                // 通过 getObject 方法调用 createBean 方法创建bean实例
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            } catch (IllegalStateException var16) {
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null) {
                    throw var16;
                }
            } catch (BeanCreationException var17) {
                BeanCreationException ex = var17;
                if (recordSuppressedExceptions) {
                    Iterator var8 = this.suppressedExceptions.iterator();

                    while(var8.hasNext()) {
                        Exception suppressedException = (Exception)var8.next();
                        ex.addRelatedCause(suppressedException);
                    }
                }

                throw ex;
            } finally {
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = null;
                }
                // 将 beanName 从 singletonCurrentlyCreation 中移除
                this.afterSingletonCreation(beanName);
            }

            if (newSingleton) {
                /* 
                 * 将 <beanName, singletonObject> 键值对添加到 singletonObjects 集合中，
                 * 并从其他集合（比如 earlySingletonObjects）中移除 singletonObject 记录
                 */
                this.addSingleton(beanName, singletonObject);
            }
        }

        return singletonObject != NULL_OBJECT ? singletonObject : null;
    }
}
```
1、先从 singletonObjects 集合获取 bean 实例，若不为空，则直接返回。
2、若为空，进入创建 bean 实例阶段。先将 beanName 添加到 singletonsCurretlyInCreation。
3、通过 getObject 方法调用 createBean 方法创建 bean 实例。
4、将 beanName 从 singletonsCurrentlyInVreation 集合中移除。
5、将 <beanName,singletonObject> 映射缓存到 singletonObjects 集合中。

在上述的分析中，我们知道了 createBean 方法在哪里被调用到。


#### 2.2、createBean 方法

```
protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
    if (this.logger.isDebugEnabled()) {
        this.logger.debug("Creating instance of bean '" + beanName + "'");
    }

    RootBeanDefinition mbdToUse = mbd;
    // 解析 bean 烈性
    Class<?> resolvedClass = this.resolveBeanClass(mbd, beanName, new Class[0]);
    if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
        mbdToUse = new RootBeanDefinition(mbd);
        mbdToUse.setBeanClass(resolvedClass);
    }

    try {
        // 处理lookup-method 和 replace-method 配置，Spring 将这两个配置统称为 override method
        mbdToUse.prepareMethodOverrides();
    } catch (BeanDefinitionValidationException var7) {
        throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(), beanName, "Validation of method overrides failed", var7);
    }

    Object beanInstance;
    try {
        // 在 bean 初始化钱应用后置处理，如果后置处理返回的 bean 不为空，则直接返回
        beanInstance = this.resolveBeforeInstantiation(beanName, mbdToUse);
        if (beanInstance != null) {
            return beanInstance;
        }
    } catch (Throwable var8) {
        throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "BeanPostProcessor before instantiation of bean failed", var8);
    }

    // 调用 doCreateBean 创建 bean
    beanInstance = this.doCreateBean(beanName, mbdToUse, args);
    if (this.logger.isDebugEnabled()) {
        this.logger.debug("Finished creating instance of bean '" + beanName + "'");
    }

    return beanInstance;
}
```
1、解析 bean 类型
2、处理 lookup-method 和 replace-method 配置
3、在 bean 初始化前应用后置处理，若后置处理返回的 bean 不为空，则直接返回
4、若上一部后置处理返回的 bean 为空，则调用 doCreateBean 创建 bean 实例


#### 2.2.1、验证和准备 override 方法
当用户配置了 lookup-method 和 replace-method 时，Spring 需要对目标 bean 进行增强。在增强之前，需要做一些准备工作，
也就是 prepareMethodOverrides 的逻辑。
```
public void prepareMethodOverrides() throws BeanDefinitionValidationException {
    MethodOverrides methodOverrides = this.getMethodOverrides();
    if (!methodOverrides.isEmpty()) {
        Set<MethodOverride> overrides = methodOverrides.getOverrides();
        synchronized(overrides) {
            // 循环处理每一个 MethodOverride 对象
            Iterator var4 = overrides.iterator();

            while(var4.hasNext()) {
                MethodOverride mo = (MethodOverride)var4.next();
                this.prepareMethodOverride(mo);
            }
        }
    }
}

protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
    // 获取名称为 mo.getMethodName() 的方法数量，当方法重载时，count 的值就会大于1
    int count = ClassUtils.getMethodCountForName(this.getBeanClass(), mo.getMethodName());
    // 表明根据方法名未找到对应的方法，此时抛出异常
    if (count == 0) {
        throw new BeanDefinitionValidationException("Invalid method override: no method with name '" + mo.getMethodName() + "' on class [" + this.getBeanClassName() + "]");
    } else {
        // 表明仅存在方法名为 mo.getMethodName()，这意味着方法不需要重载
        if (count == 1) {
            // 该方法不存在重载，overload 成员变量设为 false
            mo.setOverloaded(false);
        }

    }
}
```
上面的源码中，prepareMethodOverrides方法循环调用了prepareMethodOverride方法，并没其他的太多逻辑。主要准备工作都是在 prepareMethodOverride 方法中进行的。
prepareMethodOverride 这个方法主要用于获取指定方法的方法数量 count，并根据 count 的值进行相应的处理。count = 0 时，表明方法不存在，此时抛出异常。count = 1 时，设置 MethodOverride 对象的overloaded成员变量为 false。这样做的目的在于，提前标注名称mo.getMethodName()的方法不存在重载，在使用 CGLIB 增强阶段就不需要进行校验，直接找到某个方法进行增强即可。


#### 2.2.2、bean 实例话前的后置处理
后置处理是 Spring 的一个拓展点，用户通过实现 BeanPostProcessor 接口，并将实现类配置到 Spring 的配置文件中（或者使用注解），即可在 bean 初始化前后进行自定义操作。

createBean 方法中的后置处理逻辑：
```
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
    Object bean = null;
    // 检测是否解析过，mbd.beforeInstantiationResolved 的值在限免的代码中会被设置
    if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
        if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            Class<?> targetType = this.determineTargetType(beanName, mbd);
            if (targetType != null) {
                // 应用前置处理
                bean = this.applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                if (bean != null) {
                    // 应用后置处理
                    bean = this.applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
        }
        // 设置 mbd.beforeInstantiationResolved
        mbd.beforeInstantiationResolved = bean != null;
    }

    return bean;
}


protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
    Iterator var3 = this.getBeanPostProcessors().iterator();

    while(var3.hasNext()) {
        BeanPostProcessor bp = (BeanPostProcessor)var3.next();
        // InstantiationAwareBeanPostProcessor 一般在 Spring 框架内部使用，不建议用户直接使用
        if (bp instanceof InstantiationAwareBeanPostProcessor) {
            InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
            // bean 初始化前置处理
            Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
            if (result != null) {
                return result;
            }
        }
    }

    return null;
}


public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
    Object result = existingBean;
    Iterator var4 = this.getBeanPostProcessors().iterator();

    do {
        if (!var4.hasNext()) {
            return result;
        }

        BeanPostProcessor beanProcessor = (BeanPostProcessor)var4.next();
        // bean 初始化后置处理
        result = beanProcessor.postProcessAfterInitialization(result, beanName);
    } while(result != null);

    return result;
}
```
在 resolveBeforeInstantiation 方法中，当前置处理方法返回的 bean 不为空时，后置处理才会被执行。
前置处理器 InstantiationAwareBeanPostProcessor 类型的，这种类型的处理器一般用在 Spring 框架的内部，比如
AOP 模块中 AbstractAutoProxyCreator 抽象类间接实现了这个接口中 postProcessBeforeInstantiation 方法，所以 AOP 可以在这个方法中生成为目标类的代理对象。


#### 2.2.3、调用 doCreateBean 方法创建 bean
```
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
    /* 
     * BeanWrapper 是一个基础接口，由接口名可看出这个接口的实现类用于包裹 bean 实例。
     * 通过 BeanWrapper 的实现类可以方便的设置/获取 bean 实例的属性
     */
    BeanWrapper instanceWrapper = null;
    if (mbd.isSingleton()) {
        // 从缓存中获取BeanWrapper，并清理相关记录
        instanceWrapper = (BeanWrapper)this.factoryBeanInstanceCache.remove(beanName);
    }

    if (instanceWrapper == null) {
        /* 
         * 创建 bean 实例，并将实例包裹在 BeanWrapper 实现类对象中返回。createBeanInstance 
         * 中包含三种创建 bean 实例的方式：
         *   1. 通过工厂方法创建 bean 实例
         *   2. 通过构造方法自动注入（autowire by constructor）的方式创建 bean 实例
         *   3. 通过无参构造方法方法创建 bean 实例
         *
         * 若 bean 的配置信息中配置了 lookup-method 和 replace-method，则会使用 CGLIB 
         */
        instanceWrapper = this.createBeanInstance(beanName, mbd, args);
    }

    // 此处 bean 可以认为是一个原始的 bean 实例，暂未填充属性
    final Object bean = instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null;
    Class<?> beanType = instanceWrapper != null ? instanceWrapper.getWrappedClass() : null;
    mbd.resolvedTargetType = beanType;
    Object var7 = mbd.postProcessingLock;
    
    // 这里又遇到后置处理了，此处的后置处理是用于处理已“合并的 BeanDefinition”。关于这种后置处理器具体的实现细节就不深入理解了
    synchronized(mbd.postProcessingLock) {
        if (!mbd.postProcessed) {
            try {
                this.applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            } catch (Throwable var17) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Post-processing of merged bean definition failed", var17);
            }

            mbd.postProcessed = true;
        }
    }

    /*
     * earlySingletonExposure 是一个重要的变量，这里要说明一下。该变量用于表示是否提前暴露
     * 单例 bean，用于解决循环依赖。earlySingletonExposure 由三个条件综合而成，如下：
     *   条件1：mbd.isSingleton() - 表示 bean 是否是单例类型
     *   条件2：allowCircularReferences - 是否允许循环依赖
     *   条件3：isSingletonCurrentlyInCreation(beanName) - 当前 bean 是否处于创建的状态中
     * 
     * earlySingletonExposure = 条件1 && 条件2 && 条件3 
     *                        = 单例 && 是否允许循环依赖 && 是否存于创建状态中。
     */
    boolean earlySingletonExposure = mbd.isSingleton() && this.allowCircularReferences && this.isSingletonCurrentlyInCreation(beanName);
    if (earlySingletonExposure) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
        }
        
        // 添加工厂对象到 SingletonFactory 缓存中
        this.addSingletonFactory(beanName, new ObjectFactory<Object>() {
            public Object getObject() throws BeansException {
                // 获取早期 bean 的引用，如果 bean 中的方法被 AOP 切带你匹配到，此时AOP相关逻辑会织入
                return AbstractAutowireCapableBeanFactory.this.getEarlyBeanReference(beanName, mbd, bean);
            }
        });
    }

    Object exposedObject = bean;

    try {
        // 向 bean 实例中填充属性，populateBean 方法也是一个重要的方法
        this.populateBean(beanName, mbd, instanceWrapper);
        if (exposedObject != null) {
            /*
              进行余下的初始化工作，如下
              1、判断 bean 是否实现了 BeanNameAwere、BeanFactoryAware、BeanClassLoaderAware 等接口，并执行接口方法
              2、应用 bean 初始化前置操作 
              3、如果 bean 实现了 InitializingBean 接口，则执行 afterPropertiesSet 方法，如果用户配置了init-method，则调用相关方法
              4、应用 bean 初始化后置操作
              另外 AOP 相关逻辑也会在该方法中织入切面逻辑，此时的 exposedObject 变成了一个代理对象
             */
            exposedObject = this.initializeBean(beanName, exposedObject, mbd);
        }
    } catch (Throwable var18) {
        if (var18 instanceof BeanCreationException && beanName.equals(((BeanCreationException)var18).getBeanName())) {
            throw (BeanCreationException)var18;
        }

        throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", var18);
    }

    if (earlySingletonExposure) {
        Object earlySingletonReference = this.getSingleton(beanName, false);
        if (earlySingletonReference != null) {
            // 若 initializeBean 方法未改变 exposedObject 的引用，则此处的条件为 true。
            if (exposedObject == bean) {
                exposedObject = earlySingletonReference;
            } else if (!this.allowRawInjectionDespiteWrapping && this.hasDependentBean(beanName)) {
                String[] dependentBeans = this.getDependentBeans(beanName);
                Set<String> actualDependentBeans = new LinkedHashSet(dependentBeans.length);
                String[] var12 = dependentBeans;
                int var13 = dependentBeans.length;

                for(int var14 = 0; var14 < var13; ++var14) {
                    String dependentBean = var12[var14];
                    if (!this.removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                        actualDependentBeans.add(dependentBean);
                    }
                }

                if (!actualDependentBeans.isEmpty()) {
                    throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
                }
            }
        }
    }

    try {
        // 注册销毁逻辑
        this.registerDisposableBeanIfNecessary(beanName, bean, mbd);
        return exposedObject;
    } catch (BeanDefinitionValidationException var16) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", var16);
    }
}
```
doCreateBean 方法的执行流程：
1、从缓存中获取 BeanWrapper 实现类对象，并清理相关记录
2、若未命中缓存，则创建 bean 实例，并将实例包裹在 BeanWrapper 实现类对象中返回
3、应用 MergedBeanDefinitionPostProcessor 后置处理器相关逻辑
4、根据条件决定是否提前暴露 bean 的早期引用（early reference），用于处理循环依赖问题
5、调用 populateBean 方法向 bean 实例中填充属性
6、调用 initializeBean 方法完成余下的初始化工作
7、注册销毁逻辑

doCreateBean 方法的流程比较复杂，步骤略多。由此也可了解到创建一个 bean 还是很复杂的，这中间要做的事情繁多。比如填充属性、对 BeanPostProcessor 拓展点提供支持等。以上的步骤对应的方法具体是怎样实现的，本文并不打算展开分析。


















