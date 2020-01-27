# 在线评测系统
作者 ZJNU **金明熠**

- 基于Springboot开发，前端jQuery、Vuejs
- 判题系统使用青岛大学OJ的[Judger](https://github.com/QingdaoU/Judger "Judger")
- 多语言支持: `C`, `C++`, `Java`, `Python2`, `Python3`，etc..
- Markdown & KaTex 支持(基于 [Editor.md](https://github.com/pandao/editor.md)) 

## 运行环境及依赖：
- 服务器系统Linux
- jdk8

## 部署：
打包成jar包，运行即可，配置文件目录应与jar包在一个目录下，配置文件为 yml
#### 例如：
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

## 浏览器支持：
Chrome Firefox Edge

## 功能简介：
### 题目分类功能：
为每道题目增加标签识，可以通过对进行分析来筛选难易程度以及内容进行分类
### 题目题解功能：
为每道题目设置解页面在这里可以学习或分享做题想法和代码
### 群组功能：
方便老师管理学生
### 积分排名：
每个题目都有对应分数
### 用户能力评价模型：
根据通过题目评价用户学习状况

## 许可

The [MIT](http://opensource.org/licenses/MIT) License

