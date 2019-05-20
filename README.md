### 背景 

随着贝壳的业务功能的不断扩大，具有复杂功能的单体应用随之进入了微服务开发的迭代模式。项目需要作业调度模块是个常见需求，在之前单体系统中，集成了Quartz完成作业调度模块，因为单体应用集成一次后，单从技术层面看几乎没有新的工作量，而且整体还是比较稳定的，但是当单体应用进行微服务拆分后，很多微服务项目都需要集成作业调度模块，常规的一些作业调度实现，已经无法满足公司级的微服务项目扩张，一种轻量级、分布式、统一管理的作业调度框架势在必行。

![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/chaifen0.png)

### 需求 

#### 从公司角度的需求

由于单体应用改为微服务项目，微服务项目的作业调度功能百花齐放，作业调度系统在整体项目体系中属于底层基础组件项目，整体需易于接入公司的监控和规范，原理需完全掌握，如任务执行出现问题，要有及时的报警以便及时跟进，并有定期日报汇总以便于掌握整体微服务系统作业的调度情况。作业调度原理要100%的掌握，不允许线上出现技术原理不清而阻塞故障的修复。当接入作业越来越多时，要有负载机制，监控整体系统运行状态。

#### 从业务系统角度的需求

作业调度系统要非常易于接入，且接入问题需要有专项小组提供解决方案，不能阻塞业务系统开发，作业需要灵活配置，且调度要准确。而且用户手册要足够强大，作业调度专项小组在必要的情况下要持续支援有些快速开发的业务项目，因为这种突击项目着重关心业务实现，根本不关心基础组件实现细节，更不想遇到组件使用的阻塞。即：投入低，回报高。

### 技术调研

在github上调研了目前比较流行作业调度项目，其大致可以分为一下几个形态：

#### 深度优化定制Quartz形态

深度了解学习改造Quartz源码，将Quartz使用DB迁移至使用Zookeeper，通过开发后端管理系统操作Zookeeper，从而来管理整个作业调度任务的配置，这种形态的作业调度，原理上作业调度实现还是在客户端完成，管理系统很弱化。这样的作业调度系统有个与生俱来的优势是：在管理系统出现故障时候也不会影响作业的继续作业。因为客户端项目几乎集成全量的作业调度代码实现。

![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/xingtai2.png)

#### Quartz服务端形态

将Quartz抽象并封装成服务端，客户端通过基础client-refence.jar完成任务的 hook method 暴露，服务端完成作业配置任务，当作业有任务触发时，通过一种协议触发客户端项目任务的勾子方法，完成调度。这种形态的作业调度框架，客户端一定程度减少了作业调度代码量，和项目性能消耗，但是带来了很多分布式调度问题，需要深入了解整体调度的实现。
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/xingtai1.png)

#### 自实现调度系统

这种形态的作业调度系统基本都是功能非常多的，功能自实现一部分，也可通过SPI集成一部分，比如调度可以用Quartz实现，或是框架自身通过解析语法如：CronExpression实现Cron作业解析出未来一定时间内的任务，当时间到达触发时间。触发可支持客户端集成，或通过一种协议触发客户端项目任务来完成调度。学习这类形态的作业调度架构，可以发人深省，无论从广度或者深度，都可以使学习者对作业调度架构的认识更上一层楼。

### 思考与实践
通过对作业系统的背景需求的认识，和在github进行的技术调研学习，结合我们自身特点，在实现自己的作业调度过程中，我们思考了如下几点：

#### 调度设计与实现

客户端暴露任务，通过注册发现将任务与服务端数据同步，在服务端将发现到的任务自定义的组装成作业，完成整体作业调度功能。服务端集成公司监控和规范。

#### 整体架构设计

![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/fm.png)

#### 作业调度原理实现

![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/liuchengtu.png)

#### 作业调度服务端高可用的实现方案

服务端通过Zookeeper主备选举，完成服务端自身高可用实现，另外服务端暴露出健康接口，这里我们公司有对服务健康接口的持续监测，如果集群中，某个健康接口出现异常，会立刻报警给值班人员。

#### 作业调度服务端实现

这里我们调研了第三种形态中的功能实现，通过对调度底层实现的学习，我们实现了任务在服务端的可配置作业。

#### 如何做到客户端极简实现

作业调度需求项目通过集成client-refence.jar将任务方法暴露到注册中心，并内部实现任务触发方法，以便完成任务调度的触发。其原理是利用反射机制，通过在方法上增加特定注解，识别出此注解方法为任务方法。

#### 任务方法的注册发现

前面说了客户端的极简实现，客户端注册任务到注册中心上面，且通过心跳完成本地暴露任务与注册中心的任务持续的一致性。

#### 项目权限设计

随之客户端项目越来越多的接入作业平台，肯定是需要一种权限体系完成客户端接入项目的逻辑隔离，这里我们参考git项目人员管理体系，项目名称为唯一标识，创建项目的同学为此项目的owner，可根据系统号邀请其他同学加入此项目，共同维护此项目作业配置。

#### 异常报警实现

定义异常事件类型和内容，如：任务失败事件，内容是异常信息等，通过观察者模式结合项目人员完成异常事件的实时报警。结合上面的权限成员，立刻发送给任务所在项目组成员。


#### 依赖、分片任务的实现

这里我们参考了一些国外的科研项目，结合业内比较流行的设计，通过用有序无环图（DAG）来实现任务的串联依赖，与任务的平级分片。

### 产品特点
 
1. 客户端任务动态注册发现
2. 任务动态配置成作业
3. 任务执行负载均衡策略

### 前置依赖 
```
1. Mysql
2. Zookeeper
3. Java
4. Maven
```

### 代码模块简介 
```
1. kob-server-console 作业管理平台。
2. kob-server-core 作业管理底层代码实现模块，以jar包引入方式被kob-server-console、kob-server-processor依赖。
3. kob-server-processor 作业调度执行器。
4. kob-basic 服务端（ kob-server-* ）、客户端（ kob-client-* ）公用jar包。
5. kob-client-demo-spring-boot 客户端springboot的一种接入形式。
6. kob-client-parent 作业调度客户端项目依赖jar包。
     kob-client-spring 客户端spring项目依赖jar，会被kob-client-spring-boot-starter依赖。
     kob-client-spring-boot-starter 客户端spring-boot项目依赖jar。
7. table.sql 以集群名称是incubating，生成的sql文件
```

### 快速开始 

#### Maven install 
```java
mvn clean install -DskipTests
```

#### pom依赖 
```java
<dependency>
  <groupId>com.ke</groupId>
  <artifactId>kob-client-spring-boot-starter</artifactId>
</dependency>
```

#### 创建项目 
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/project_access.png)

#### Client配置文件 
```java
kob.client:
  zp: incubator
  project_code: kob_monitor
  Zookeeper_servers: localhost:2801
  admin_url: http://localhost:8668
```

#### Client编写任务 
```java
@Task(key = "hello", remark = "你好")
public TaskResult helloWorld(TaskBaseContext context){
    System.out.println(JSON.toJSONString(context));
    return TaskResult.success();
}
```

#### Server配置作业 
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/job_init.png)

### 联系我们 
```
QQ交流群：970170336
```
### 如何贡献 
```
1. 根据开发计划 提交pr
2. 解决issue
```
### 报告问题 
```
问题建议叙述
1. 环境（os，jre，kob verison ...）
2. 问题描述（如何操作、现象、日志 ...）
3. 是否可以稳定复现
```

### 许可 
kob is licensed under the Apache 2.0 License. (http://www.apache.org/licenses/LICENSE-2.0.txt)
