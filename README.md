# WK_Kchart

#### 介绍
k线图，实现了柔滑缩放，滑动加载更多。长按选中等交互，包含蜡烛图，分时图，交易量图，深度图，MACD，RSI，KDJ，BOLL图表，也包含MA平均指标。新增指标自定义功能！，新增SAR指标支持！,支持图表模组折叠/并列展示模式!

 ![输入图片说明](https://images.gitee.com/uploads/images/2018/1222/215257_09148c33_1444166.jpeg "demo.jpg")。![输入图片说明](https://images.gitee.com/uploads/images/2019/0216/102726_b9c52513_1444166.jpeg "1031550283584_.pic_gaitubao_com_346x717.jpg")
![20241218-100242](https://github.com/user-attachments/assets/0bcac3a7-06a8-483c-b1cf-d45ecb74499e)


    /**
     * 各个视图模块的配置信息有关属性
     */
    public float mainViewHeight = 400;//主图模块高度
    public float indexViewHeight = 180;//指标模块高度
    public float viewInterval = 0; // 各个视图模块间的间隔
    public float leftScrollOffset = 0;//X 轴方向的最小滚动值固定偏移量（左边）
    public float rightScrollOffset = 0;//X 轴方向的最大滚动值固定偏移量（右边）
    public int mainModuleLayoutType = ModuleLayoutType.OVERLAP;// 主图模块布局类型
    public int indexModuleLayoutType = ModuleLayoutType.SEPARATE;// 指标模块布局类型
    public DataDisplayType dataDisplayType = DataDisplayType.REAL_TIME;//指标模块数据显示类型

    /**
     * 共用的有关属性
     */
    public float pointSize = 6f; //数据点大小
    public float lineWidth = 3f; //线条宽度
    public int lineColor = 0x1Affffff; // 线条颜色
    public float labelSize = 26; // 标签字符大小
    public int labelColor = 0xff8c99a6; //标签字符颜色
    public boolean onSingleClickSelected = false;//是否可以单击选中
    public boolean enableLeftLoadMore = true;//启用左滑加载更多
    public boolean enableRightLoadMore = true;//启用右滑加载更多

    /**
     * 边框线有关属性
     */
    public float borderWidth = 3f; // 边框线宽度
    public int borderColor = 0x1Affffff; // 边框线颜色

    /**
     * 与 grid 标尺刻度有关属性
     */
    public int gridCount = 4; // grid 数量
    public float gridLabelMarginVertical = 0; // grid 标签垂直Margin
    public float gridScaleLineLength = 10; // grid 刻度线长度
    public LineStyle gridLineStyle = LineStyle.DOTTED;//grid 线条样式

    /**
     * 与 axis 标尺刻度有关属性
     */
    public float axisLabelMarginHorizontal = 16f;//axis 标签水平Margin
    public float axisLabelMarginVertical = 8f;//axis 标签垂直Margin
    public float axisScaleLineLength = 10; //axis 刻度线长度
    public boolean axisShowFirst = true;//axis 是否显示第一条
    public boolean axisShowLast = true;//axis 是否显示最后一条
    public int axisLabelPosition = PositionType.END | PositionType.TOP; //axis 标签位置
    public LineStyle axisLineStyle = LineStyle.SOLID;//axis 线条样式

    /**
     * 与高亮线有关的属性
     */
    public boolean axisHighlightAutoWidth = false; // axis高亮线条自动宽度
    public boolean axisHighlightLabelAutoSelect = false; // axis高亮线条文字自动选择对应区域值
    public int axisHighlightColor = 0xff4d6370; // axis高亮线条颜色 0x33ffffff
    public boolean gridHighlightAutoWidth = false; // grid高亮线条自动宽度
    public int gridHighlightColor = 0xff4d6370; // grid高亮线条颜色 0x33ffffff
    public int highLightStyle = HighLightStyle.SOLID; // 高亮线条样式

    /**
     * 与MarkerView 有关的属性
     */
    public float markerRadius = 0; // MarkerView 边框圆角
    public float markerPaddingVertical = 5f; // MarkerView 垂直padding
    public float markerPaddingHorizontal = 10f; // MarkerView 水平padding
    public float markerBorderWidth = 3f; // MarkerView 边框宽度
    public int markerBorderColor = 0xff4d6370; // MarkerView 边框颜色
    public float markerTextSize = 26; // MarkerView 字符大小
    public int markerTextColor = 0xffffffff; // MarkerView 字符颜色
    public Paint.Style markerStyle = Paint.Style.FILL_AND_STROKE; //  MarkerView 的style（边框/边框和填充）
    public int axisMarkerPosition = PositionType.AUTO; // axis 轴 MarkerView 位置
    public int gridMarkerPosition = PositionType.BOTTOM | PositionType.OUTSIDE_VERTICAL;
    // grid 轴 MarkerView 位置

    /**
     * 与选择器有关的属性
     */
    public float selectorPadding = 16;//信息选择框的padding
    public float selectorMarginHorizontal = 16;//信息选择框的水平margin
    public float selectorMarginVertical = 40;//信息选择框的垂直margin
    public float selectorIntervalVertical = 16;//信息选择框的item垂直间隔
    public float selectorIntervalHorizontal = 50;//信息选择框的item水平间隔
    public float selectorRadius = 5f;//信息选择框的圆角度数
    public float selectorBorderWidth = 3f;//选择器边框线宽度
    public int selectorBorderColor = 0x55bdd9e6;//选择器边框线颜色
    public int selectorBackgroundColor = 0xE625383F;//选择器背景颜色
    public int selectorLabelColor = 0xffbdd9e6;//选择器label颜色
    public int selectorValueColor = 0xffbdd9e6;//选择器value颜色
    public float selectorLabelSize = 26;//选择器label文字大小
    public float selectorValueSize = 26;//选择器value文字大小

    /**
     * 与指标文字有关的属性
     */
    public float indexTextSize = 26f;//指标文字大小
    public float indexTextMarginHorizontal = 0f;//指标文字水平margin
    public float indexTextMarginVertical = 8f;//指标文字垂直margin
    public float indexTextInterval = 16f;//指标文字的间隔
    public boolean indexDefaultShowLastItemInfo = true;//指标默认显示最后一条的数据
    public int indexLabelPosition =
            PositionType.START | PositionType.TOP | PositionType.OUTSIDE_VERTICAL;//指标文字的位置

    /**
     * 与游标指示器有关的属性
     */
    public int cursorBackgroundColor = 0xFF061520;//游标文字容器背景颜色
    public int foldedCursorLineColor = 0xff00efff;//（折叠时）游标线颜色
    public int foldedCursorTextColor = 0xff00efff;//（折叠时）游标值颜色
    public int spreadCursorLineColor = 0xff00efff;//（展开时）游标线颜色
    public int spreadCursorTextColor = 0xff00efff;//（展开时）游标值颜色
    public int spreadCursorBorderColor = 0xff00efff;//（展开时）游标值容器边框颜色
    public float spreadCursorBorderWidth = 3;//（展开时）游标文字容器边框宽度
    public float spreadCursorRadius = 10;//（展开时）游标文字容器圆角
    public float spreadCursorPaddingHorizontal = 10f;//（展开时）游标文字水平Padding
    public float spreadCursorPaddingVertical = 6f;//（展开时）游标文字垂直Padding
    public float spreadTriangleWidth = 10;//（展开时）游标三角宽度
    public float spreadTriangleHeight = 10;//（展开时）游标三角高度

    /**
     * 涨跌有关的属性
     */
    public int increasingColor = 0xffB5FC00; // 上涨颜色（亮色）
    public int decreasingColor = 0xffFE0D5E; // 下跌颜色（亮色）
    public float darkColorAlpha = 1f; // （暗色）透明度（基于涨跌色配合透明度来实现暗色）
    public float shaderBeginColorAlpha = 0.35f;// 阴影开始颜色的透明度
    public float shaderEndColorAlpha = 0.05f;// 阴影结束颜色的透明度
    public Paint.Style increasingStyle = Paint.Style.FILL; // 上涨蜡烛图填充样式。默认实心
    public Paint.Style decreasingStyle = Paint.Style.STROKE; // 下跌蜡烛图填充样式，默认空心

    /**
     * 缩放有关的属性
     */
    public boolean canScroll = true;// 能否滚动
    public float pointBorderWidth = 3f; // 数据点矩形边框线宽度
    public float pointSpace = 8f;//数据点间隔
    public float pointWidth = 28f;//数据点初始宽度（缩放都将以此宽度为基准）
    public float visibleCount = 20; // 竖屏状态下的默认缩放倍数下显示多少个蜡烛图。注：横屏时会自动根据视图宽高变化比例计算，不需要手工设置
    public float maxScale = 10;// 最多放大倍数
    public float minScale = 6;// 最多缩小倍数(缩小后的宽度不会小于 蜡烛图矩形边框线宽度（candleBorderWidth）)
    public float currentScale = 1;// 当前缩放倍数

    /**
     * 极值Label有关属性
     */
    public float extremumLabelMarginHorizontal = 16f;//极值Label的水平边距
    public float extremumLabelMarginVertical = 8f;//极值Label的垂直边距
    public int extremumLabelPosition = PositionType.END | PositionType.OUTSIDE_VERTICAL;
    // 极值Label的位置

    /**
     * 极值Tag有关属性
     */
    public float candleExtremumLabelSize = 26; // 极值字符大小
    public int candleExtremumLableColor = 0xffffffff; // 极值字符颜色
    public Drawable extremumTagDrawable = null;//极值标签Drawable
    public float extremumTagDrawableWidth = 0;//极值标签Drawable宽度
    public float extremumTagDrawableHeight = 0;//极值标签Drawable高度
    public float extremumTagDrawableMarginHorizontal = 10;//极值标签Drawable水平margin
    public int extremumTagDrawableVisible = ExtremumVisible.MAX_VISIBLE;//极值标签的Drawable显示模式

    /**
     * 与指标有关的属性
     */
    public int centerLineColor = 0x1Affffff; // 视图中心线颜色
    public int indexTagColor = 0xffFF9F00; // 指标Tag颜色

    /**
     * 与水印有关的属性
     */
    public float waterMarkingWidth = 0;//水印宽度
    public float waterMarkingHeight = 0;//水印高度
    public float waterMarkingMarginHorizontal = 0;//水印水平margin
    public float waterMarkingMarginVertical = 0;//水印垂直margin
    public Drawable waterMarkingDrawable = null;//水印Drawable
    public int waterMarkingPosition = PositionType.START | PositionType.BOTTOM;//水印位置

    /**
     * 与呼吸灯有关的属性
     */
    public float breathingLampRadius = 5;//呼吸灯圆点半径
    public int breathingLampColor = 0xFFFFFFFF;//呼吸灯颜色
    public int breathingLampAutoTwinkleInterval = 1100;//呼吸灯自动闪烁时间（0为不自动闪烁）

    /**
     * 与标记点有关的属性
     */
    public float markerPointTextMarginVertical = 8;//标记点文字垂直边距
    public float markerPointTextMarginHorizontal = 12;//标记点文字水平边距
    public float markerPointMinMargin = 2;//标记点最小边距
    public float markerPointLineWidth = 2;//标记点连接线宽度
    public float markerPointLineDefaultLength = 20;//标记点连接线默认长度
    public float markerPointJointRadius = 8;//标记点接点半径（小圆点和小三角）
    public float markerPointJointMargin = 6;//标记点接点边距（小圆点距离K线柱的距离）
    public float markerPointTextSize = 20;//标记点文字大小
    public int markerPointTextColor = 0xFFFFFFFF;//标记点文字颜色
    public int markerPointColorB = 0xFFFF8100;//B标记点颜色
    public int markerPointColorS = 0xFF00ABFF;//S标记点颜色
    public int markerPointColorT = 0xFF27BCC4;//T标记点颜色

    /**
     * 与loading和error有关的属性
     */
    public float loadingTextSize = 26;
    public int loadingTextColor = 0xffffffff;
    public String loadingText = "Loading...";

    public float errorTextSize = 26;
    public int errorTextColor = 0xffffffff;
    public String errorText = "Empty";

    /**
     * 与分时图有关的属性
     */
    public float timeLineWidth = 3f; // 分时线宽度
    public int timeLineColor = 0xFF52649C; // 分时线颜色

     /**
     * 与深度图有关的属性
     */
    public float polylineWidth = 6f; // 折线宽度
    public float circleSize = 12f;// 圆点大小

    /**
     * 与深度图 grid 标尺刻度有关属性
     */
    public int depthGridStyle = DepthGridStyle.GAP_STYLE; // 深度图grid样式
