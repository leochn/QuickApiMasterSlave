# QuickApiMasterSlave

读写分离代码生成器件,只要修改application.properties中连接数据库的配置信息.

在DataSourceConfig中,根据需要自定义选择数据源.

运行代码生成函数CodeGenetator.main()之前,删除xxx.controller, xxx.service(BaseService.java除外), xxx.pojo, xxx.mapper 包下面的类,同时删除src/main/resources/mapper 下的.xml文件.
