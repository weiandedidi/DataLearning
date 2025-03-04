# 部署指令
#### **步骤1 关键配置说明**

1. **JobManager内存配置**：
   - `jobmanager.memory.heap.size=1024m`：为JobManager的JVM堆内存分配1GB。
   - `jobmanager.memory.direct.size=512m`：为JobManager的直接内存分配512MB。
   - `taskmanager.memory.process.size=4096m`：为TaskManager的总进程内存分配4GB。
2. **TaskManager内存配置**：
   - `taskmanager.memory.process.size=4096m`：为TaskManager的总进程内存分配4GB。
   - `taskmanager.memory.heap.size=2048m`：为TaskManager的JVM堆内存分配2GB。
   - `taskmanager.memory.direct.size=1024m`：为TaskManager的直接内存分配1GB。
3. **并行度配置**：
   - `parallelism.default=2`：设置默认并行度为2，适合本地开发环境。
   - `taskmanager.numberOfTaskSlots=4`：为TaskManager分配4个任务插槽。
4. **资源限制**：
   - JobManager限制为2GB内存和1个CPU核心。
   - TaskManager限制为4GB内存和2个CPU核心。

#### 步骤2：验证`docker-compose.yml`文件

在终端中，进入包含`docker-compose.yml`文件的目录，运行以下命令验证配置文件的语法是否正确：

```bash
cd deployment/flink 

docker-compose -f docker-compose.yml config
```

如果配置正确，命令将输出解析后的配置信息，没有错误提示。

#### 步骤3：初始化必要的目录

确保所有必要的目录都已经创建：

```bash
mkdir -p checkpoints savepoints logs sql
```

#### 步骤4：停止并删除现有的容器

如果之前已经启动过容器，需要先停止并删除它们：

```bash
docker-compose down

bash复制
```



#### 步骤5：启动Flink集群

运行以下命令启动Flink集群：

```bash
docker-compose up -d
```

`-d`参数表示在后台启动容器。

#### 步骤6：验证容器运行状态

运行以下命令查看所有正在运行的容器：

```bash
docker ps
```

你应该看到`jobmanager`和`taskmanager`两个容器正在运行。

#### 步骤7：访问Flink Web UI

打开浏览器，访问`http://localhost:8081`，你应该能够看到Flink的Web UI界面，确认TaskManager已经注册到JobManager。

#### 步骤8：提交Flink SQL作业

在`sql/`目录下创建一个简单的Flink SQL脚本，例如`query.sql`，内容如下：

```sql
CREATE TABLE my_table (
    id INT,
    name STRING
) WITH (
    'connector' = 'filesystem',
    'path' = './data',
    'format' = 'csv'
);

INSERT INTO my_table VALUES (1, 'Alice'), (2, 'Bob');

sql复制
```



然后，通过以下命令提交SQL作业：

```bash
docker exec -it jobmanager ./bin/sql-client.sh -f /opt/flink/sql/query.sql
```



#### 步骤9：查看日志

如果需要查看容器的日志，可以运行以下命令：

```bash
docker logs -f jobmanager
```



或者查看TaskManager的日志：

```bash
docker logs -f taskmanager
```



#### 步骤10：停止和删除容器

当你完成实验后，可以通过以下命令停止并删除所有容器：

```bash
docker-compose down
```



这将停止并删除所有由Docker Compose启动的容器，并清理相关网络和卷。