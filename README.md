# Legends 远程任务调度框架

使用 Java 开发的一个任务调度框架，可以远程执行一次性或重复性的JOB，查看任务的执行状态以及任务结果（返回值、执行时间等）
本框架只负责调度，支持集群，任务执行在目标的服务器上，与业务解耦。

演示地址:  http://legends.sunyi.io/

## 功能简介


### 服务器端-添加任务

可以添加一个远程任务：
* 在指定某个时间执行一次任务
* 指定一个 Cron 表达式循环的执行任务。
* 添加任务时，测试这个任务是否配置成功。


需要指定这个远程任务的*名字,分组，服务器地址，执行时间或Cron表达式，任务类全路径，参数*等等。
![img](https://github.com/tongbanjie/legends/blob/master/readme/AddJob.png)


### 服务器端-任务列表
查看配置了哪些任务，可以编辑修改这些任务，比如：*执行时间、参数*等等。
![img](https://github.com/tongbanjie/legends/blob/master/readme/JobList.png)



### 服务器端-任务执行记录
查看所有的任务执行记录，包括：*执行结果、任务耗时、返回信息、抛出的异常*等等。
![img](https://github.com/tongbanjie/legends/blob/master/readme/JobSnapshot.png)


### 其他
* 删除一个任务可以在历史记录里看到。
* 任务执行情况数据过多后，清理一周或一个月前的数据。
* 可以监控所有任务下次执行时间。
