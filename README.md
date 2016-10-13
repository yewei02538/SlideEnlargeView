## Slide Enlarge View

看到华为mate8的锁屏，觉得很好玩，于是试着做了下，下面锁屏的效果

![](/SCREEN/20161013147634518057ff3d5c9ccec.gif)

图片质量不是很好，大家见谅

## 分析

* 当滑动屏保的时候整个都会放大
* 点击文字会响应事件

要想让所有的布局跟着放大，我想到是重写`onTouchEvent`判断所点击的`View`是不是ImageView 是的话调用`setScaleX()和setScaleY()`

下面是截图

![](/SCREEN/20161013147634509157ff3d033d684.gif)


