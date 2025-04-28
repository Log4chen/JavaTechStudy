##### 同一建模语言UML（Unified Modeling Language）

*   泛化（Generalization） Is-A ： Student is Person
*   实现（Realization）
*   依赖 (Dependency) Uses-A
*   关联 (Association)
    *   聚合(Aggregation) Has-A\:Department has Teacher,Teacher可以独立于Department存在
    *   组合(Composition) Contains-A House has Room，Room不能独立于House存在

详见：[UML类图与类的关系详解](http://www.uml.org.cn/oobject/201104212.asp)

##### 设计模式原则

*   单一职责原则
*   开闭原则：模块、类和函数应该对扩展开放，对修改关闭
*   里氏替换原则：派生类型必须完全可以替代其基类型
*   接口隔离原则：客户端不应该依赖于它所不需要的接口
*   依赖倒置原则：高级模块不应该依赖低级模块，两者都应该依赖抽象。抽象不应该依赖于细节，细节应该依赖于抽象。

##### 设计模式-创建型模式

*   单例模式
*   工厂模式
    *   简单工厂模式
    *   工厂方法模式
    *   抽象工厂模式
*   建造者模式
*   原型模式
*   对象池模式

##### 设计模式-行为模式

*   策略模式
*   观察者模式
*   模板方法模式
*   责任链模式
*   迭代器模式

##### 设计模式-结构型模式

*   代理模式
*   适配器模式
*   装饰器模式
*   桥接模式
*   组合模式

##### 函数式编程

*   函数式设计模式
    *   MapReduce映射-规约：将现有任务分解为多个较小的任务，使他们并行运行并聚合结果（规约），它能够大大改善大数据处理的性能。

