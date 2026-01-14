# WK_Kchart

#### 介绍
k线图，实现了柔滑缩放，滑动加载更多。长按选中等交互，包含蜡烛图，分时图，交易量图，深度图，MACD，RSI，KDJ，BOLL图表，也包含MA平均指标。新增指标自定义功能！，新增EMA,SAR指标支持！,支持图表模组折叠/并列展示模式!

![输入图片说明](https://images.gitee.com/uploads/images/2018/1222/215257_09148c33_1444166.jpeg "demo.jpg")。![输入图片说明](https://images.gitee.com/uploads/images/2019/0216/102726_b9c52513_1444166.jpeg "1031550283584_.pic_gaitubao_com_346x717.jpg")
![20241218-100242](https://github.com/user-attachments/assets/0bcac3a7-06a8-483c-b1cf-d45ecb74499e)

## BaseAttribute

| Attribute Name                      | Default Value               | Description                                        |
|-------------------------------------|-----------------------------|----------------------------------------------------|
| mainViewHeight                      | 400                         | 主图模块高度                                             |
| indexViewHeight                     | 180                         | 指标模块高度                                             |
| viewInterval                        | 0                           | 各个视图模块间的间隔                                         |
| leftScrollOffset                    | 0                           | X 轴方向的最小滚动值固定偏移量（左边）                               |
| rightScrollOffset                   | 0                           | X 轴方向的最大滚动值固定偏移量（右边）                               |
| mainModuleLayoutType                | ModuleLayoutType.OVERLAP    | 主图模块布局类型                                           |
| indexModuleLayoutType               | ModuleLayoutType.SEPARATE   | 指标模块布局类型                                           |
| dataDisplayType                     | DataDisplayType.REAL_TIME   | 指标模块数据显示类型                                         |
| pointSize                           | 6f                          | 数据点大小                                              |
| lineWidth                           | 3f                          | 线条宽度                                               |
| lineColor                           | 0x1Affffff                  | 线条颜色                                               |
| labelSize                           | 26                          | 标签字符大小                                             |
| labelColor                          | 0xff8c99a6                  | 标签字符颜色                                             |
| onSingleClickSelected               | false                       | 是否可以单击选中                                           |
| enableLeftLoadMore                  | true                        | 启用左滑加载更多                                           |
| enableRightLoadMore                 | true                        | 启用右滑加载更多                                           |
| borderWidth                         | 3f                          | 边框线宽度                                              |
| borderColor                         | 0x1Affffff                  | 边框线颜色                                              |
| gridCount                           | 4                           | grid 数量                                            |
| gridLabelMarginVertical             | 0                           | grid 标签垂直Margin                                    |
| gridScaleLineLength                 | 10                          | grid 刻度线长度                                         |
| gridLineStyle                       | LineStyle.DOTTED            | grid 线条样式                                          |
| axisLabelMarginHorizontal           | 16f                         | axis 标签水平Margin                                    |
| axisLabelMarginVertical             | 8f                          | axis 标签垂直Margin                                    |
| axisScaleLineLength                 | 10                          | axis 刻度线长度                                         |
| axisShowFirst                       | true                        | axis 是否显示第一条                                       |
| axisShowLast                        | true                        | axis 是否显示最后一条                                      |
| axisLabelPosition                   | PositionType.END            | PositionType.TOP                                   | axis 标签位置 |
| axisLineStyle                       | LineStyle.SOLID             | axis 线条样式                                          |
| axisHighlightAutoWidth              | false                       | axis高亮线条自动宽度                                       |
| axisHighlightLabelAutoSelect        | false                       | axis高亮线条文字自动选择对应区域值                                |
| axisHighlightColor                  | 0xff4d6370                  | axis高亮线条颜色 0x33ffffff                              |
| gridHighlightAutoWidth              | false                       | grid高亮线条自动宽度                                       |
| gridHighlightColor                  | 0xff4d6370                  | grid高亮线条颜色 0x33ffffff                              |
| highLightStyle                      | HighLightStyle.SOLID        | 高亮线条样式                                             |
| markerRadius                        | 0                           | MarkerView 边框圆角                                    |
| markerPaddingVertical               | 5f                          | MarkerView 垂直padding                               |
| markerPaddingHorizontal             | 10f                         | MarkerView 水平padding                               |
| markerBorderWidth                   | 3f                          | MarkerView 边框宽度                                    |
| markerBorderColor                   | 0xff4d6370                  | MarkerView 边框颜色                                    |
| markerTextSize                      | 26                          | MarkerView 字符大小                                    |
| markerTextColor                     | 0xffffffff                  | MarkerView 字符颜色                                    |
| markerStyle                         | Paint.Style.FILL_AND_STROKE | MarkerView 的style（边框/边框和填充）                        |
| axisMarkerPosition                  | PositionType.AUTO           | axis 轴 MarkerView 位置                               |
| gridMarkerPosition                  | PositionType.BOTTOM         | PositionType.OUTSIDE_VERTICAL                      | grid 轴 MarkerView 位置 |
| selectorPadding                     | 16                          | 信息选择框的padding                                      |
| selectorMarginHorizontal            | 16                          | 信息选择框的水平margin                                     |
| selectorMarginVertical              | 40                          | 信息选择框的垂直margin                                     |
| selectorIntervalVertical            | 16                          | 信息选择框的item垂直间隔                                     |
| selectorIntervalHorizontal          | 50                          | 信息选择框的item水平间隔                                     |
| selectorRadius                      | 5f                          | 信息选择框的圆角度数                                         |
| selectorBorderWidth                 | 3f                          | 选择器边框线宽度                                           |
| selectorBorderColor                 | 0x55bdd9e6                  | 选择器边框线颜色                                           |
| selectorBackgroundColor             | 0xE625383F                  | 选择器背景颜色                                            |
| selectorLabelColor                  | 0xffbdd9e6                  | 选择器label颜色                                         |
| selectorValueColor                  | 0xffbdd9e6                  | 选择器value颜色                                         |
| selectorLabelSize                   | 26                          | 选择器label文字大小                                       |
| selectorValueSize                   | 26                          | 选择器value文字大小                                       |
| indexTextSize                       | 26f                         | 指标文字大小                                             |
| indexTextMarginHorizontal           | 0f                          | 指标文字水平margin                                       |
| indexTextMarginVertical             | 8f                          | 指标文字垂直margin                                       |
| indexTextInterval                   | 16f                         | 指标文字的间隔                                            |
| indexDefaultShowLastItemInfo        | true                        | 指标默认显示最后一条的数据                                      |
| indexLabelPosition                  | PositionType.START          | PositionType.TOP                                   | PositionType.OUTSIDE_VERTICAL | 指标文字的位置 |
| cursorBackgroundColor               | 0xFF061520                  | 游标文字容器背景颜色                                         |
| foldedCursorLineColor               | 0xff00efff                  | （折叠时）游标线颜色                                         |
| foldedCursorTextColor               | 0xff00efff                  | （折叠时）游标值颜色                                         |
| spreadCursorLineColor               | 0xff00efff                  | （展开时）游标线颜色                                         |
| spreadCursorTextColor               | 0xff00efff                  | （展开时）游标值颜色                                         |
| spreadCursorBorderColor             | 0xff00efff                  | （展开时）游标值容器边框颜色                                     |
| spreadCursorBorderWidth             | 3                           | （展开时）游标文字容器边框宽度                                    |
| spreadCursorRadius                  | 10                          | （展开时）游标文字容器圆角                                      |
| spreadCursorPaddingHorizontal       | 10f                         | （展开时）游标文字水平Padding                                 |
| spreadCursorPaddingVertical         | 6f                          | （展开时）游标文字垂直Padding                                 |
| spreadTriangleWidth                 | 10                          | （展开时）游标三角宽度                                        |
| spreadTriangleHeight                | 10                          | （展开时）游标三角高度                                        |
| increasingColor                     | 0xffB5FC00                  | 上涨颜色（亮色）                                           |
| decreasingColor                     | 0xffFE0D5E                  | 下跌颜色（亮色）                                           |
| darkColorAlpha                      | 1f                          | （暗色）透明度（基于涨跌色配合透明度来实现暗色）                           |
| shaderBeginColorAlpha               | 0.35f                       | 阴影开始颜色的透明度                                         |
| shaderEndColorAlpha                 | 0.05f                       | 阴影结束颜色的透明度                                         |
| increasingStyle                     | Paint.Style.FILL            | 上涨蜡烛图填充样式。默认实心                                     |
| decreasingStyle                     | Paint.Style.STROKE          | 下跌蜡烛图填充样式，默认空心                                     |
| canScroll                           | true                        | 能否滚动                                               |
| pointBorderWidth                    | 3f                          | 数据点矩形边框线宽度                                         |
| pointSpace                          | 8f                          | 数据点间隔                                              |
| pointWidth                          | 28f                         | 数据点初始宽度（缩放都将以此宽度为基准）                               |
| visibleCount                        | 20                          | 竖屏状态下的默认缩放倍数下显示多少个蜡烛图。注：横屏时会自动根据视图宽高变化比例计算，不需要手工设置 |
| maxScale                            | 10                          | 最多放大倍数                                             |
| minScale                            | 6                           | 最多缩小倍数(缩小后的宽度不会小于 蜡烛图矩形边框线宽度（candleBorderWidth）)   |
| currentScale                        | 1                           | 当前缩放倍数                                             |
| extremumLabelMarginHorizontal       | 16f                         | 极值Label的水平边距                                       |
| extremumLabelMarginVertical         | 8f                          | 极值Label的垂直边距                                       |
| extremumLabelPosition               | PositionType.END            | PositionType.OUTSIDE_VERTICAL                      | 极值Label的位置 |
| candleExtremumLabelSize             | 26                          | 极值字符大小                                             |
| candleExtremumLableColor            | 0xffffffff                  | 极值字符颜色                                             |
| extremumTagDrawable                 | null                        | 极值标签Drawable                                       |
| extremumTagDrawableWidth            | 0                           | 极值标签Drawable宽度                                     |
| extremumTagDrawableHeight           | 0                           | 极值标签Drawable高度                                     |
| extremumTagDrawableMarginHorizontal | 10                          | 极值标签Drawable水平margin                               |
| extremumTagDrawableVisible          | ExtremumVisible.MAX_VISIBLE | 极值标签的Drawable显示模式                                  |
| centerLineColor                     | 0x1Affffff                  | 视图中心线颜色                                            |
| indexTagColor                       | 0xffFF9F00                  | 指标Tag颜色                                            |
| waterMarkingWidth                   | 0                           | 水印宽度                                               |
| waterMarkingHeight                  | 0                           | 水印高度                                               |
| waterMarkingMarginHorizontal        | 0                           | 水印水平margin                                         |
| waterMarkingMarginVertical          | 0                           | 水印垂直margin                                         |
| waterMarkingDrawable                | null                        | 水印Drawable                                         |
| waterMarkingPosition                | PositionType.START          | PositionType.BOTTOM                                | 水印位置 |
| breathingLampRadius                 | 5                           | 呼吸灯圆点半径                                            |
| breathingLampColor                  | 0xFFFFFFFF                  | 呼吸灯颜色                                              |
| breathingLampAutoTwinkleInterval    | 1100                        | 呼吸灯自动闪烁时间（0为不自动闪烁）                                 |
| markerPointTextMarginVertical       | 8                           | 标记点文字垂直边距                                          |
| markerPointTextMarginHorizontal     | 12                          | 标记点文字水平边距                                          |
| markerPointMinMargin                | 2                           | 标记点最小边距                                            |
| markerPointLineWidth                | 2                           | 标记点连接线宽度                                           |
| markerPointLineDefaultLength        | 20                          | 标记点连接线默认长度                                         |
| markerPointJointRadius              | 8                           | 标记点接点半径（小圆点和小三角）                                   |
| markerPointJointMargin              | 6                           | 标记点接点边距（小圆点距离K线柱的距离）                               |
| markerPointTextSize                 | 20                          | 标记点文字大小                                            |
| markerPointTextColor                | 0xFFFFFFFF                  | 标记点文字颜色                                            |
| markerPointColorB                   | 0xFFFF8100                  | B标记点颜色                                             |
| markerPointColorS                   | 0xFF00ABFF                  | S标记点颜色                                             |
| markerPointColorT                   | 0xFF27BCC4                  | T标记点颜色                                             |
| loadingTextSize                     | 26                          | loading文字大小                                        |
| loadingTextColor                    | 0xffffffff                  | loading文字颜色                                        |
| loadingText                         | "Loading..."                | loading文字                                          |
| errorTextSize                       | 26                          | error文字大小                                          |
| errorTextColor                      | 0xffffffff                  | error文字颜色                                          |
| errorText                           | "Empty"                     | error文字                                            |


## CandleAttribute

| Attribute Name | Default Value | Description |
|----------------|---------------|-------------|
| timeLineWidth  | 3f            | 分时线宽度       |
| timeLineColor  | 0xFF52649C    | 分时线颜色       |


## DepthAttribute

| Attribute Name | Default Value            | Description |
|----------------|--------------------------|-------------|
| polylineWidth  | 6f                       | 折线宽度        |
| circleSize     | 12f                      | 圆点大小        |
| depthGridStyle | DepthGridStyle.GAP_STYLE | 深度图grid样式   |
