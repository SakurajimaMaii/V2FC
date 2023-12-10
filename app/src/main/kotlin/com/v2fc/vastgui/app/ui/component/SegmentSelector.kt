package com.v2fc.vastgui.app.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/2

// Switch example
private val states = listOf("关闭", "打开")

// Color example
private val colors = listOf("primary", "error", "warning", "success")

// Dimension example
private val dimensions = listOf("40", "60", "80")

// Loading example
private val loadingStates = listOf("是", "否")

// Number example
private val numbers = listOf("1", "2", "3", "4", "5")

/**
 * State of the [SegmentSelector] item.
 *
 * @property center The segment item center coordinate in [SegmentSelector] .
 * @property size The segment item size in [SegmentSelector] .
 */
@Stable
data class SegmentItemState internal constructor(
    internal val center: Offset = Offset.Zero,
    internal val size: Size = Size.Zero
) {
    /**
     * The left-top coordinate of the segment item, it is used
     * to determine the position of the active indicator.
     */
    internal val topLeft: Offset
        get() {
            val x = center.x - size.width / 2f
            val y = center.y - size.height / 2f
            return Offset(x, y)
        }
}

@Composable
fun rememberSegmentItemState() = remember {
    mutableStateOf(SegmentItemState())
}

/**
 * Example of SegmentSelector with zoomable active indicator.
 */
@Preview(showBackground = true)
@Composable
fun SegmentZoomSelectorPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("状态")
        SegmentZoomSelector(states)
        Text("颜色")
        SegmentZoomSelector(colors)
        Text("尺寸(单位40px)")
        SegmentZoomSelector(dimensions)
        Text("加载中")
        SegmentZoomSelector(loadingStates)
        Text("最多五个")
        SegmentZoomSelector(numbers)
    }
}

/**
 * Example of SegmentSelector with sliding active indicator.
 */
@Preview(showBackground = true)
@Composable
fun SegmentSlideSelectorPreview() {
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
fun SegmentZoomSelector(selectors: List<String> = listOf()) {
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
                modifier = Modifier.padding(5.dp),
                activeColor = Color(0xff2ecc71),
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                }
            ) {
                Text(text = item, modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

@Composable
fun SegmentSlideSelector(selectors: List<String> = listOf()) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val animationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessVeryLow
    )
    SegmentSelector(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0XFFEEEEF0)),
        animationSpec = animationSpec,
        activeColor = Color(0xff2ecc71)
    ) { state ->
        selectors.forEachIndexed { index, item ->
            SegmentItem(
                modifier = Modifier.padding(5.dp),
                onStateChanged = state,
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            ) {
                Text(text = item, modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

@Composable
fun SegmentSelector(
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    activeRadius: Dp = 8f.dp,
    animationSpec: AnimationSpec<Float> = spring(),
    content: @Composable RowScope.((SegmentItemState) -> Unit) -> Unit
) {
    val (state, onStateChanged) = rememberSegmentItemState()
    val leftAnimation = remember { Animatable(state.topLeft.x) }
    LaunchedEffect(state) {
        leftAnimation.animateTo(state.topLeft.x, animationSpec)
    }
    Row(
        modifier = modifier
            .drawWithContent {
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(leftAnimation.value, state.topLeft.y),
                    size = state.size,
                    cornerRadius = CornerRadius(activeRadius.toPx())
                )
                drawContent()
            },
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content(onStateChanged)
    }
}

@Composable
fun RowScope.SegmentItem(
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    selected: Boolean = false,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var size by remember { mutableStateOf(Offset(0f, 0f)) }
    var cornerRadius by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = modifier
            .weight(1f)
            .onSizeChanged {
                size = Offset(it.width.toFloat(), it.height.toFloat())
                cornerRadius = it.width.coerceAtMost(it.height) / 6f
            }
            .pointerInput("click") {
                detectTapGestures(onTap = {
                    onClick()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val width = with(density) { size.x.toDp() }
        val height = with(density) { size.y.toDp() }
        this@SegmentItem.AnimatedVisibility(
            visible = selected,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .size(width, height)
                    .clip(RoundedCornerShape(CornerSize(cornerRadius)))
                    .background(activeColor)
            )
        }
        content()
    }
}

/**
 * SegmentItem for [SegmentSelector] .
 *
 * @param onStateChanged Updates the segment item size
 * and offset values to position the active indicator.
 * @param selected Whether this item is selected.
 * @param onClick Called when this item is clicked.
 * @param content Item content.
 */
@Composable
fun RowScope.SegmentItem(
    onStateChanged: (SegmentItemState) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var segmentItemState by remember { mutableStateOf(SegmentItemState()) }
    Box(
        modifier = modifier
            .weight(1f)
            .onSizeChanged {
                val size = Size(it.width.toFloat(), it.height.toFloat())
                segmentItemState = segmentItemState.copy(size = size)
                onStateChanged(segmentItemState)
            }
            .onGloballyPositioned { coordinates ->
                val center = Offset(
                    coordinates.boundsInParent().center.x,
                    coordinates.boundsInParent().center.y
                )
                segmentItemState = segmentItemState.copy(center = center)
                if (selected) {
                    onStateChanged(segmentItemState)
                }
            }
            .pointerInput("click") {
                detectTapGestures(
                    onTap = {
                        onStateChanged(segmentItemState)
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) { content() }
}