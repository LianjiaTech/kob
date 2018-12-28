## 用户手册 

### 产品简介 
```
Kob作业平台，以把任务组装成作业的思想，动态配置作业需求孵化而出的作业平台。
```
#### 背景 
```
在互联网开发进入，微服务快速迭代的模式后，每个微服务若都实现内部跑批任务，从整体角度看，成本还是比较大的。
```

#### 需求 
```
通过接入作业平台，快速完成作业调度业务场景。
```

#### 架构 
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/fm.png)
```
客户端项目：通过接入作业调度平台jar包，完成任务的编写。
作业调度平台项目：通过客户端任务动态配置出所需作业。
```

#### 调度原理
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/liuchengtu.png)

### 核心特性 
```
1. 客户端任务动态注册发现
2. 任务动态配置成作业
3. 任务执行负载均衡策略
```
### 前置依赖 
```
1. mysql
2. zookeeper
```
### 项目简介 
```
1. kob-admin-console 作业管理平台。
2. kob-admin-core 作业管理底层代码实现模块，以jar包引入方式被kob-admin-console、kob-admin-processor依赖。
3. kob-admin-processor 作业调度执行器。
4. kob-basic 服务端（ kob-admin-* ）、客户端（ kob-client-* ）公用jar包。
5. kob-client-demo-spring-boot 客户端springboot的一种接入形式。
6. kob-client-parent 作业调度客户端项目依赖jar包。
     kob-client-spring 客户端spring项目依赖jar，会被kob-client-spring-boot-starter依赖。
     kob-client-spring-boot-starter 客户端spring-boot项目依赖jar。
7. table.sql 以集群名称是incubating，生成的sql文件
```

### 快速开始 
##### Maven install 
```
mvn clean install -DskipTests
```
##### pom依赖 
```
<dependency>
  <groupId>com.ke</groupId>
  <artifactId>kob-client-spring-boot-starter</artifactId>
</dependency>
```
##### 创建项目 
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/project_access.png)
##### 配置文件 
```
kob.client:
  cluster: incubator
  project_code: kob_monitor
  zk_servers: localhost:2801
  admin_url: http://localhost:8668
```
##### 编写任务 
```
@Task(key = "hello", remark = "你好")
public TaskResult helloWorld(TaskBaseContext context){
    System.out.println(JSON.toJSONString(context));
    return TaskResult.success();
}
```
##### 配置作业 
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/job_init.png)


### 联系我们 
```
交流群：970170336
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
