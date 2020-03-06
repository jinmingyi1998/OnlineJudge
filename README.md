# 在线评测系统
作者 ZJNU **金明熠**

- 基于Springboot开发，前端jQuery、Vuejs
- [判题服务](https://github.com/jinmingyi1998/JudgerServer)基于青岛大学OJ的[Judger](https://github.com/QingdaoU/Judger "Judger")
- 多语言支持: `C`, `C++`, `Java`, `Python2`, `Python3`，etc..
- Markdown & KaTex 支持(基于 [Editor.md](https://github.com/pandao/editor.md)) 

## 运行环境及依赖：
- 服务器系统Linux
- jdk8+

## 部署：
### 源代码部署:
```sh
./gradlew bootJar
```
打包成jar包，运行`./build/lib/`目录下的jar即可`java -jar jar-name.jar`，配置文件目录应与jar包在一个目录下，配置文件为 yml
### Docker部署 (RECOMMENDED):
示例：
```
export DOCKER_IMG=registry.cn-hangzhou.aliyuncs.com/jinmingyi/onlinejudge #国外可使用jinmingyi1998/onlinejudge
docker pull $DOCKER_IMG
docker run -d --name onlinejudge \
    -p $PORT:8080 \ 
    -v $OJ_VOLUME:/onlinejudge \
    -e MYSQL_USER=$MYSQL_USER \
    -e MYSQL_PASSWORD=$MYSQL_PASSWORD \
    -e MYSQL_URL=db:3306/oj \
    -e REDIS_URL=redis
    -e JUDGER_SERVICE=http://judgerserver:12345/judge \
    $DOCKER_IMG
```
#### custom config example：
```yaml
spring:
  datasource:
    username: user
    password: pwd
    url: jdbc:mysql://localhost:3306/onlinejudge?useUnicode=yes&characterEncoding=UTF-8
    driver-class-name: com.mysql.jdbc.Driver
server:
  port: 8088
```
#### docker需要挂出来 /onlinejudge
/onlinejudge 目录下:
 - media/ 存放上传的图片
 - config/ 运行时配置，将覆盖默认配置
 - log/ 运行日志
 
#### 环境变量设置：
 - MYSQL_USER (default = root)
 - MYSQL_PASSWORD (default = 1234)
 - MYSQL_URL
 - REDIS_URL (default = localhost)
 - REDIS_PORT (default = 6379)
 - JUDGE_SERVICE (**one** link to submit codes)
 
## 浏览器支持：
Chrome ~~Firefox, Edge without Chromium, IE~~

## 功能简介：
### 题目分类功能：
为每道题目增加标签识，可以通过对进行分析来筛选难易程度以及内容进行分类
### 题目题解功能：
为每道题目设置解页面在这里可以学习或分享做题想法和代码
### 比赛
线上组织比赛
### 群组(Team)功能：
老师可创建Team，方便老师管理学生
### 积分排名：
每个题目都有对应分数
### 用户能力评价模型：
根据通过题目评价用户学习状况
### Forum
（高于一定分数限制的）用户可发布文章

## 许可
The [MIT](http://opensource.org/licenses/MIT) License