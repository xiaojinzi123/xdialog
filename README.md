# xdialog

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

## version 1.0

	dependencies {
	        compile 'com.github.xiaojinzi123:xdialog:1.0'
	}
### 用法

弹出一个不可关闭加载框框:
XDialog.show(this, "tag1");

弹出一个可关闭加载框框:
XDialog.show(this, "tag2",true);

当连续调用这两句,表示这个加载框关联了两个tag,需要调用两次关闭才能关闭
XDialog.show(this, "tag1");
XDialog.show(this, "tag2",true);

XDialog.close("tag1");//代码走到这里用户点击空白处可关闭加载框
XDialog.close("tag2");

在调用了XDialog.close("tag1");这句代码之后,加载框就只有跟tag2有关联了,而tag2又是可以被关闭的,那么用户点击加载框的外部就可关闭
总结:一个加载框可以关联多个tag,如果关联的tag有一个是不允许用户点击空白处关闭的,那么用户点击空白处就不会被关闭
注意:点击返回键可以立刻关闭这个加载框,并且会清除这个加载框关联的所有tag
