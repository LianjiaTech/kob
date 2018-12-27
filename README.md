<a name="ml_"></a>
## Kob-parent (incubating) Project
## 目录
* <a href="#ml_00">1 用户手册</a>
	* <a href="#ml_00_00">1.1 产品简介</a>
		* <a href="#ml_00_00_00">1.1.1 背景</a>
		* <a href="#ml_00_00_10">1.1.2 需求</a>
		* <a href="#ml_00_00_20">1.1.3 架构理解</a>
		* <a href="#ml_00_00_30">1.1.4 调度原理</a>
	* <a href="#ml_00_10">2.1 核心特性</a>
	* <a href="#ml_00_20">2.2 前置依赖</a>
	* <a href="#ml_00_30">2.3 项目简介</a>
	* <a href="#ml_00_50">2.4 快速开始</a>
		* <a href="#ml_00_50_10">2.5.1 作业调度平台</a>
			* <a href="#ml_00_50_10_00">2.5.1.1 kob-admin-console 管理平台启动</a>
			* <a href="#ml_00_50_10_00">2.5.1.2 kob-admin-processor 作业处理中心启动</a>
		* <a href="#ml_00_50_20">2.5.3 客户端项目接入</a>
			* <a href="#ml_00_50_20_00">2.5.3.1 Maven install</a>
			* <a href="#ml_00_50_20_10">2.5.3.2 pom依赖</a>
			* <a href="#ml_00_50_20_20">2.5.3.2 创建项目</a>
			* <a href="#ml_00_50_20_30">2.5.3.3 配置文件</a>
			* <a href="#ml_00_50_20_40">2.5.3.4 编写任务</a>
			* <a href="#ml_00_50_20_50">2.5.3.5 配置作业</a>
	* <a href="#ml_00_60">2.6 联系我们</a>
	* <a href="#ml_00_70">2.7 如何贡献</a>
	* <a href="#ml_00_80">2.8 报告问题</a>
	* <a href="#ml_00_90">2.9 许可</a>

<a name="ml_00"></a>
## 1 用户手册 <a href="#ml_">↑</a>

<a name="ml_00_00"></a>
### 1.1 产品简介 <a href="#ml_">↑</a>
```
Kob作业平台，以任务组装成作业的思想，动态配置作业需求孵化而出的作业平台。
```
<a name="ml_00_00_00"></a>
#### 1.1.1 背景 <a href="#ml_">↑</a>
```
在互联网开发进入，微服务快速迭代的模式后，每个微服务若都实现内部跑批任务，冲整体角度看，成本还是比较大的。
```

<a name="ml_00_00_10"></a>
#### 1.1.2 需求 <a href="#ml_">↑</a>
```
通过接入作业平台的项目，快速完成作业调度业务场景。
```

<a name="ml_00_00_20"></a>
#### 1.1.2 架构 <a href="#ml_">↑</a>
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/fm.png)
```
客户端项目：通过进入作业调度平台jar包，完成任务的编写。
作业调度平台项目：通过客户端任务动态配置出所需作业。
```

<a name="ml_00_00_30"></a>
#### 1.1.3 调度原理<a href="#ml_">↑</a>
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/liuchengtu.png)

<a name="ml_00_10"></a>
### 1.2 核心特性 <a href="#ml_">↑</a>
```
1. 客户端任务动态注册发现
2. 任务动态配置成作业
3. 任务执行负载均衡策略
```
<a name="ml_00_20"></a>
### 1.2 前置依赖 <a href="#ml_">↑</a>
```
1. mysql
2. zookeeper
```
<a name="ml_00_30"></a>
### 1.3 项目简介 <a href="#ml_">↑</a>
```
1. kob-admin-console 作业管理平台。
2. kob-admin-core 作业管理底层代码实现模块，以jar包引入方式被kob-admin-console、kob-admin-processor依赖。
3. kob-admin-processor 作业调度执行器。
4. kob-basic 服务端（ kob-admin-* ）、客户端（ kob-client-* ）公用jar包。
5. kob-client-demo-spring-boot 客户端springboot的一种接入形式。
6. kob-client-parent 作业调度客户端项目依赖jar包。
	① kob-client-spring 客户端spring项目依赖jar，会被kob-client-spring-boot-starter依赖。
	② kob-client-spring-boot-starter 客户端spring-boot项目依赖jar。
7. table.sql 以集群名称是incubating，生成的sql文件
```

<a name="ml_00_50"></a>
### 2.5 快速开始 <a href="#ml_">↑</a>
<a name="ml_00_50_00"></a>
#### 2.5.1 视频 <a href="#ml_">↑</a>
<a name="ml_00_50_10"></a>
#### 2.5.2 文本 <a href="#ml_">↑</a>
<a name="ml_00_50_10_00"></a>
##### 2.5.2.1 Maven install <a href="#ml_">↑</a>
```
mvn clean install -DskipTests
```
<a name="ml_00_50_10_10"></a>
##### 2.5.2.2 pom依赖 <a href="#ml_">↑</a>
```
<dependency>
	<groupId>com.ke</groupId>
	<artifactId>kob-client-spring-boot-starter</artifactId>
</dependency>
```
<a name="ml_00_50_10_20"></a>
##### 2.5.2.3 创建项目 <a href="#ml_">↑</a>
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/project_access.png)
<a name="ml_00_50_10_30"></a>
##### 2.5.2.4 配置文件 <a href="#ml_">↑</a>
```
kob.client:
  cluster: incubator
  project_code: kob_monitor
  zk_servers: localhost:2801
  admin_url: http://localhost:8668
```
<a name="ml_00_50_10_40"></a>
##### 2.5.2.5 编写任务 <a href="#ml_">↑</a>
```
@Task(key = "hello", remark = "你好")
public TaskResult helloWorld(TaskBaseContext context){
    System.out.println(JSON.toJSONString(context));
    return TaskResult.success();
}
```
<a name="ml_00_50_10_50"></a>
##### 2.5.2.6 配置作业 <a href="#ml_">↑</a>
![cache redis](https://raw.githubusercontent.com/zhaoyuguang/test/master/job_init.png)


<a name="ml_00_60"></a>
### 2.6 联系我们 <a href="#ml_">↑</a>
```
交流群：
```
<a name="ml_00_70"></a>
### 2.7 如何贡献 <a href="#ml_">↑</a>
```
1. 根据开发计划 提交pr
2. 解决issue
```
<a name="ml_00_80"></a>
### 2.8 报告问题 <a href="#ml_">↑</a>
```
问题建议叙述
1. 环境（os，jre，kob verison ...）
2. 问题描述（如何操作、现象、日志 ...）
3. 是否可以稳定复现
```

<a name="ml_00_90"></a>
### 2.9 许可 <a href="#ml_">↑</a>
kob is licensed under the Apache 2.0 License. (http://www.apache.org/licenses/LICENSE-2.0.txt)

