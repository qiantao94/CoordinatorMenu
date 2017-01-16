# QQ侧滑菜单
> [[详情介绍]](https://gold.xitu.io/post/5875c01361ff4b006d592699)

### 效果
![](https://github.com/bestTao/CoordinateMenu/blob/master/demo.gif)<br/>
### 使用
1. 先添加以下代码到你项目中的根目录的`build.gradle`

   ```x
   allprojects {
   		repositories {
   			...
   			maven { url 'https://jitpack.io' }
   		}
   }
   ```

   ​


2. 再引入依赖：

   ```xml
   dependencies {
   	        compile 'com.github.bestTao:CoordinatorMenu:v1.0.2'
   }
   ```

   ​


3. 最后在layout中类似这样使用：

   ```xml
   <com.qiantao.coordinatormenu.CoordinatorMenu
           android:layout_width="match_parent"
           android:layout_height="match_parent">

           <LinearLayout
               android:id="@+id/menu"
           	android:layout_width="match_parent"
               android:layout_height="match_parent">
           </LinearLayout>

           <com.qiantao.coordinatormenu.MainView
   			android:id="@+id/main"
       		android:layout_width="match_parent"
       		android:layout_height="match_parent">
     		</com.qiantao.coordinatormenu.MainView>
     
   </com.qiantao.coordinatormenu.CoordinatorMenu>
   ```

   > `CoordinatorMenu`的两个子view务必设置控件id
