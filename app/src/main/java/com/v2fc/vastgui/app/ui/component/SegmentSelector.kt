package com.v2fc.vastgui.app.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ave.vastgui.tools.utils.DensityUtils
import kotlinx.coroutines.launch

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/2

// 开关状态示例
private val states = listOf("关闭", "打开")

// 颜色示例
private val colors = listOf("primary", "error", "warning", "success")

// 尺寸示例
private val dimensions = listOf("40", "60", "80")

// 加载状态示例
private val loadingStates = listOf("是", "否")

// 数字多级选择
private val numbers = listOf("1", "2", "3", "4", "5")

/**
 * 缩放背景分段选择器示例
 */
@Preview(showBackground = true)
@Composable
fun SegmentScaleSelectorPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("状态")
        SegmentScaleSelector(states)
        Text("颜色")
        SegmentScaleSelector(colors)
        Text("尺寸(单位40px)")
        SegmentScaleSelector(dimensions)
        Text("加载中")
        SegmentScaleSelector(loadingStates)
        Text("最多五个")
        SegmentScaleSelector(numbers)
    }
}

/**
 * 滑动背景分段选择器示例
 */
@Preview(showBackground = true)
@Composable
fun SegmentSelectorSlidePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("状态")
        SegmentSlideSelector(states)
        Text("颜色")
        SegmentSlideSelector(colors)
        Text("尺寸(单位40px)")
        SegmentSlideSelector(dimensions)
        Text("加载中")
        SegmentSlideSelector(loadingStates)
        Text("最多五个")
        SegmentSlideSelector(numbers)
    }
}

@Composable
fun SegmentSlideSelector(selectors: List<String> = listOf()) {
    var selectedItem by remember { mutableIntStateOf(0) }
    var size by remember { mutableStateOf(Offset.Zero) }
    var topLeft by remember { mutableStateOf(Offset.Zero) }
    val leftAnimation = remember { Animatable(0f) }
    val animationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessVeryLow
    )
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0XFFEEEEF0))
            .drawWithContent {
                drawRoundRect(
                    color = Color(0xff2ecc71),
                    topLeft = Offset(leftAnimation.value, topLeft.y),
                    size = Size(size.x, size.y),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawContent()
            },
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        selectors.forEachIndexed { index, item ->
            SegmentItem(
                onSizeChanged = { rectSize, rectCenter ->
                    size = rectSize
                    if (selectedItem == index) {
                        topLeft = Offset(
                            rectCenter.x - rectSize.x / 2f,
                            rectCenter.y - rectSize.y / 2f
                        )
                    }
                    if (leftAnimation.value == 0f) {
                        scope.launch {
                            leftAnimation.snapTo(topLeft.x)
                        }
                    }
                },
                onValueChanged = { rectSize, rectCenter ->
                    selectedItem = index
                    scope.launch {
                        leftAnimation.animateTo(rectCenter.x - rectSize.x / 2f, animationSpec)
                    }
                }
            ) {
                val fontWeight by animateIntAsState(
                    targetValue = if (selectedItem == index) FontWeight.Black.weight else FontWeight.Normal.weight,
                    animationSpec = tween(),
                    label = "FontWeight animation"
                )
                Text(
                    text = item,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontWeight = FontWeight(fontWeight)
                )
            }
        }
    }
}

@Composable
fun SegmentScaleSelector(selectors: List<String> = listOf()) {
    var selectedItem by remember { mutableIntStateOf(0) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0XFFEEEEF0)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        selectors.forEachIndexed { index, item ->
            SegmentItem(
                selected = selectedItem == index,
                onValueChanged = {
                    selectedItem = index
                }
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontWeight = if (selectedItem == index) FontWeight.Black else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * 带有缩放背景的分段选择项。
 *
 * @param selected 用于判断该项是否被选中。
 * @param onValueChanged 当项目被选中时的回调。
 * @param content 用于展示选项内部内容。
 */
@Composable
fun RowScope.SegmentItem(
    selected: Boolean = false,
    onValueChanged: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var size by remember { mutableStateOf(Offset(0f, 0f)) }
    var cornerRadius by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = Modifier
            .padding(5.dp)
            .weight(1f)
            .wrapContentHeight()
            .onSizeChanged {
                size = Offset(it.width.toFloat(), it.height.toFloat())
                cornerRadius = it.width.coerceAtMost(it.height) / 6f
            }
            .pointerInput("click") {
                detectTapGestures(onTap = {
                    onValueChanged()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        this@SegmentItem.AnimatedVisibility(
            visible = selected,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .size(DensityUtils.px2dp(size.x).dp, DensityUtils.px2dp(size.y).dp)
                    .clip(RoundedCornerShape(CornerSize(cornerRadius)))
                    .background(Color.White)
            )
        }
        content()
    }
}

/**
 * 无背景的分段选择项。
 *
 * @param onSizeChanged 当选项确定位置后的回调，向外传递选项的大小
 * 和偏移值以便确定选中背景的初始位置。
 * @param onValueChanged 当项目被选中时的回调。
 * @param content 用于展示选项内部内容。
 */
@Composable
fun RowScope.SegmentItem(
    onSizeChanged: (Offset, Offset) -> Unit,
    onValueChanged: (Offset, Offset) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var size by remember { mutableStateOf(Offset.Zero) }
    var position by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = Modifier
            .padding(5.dp)
            .weight(1f)
            .wrapContentHeight()
            .onSizeChanged {
                size = Offset(it.width.toFloat(), it.height.toFloat())
            }
            .onGloballyPositioned { coordinates ->
                position = Offset(
                    coordinates.boundsInParent().center.x,
                    coordinates.boundsInParent().center.y
                )
                onSizeChanged(size, position)
            }
            .pointerInput("click") {
                detectTapGestures(
                    onTap = {
                        onValueChanged(size, position)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) { content() }
}