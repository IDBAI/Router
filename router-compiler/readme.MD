此 module 为 Java Library ，是注解字段解析支持库，定义的注解编译器，  
被APP module和其他业务module 以   
`annotationProcessor project(':router-compiler')`  
的方式引用。   
如何你的业务module 不需要使用注解方式解析字段，则可以不引用。

此库的代码不会打包到app中，它只是负责在编译时候，帮我们把扫描到的路由注解（@InjectParams）的字段进行生成对应的解析字段需要的java文件。  
