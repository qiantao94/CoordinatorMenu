# QQ侧滑菜单
> [[详情介绍]](https://gold.xitu.io/post/5875c01361ff4b006d592699)

### 效果
![](https://github.com/bestTao/CoordinateMenu/blob/master/demo.gif)<br/>
### 使用
1. 先添加以下代码到你项目中的根目录的`build.gradle`
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```
2. 再引入依赖即可：
```gradle
dependencies {
	        compile 'com.github.bestTao:CoordinatorMenu:v1.0.0'
}
