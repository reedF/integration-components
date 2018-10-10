#快速集成spring-boot and mybatis
参考：
https://github.com/abel533/Mapper/wiki/1.3-spring-boot
https://github.com/pagehelper/Mybatis-PageHelper

# 集成Mybatis及其组件
1.集成spring-boot-mybatis
2.集成Mybaits通用Mapper
3.集成Mybatis-generator,并使用通用Mapper生成code
4.集成Mybatis分页插件Pagehelper

# Mybatis-generator使用
方式1：直接运行mvn mybatis-generator:generate
方式2：IDE内Run AS——>Maven Build… ——>在Goals框中输入：mybatis-generator:generate
注意事项：
1.使用通用Mapper插件tk.mybatis.mapper.generator.MapperPlugin生成代码时，表名默认使用类名，
如表名命名不符合驼峰转下划线(只对大写字母进行处理，如TestUser默认对应的表名为test_user)规则时，不会生成@Table注解,需手动加入
2.可通过在配置文件内配置mapper.style,定义实体和表转换时的规则，默认驼峰转下划线
可选值为normal用实体名和字段名;camelhump是默认值，驼峰转下划线;uppercase转换为大写;lowercase转换为小写
3.通用Mapper的配置参数详见：
https://github.com/abel533/Mapper/wiki/3.config

# 热部署使用
方式1：引入依赖spring-boot-devtools
方式2：build时使用插件<artifactId>springloaded</artifactId>，并使用mvn spring-boot:run运行

