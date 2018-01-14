# SlideDrawerHelper
垂直方向抽屉滑动效果封装工具库。采用建造者模式简化多种功能配置流程，一行代码搞定垂直抽屉滑动。

[![](https://jitpack.io/v/HuangGangHust/SlideDrawerHelper.svg)](https://jitpack.io/#HuangGangHust/SlideDrawerHelper)

[下载示例APK体验](https://github.com/HuangGangHust/SlideDrawerHelper/raw/master/SlideDrawerHelper-debug.apk)

## 功能

1. 一行代码搞定垂直抽屉滑动；
2. 支持上、下两段式垂直抽屉滑动；
3. 支持上、中、下三段式垂直抽屉滑动；
4. 支持点击布局自动滑动和手指拖动布局滑动；
5. 支持设置滑动联动动画；
6. 支持滑动布局初始化或滑动开始、结束以及进行过程中，进行用户所需的其他操作；
7. 支持不同高度阈值配置；
8. 支持设置初始化布局位置；
9. 支持滑动动画持续时间配置；
10. ……



## 依赖

1. 在项目的根目录的build.gradle文件中（注意：不是module的build.gradle文件）加入如下依赖：

   ```groovy
   allprojects {
   	repositories {
   		...
   		maven { url "https://jitpack.io" }
   	}
   }
   ```


2. 在module的build.gradle文件中加入如下依赖：

   ```groovy
   dependencies {
       compile 'com.github.HuangGangHust:SlideDrawerHelper:1.0.1'
   }
   ```

## 使用

1.    上、中、下三段式垂直抽屉滑动：

      调用 **SlideDrawerHelper.Builder** 的 `build()` 方法，创建 **SlideDrawerHelper** 实例即可。

      使用示例：
      ```java
      // dragLayout：滑动触发布局，可拖动或点击；
      // slideParentLayout：滑动总布局
      new SlideDrawerHelper.Builder(dragLayout, slideParentLayout).build();​
      ```

2.    上、下两段式垂直抽屉滑动：

         在调用 **SlideDrawerHelper.Builder** 的 `build()` 之前，调用 `removeMediumHeightState(true)` 方法。

         使用示例：

      ```java
         new SlideDrawerHelper.Builder(dragLayout, slideParentLayout)
           .removeMediumHeightState(true)// 移除中间高度状态
           .build();
      ```

3.    不同高度阈值配置：

         在调用 **SlideDrawerHelper.Builder** 的 `build()` 之前，调用 `slideThreshold(minHeight, mediumHeight, maxHeight)` 方法或 `slidePercentThreshold(minHeightPercent, mediumHeightPercent, maxHeightPercent)` 方法。默认滑动布局的minHeight、mediumHeight、maxHeight与屏幕高度的比例依次为：1/12，1/2，1。

         使用示例：

      ```java
         new SlideDrawerHelper.Builder(dragLayout, slideParentLayout)
           // 设置滑动上、中、下三段布局阈值高度
           .slideThreshold(160, 960, 1920)
           .build();
         或：
         new SlideDrawerHelper.Builder(dragLayout, slideParentLayout)
           // 设置滑动上、中、下三段布局阈值高度与屏幕高度的比例
           .slidePercentThreshold(0.1f, 0.5f, 1f)
           .build();
      ```



4.    设置初始化布局位置

      在调用 **SlideDrawerHelper.Builder** 的 `build()` 之前，调用 `initHeightState(@NonNull SlideParentHeight initHeightState)` 方法。

      使用示例：
      ```java
      new SlideDrawerHelper.Builder(dragLayout, slideParentLayout)
        // 设置滑动总布局初始化高度状态为最大高度
        .initHeightState(SlideDrawerHelper.SlideParentHeight.MAX_HEIGHT)
        .build();
      ```

5.    滑动动画持续时间(ms) 配置

         在调用 **SlideDrawerHelper.Builder** 的 `build()` 之前，调用 `animDuration(long animDuration)` 方法。

         使用示例：

      ```java
         new SlideDrawerHelper.Builder(dragLayout, slideParentLayout)
           // 设置滑动动画的执行时间(ms)
           .animDuration(200)
           .build();
      ```


6.    设置滑动联动动画

      若需要设置滑动联动动画，或滑动开始和结束的特殊操作等，可以使用 **SlideAnimAttacher** 或其子类，实现所需的对应方法，再调用 `setSlideDrawerListener(slideDrawerListener)` 。 （**SlideAnimAttacher** 为实现 **SlideDrawerListener** 部分常用方法的抽象类）

      使用示例：

      ```java
      mSlideDrawerHelper.setSlideDrawerListener(new SlideAnimAttacher() {
          @Override
          public Animator slideAttachAnim(int currentHeight, float targetHeight, long animDuration) {
            // 返回滑动联动动画即可
            return null;
          }
      });
      ```

      也可以：

      自行实现滑动抽屉监听接口 **SlideDrawerListener** ，并通过 **SlideDrawerHelper** 的 `setSlideDrawerListener(SlideDrawerListener slideDrawerListener)` 方法进行绑定。

      使用示例：拖动/滑动过程中 mTestView 控件透明度相应变化。
      ```java
      mSlideDrawerHelper.setSlideDrawerListener(new SlideDrawerListener() {
          @Override
          public void init(SlideDrawerHelper.SlideParentHeight initHeightState) {
            // SlideDrawerHelper 初始化回调
          }
        
          @Override
          public void onDragUpdate(int currentHeight, int moveDistanceY) {
              // 手指拖动布局滑动过程中回调
              if (currentHeight > mSlideDrawerHelper.getMinHeight()) {
                  mTestView.setAlpha((float) currentHeight / mSlideDrawerHelper.getMaxHeight());
              } else {
                  mTestView.setAlpha(0.1f);
              }
          }

          @Override
          public void onSlideUpdate(int currentHeight, float targetHeight, ValueAnimator animation) {
              // 松口手指后，布局自动滑动过程中回调
              long currentPlayTime = animation.getCurrentPlayTime();
              long duration = animation.getDuration();
              Log.d(TAG, "滑动布局目标高度：" + targetHeight
                      + "，滑动布局当前高度：" + currentHeight
                      + "，动画总时间(ms)：" + duration
                      + "，已执行时间(ms)：" + currentPlayTime
                      + "，动画执行进度：" + (float) currentPlayTime / duration);
          }
        
          @Override
          public Animator slideAttachAnim(int currentHeight, float targetHeight, long animDuration) {
              // 布局自动滑动过程中，联动执行的动画（该方法返回的Animator）
              if (targetHeight > mSlideDrawerHelper.getMediumHeight()) {
                  return ObjectAnimator.ofFloat(mTestView, "alpha", 1f);
              }
              if (targetHeight > mSlideDrawerHelper.getMinHeight()) {
                  return ObjectAnimator.ofFloat(mTestView, "alpha", 0.5f);
              }
              return ObjectAnimator.ofFloat(mTestView, "alpha", 0.1f);
          }
      });
      ```
      ​

7.    滑动布局初始化或滑动过程中，进行用户所需的其他操作

         使用方法类同第6条。





## 欢迎Star，欢迎反馈问题：huangganghust@qq.com
