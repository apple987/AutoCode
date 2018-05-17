# AutoCode是一款简单的代码生成工具，可以快速生成前后台代码，欢迎大家提出宝贵意见！。<br>
**工具特点:**<br>
> * 结构简单，代码清晰。<br>
> * 配置灵活，扩展方便。<br>
> * 轻量便捷，模板配置。<br>

**项目结构**
```
AutoCode<br>
│ 
├─com.qdone.radmodel <br>
│  ├─ControllerBuilder 构建controller代码<br>
│  ├─ServiceBuilder 构建service代码<br>
│  ├─IBatisBuilder 构建mybatis代码<br>
│  ├─EntityBuilder 构建数据库实体类<br>
│  ├─SelectJspBuilder 构建列表页面代码<br>
│  ├─UpdateJspBuilder 构建更新数据页面代码<br>
│  └─InsertJspBuilder 构建添加数据页面代码<br>
│ 
├─TestRadModel 项目启动类<br>
│  
├──resources <br>
│  ├─config.properties 数据库配置 <br>
│  └─template <br>
│       ├─controllerTemplate.txt 构建controller模板<br>
│       ├─serviceTemplate.txt 构建service模板<br>
│       ├─serviceImplTemplate.txt 构建service模板<br>
│       ├─serviceImplTemplate.txt 构建service模板<br>
│       ├─selectHeader.txt 构建列表页头部信息模板<br>
│       ├─selectFooter.txt 构建列表页底部信息模板<br>
│       ├─insertHeader.txt 构建添加数据页面模板<br>
│       └─updateHeader.txt 构建更新数据页面代码<br>
│ 
```
**本地部署**<br>
- 通过git下载源码。<br>
- 创建本地数据库(mysql/oracle)。<br>
- 配置config.properties文件，指定生成表，数据库信息等。<br>
- Eclipse、IDEA运行TestRadModel.java，则可启动项目。<br>
- 代码生成完毕，项目路径会自动创建output文件夹，包含src和view分别对应后台代码和页面部分。<br>
- 拷贝src和view代码部分到开发框架即可。<br>
<br>
**注意事项**<br>
- 代码生成器，目前仅支持mysql和oracle。<br>
- 代码生成器，目前只是单表的生成版本。<br>
- 使用代码生成器之前，必须先创建表，并且指定主键，推荐填写表字段和表的备注(方便自动生成页面)。<br>
- 源码中保留有其他模板扩展功能，如果需要请自行参考ControllerBuilder配置，开启自定义模板设置。<br>
<br>
**用户反馈：**<br>
- Git仓库：https://github.com/apple987/AutoCode <br>
- 邮箱地址: m15171479289@163.com <br>
