@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
  ExperimentalMaterial3Api::class
)
@file:Suppress("unused")

package com.example.dummystocks.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.dummystocks.ui.SheetValue.Expanded
import com.example.dummystocks.ui.SheetValue.Hidden
import com.example.dummystocks.ui.SheetValue.PartiallyExpanded
import com.example.dummystocks.ui.SheetValue.PartiallyHidden
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal val BottomSheetMaxWidth = 640.dp

enum class SheetValue {
  /**
   * The sheet is not visible.
   */
  Hidden,
  /**
   * The sheet is partially visible.
   */
  PartiallyHidden,

  /**
   * The sheet is visible at full height.
   */
  Expanded,

  /**
   * The sheet is partially visible.
   */
  PartiallyExpanded,
}

internal fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
  sheetState: SheetState,
  orientation: Orientation,
  onFling: (velocity: Float) -> Unit
): NestedScrollConnection = object : NestedScrollConnection {
  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    val delta = available.toFloat()
    return if (delta < 0 && source == NestedScrollSource.Drag) {
      sheetState.anchoredDraggableState.dispatchRawDelta(delta).toOffset()
    } else {
      Offset.Zero
    }
  }

  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource
  ): Offset {
    return if (source == NestedScrollSource.Drag) {
      sheetState.anchoredDraggableState.dispatchRawDelta(available.toFloat()).toOffset()
    } else {
      Offset.Zero
    }
  }

  override suspend fun onPreFling(available: Velocity): Velocity {
    val toFling = available.toFloat()
    val currentOffset = sheetState.requireOffset()
    val minAnchor = sheetState.anchoredDraggableState.anchors.minAnchor()
    return if (toFling < 0 && currentOffset > minAnchor) {
      onFling(toFling)
      // since we go to the anchor with tween settling, consume all for the best UX
      available
    } else {
      Velocity.Zero
    }
  }

  override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
    onFling(available.toFloat())
    return available
  }

  private fun Float.toOffset(): Offset = Offset(
    x = if (orientation == Horizontal) this else 0f,
    y = if (orientation == Vertical) this else 0f
  )

  @JvmName("velocityToFloat")
  private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

  @JvmName("offsetToFloat")
  private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}

/**
 * <a href="https://m3.material.io/components/bottom-sheets/overview" class="external" target="_blank">Material Design standard bottom sheet scaffold</a>.
 *
 * Standard bottom sheets co-exist with the screen’s main UI region and allow for simultaneously
 * viewing and interacting with both regions. They are commonly used to keep a feature or
 * secondary content visible on screen when content in main UI region is frequently scrolled or
 * panned.
 *
 * ![Bottom sheet image](https://developer.android.com/images/reference/androidx/compose/material3/bottom_sheet.png)
 *
 * This component provides API to put together several material components to construct your
 * screen, by ensuring proper layout strategy for them and collecting necessary data so these
 * components will work together correctly.
 *
 * A simple example of a standard bottom sheet looks like this:
 *
 * @sample androidx.compose.material3.samples.SimpleBottomSheetScaffoldSample
 *
 * @param sheetContent the content of the bottom sheet
 * @param modifier the [Modifier] to be applied to this scaffold
 * @param scaffoldState the state of the bottom sheet scaffold
 * @param sheetPartialExpandedHeight the height of the bottom sheet when it is collapsed
 * @param sheetShape the shape of the bottom sheet
 * @param sheetContainerColor the background color of the bottom sheet
 * @param sheetContentColor the preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetContainerColor], or if that is
 * not a color from the theme, this will keep the same content color set above the bottom sheet.
 * @param sheetTonalElevation the tonal elevation of the bottom sheet
 * @param sheetShadowElevation the shadow elevation of the bottom sheet
 * @param sheetDragHandle optional visual marker to pull the scaffold's bottom sheet
 * @param sheetSwipeEnabled whether the sheet swiping is enabled and should react to the user's
 * input
 * @param topBar top app bar of the screen, typically a [SmallTopAppBar]
 * @param snackbarHost component to host [Snackbar]s that are pushed to be shown via
 * [SnackbarHostState.showSnackbar], typically a [SnackbarHost]
 * @param containerColor the color used for the background of this scaffold. Use [Color.Transparent]
 * to have no color.
 * @param contentColor the preferred color for content inside this scaffold. Defaults to either the
 * matching content color for [containerColor], or to the current [LocalContentColor] if
 * [containerColor] is not a color from the theme.
 * @param content content of the screen. The lambda receives a [PaddingValues] that should be
 * applied to the content root via [Modifier.padding] and [Modifier.consumeWindowInsets] to
 * properly offset top and bottom bars. If using [Modifier.verticalScroll], apply this modifier to
 * the child of the scroll, and not on the scroll itself.
 */
@Composable
fun BottomSheetScaffold(
  sheetContent: @Composable ColumnScope.() -> Unit,
  modifier: Modifier = Modifier,
  scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
  sheetPartialExpandedHeight: Dp = BottomSheetDefaults.SheetPeekHeight,
  sheetPartialHiddenHeight: Dp = BottomSheetDefaults.SheetPeekHeight,
  sheetShape: Shape = BottomSheetDefaults.ExpandedShape,
  sheetContainerColor: Color = BottomSheetDefaults.ContainerColor,
  sheetContentColor: Color = contentColorFor(sheetContainerColor),
  sheetTonalElevation: Dp = BottomSheetDefaults.Elevation,
  sheetShadowElevation: Dp = BottomSheetDefaults.Elevation,
  sheetDragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
  sheetSwipeEnabled: Boolean = true,
  topBar: @Composable (() -> Unit)? = null,
  snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
  containerColor: Color = MaterialTheme.colorScheme.surface,
  contentColor: Color = contentColorFor(containerColor),
  content: @Composable (PaddingValues) -> Unit
) {
  val sheetPartialExpandedHeightPx = with(LocalDensity.current) {
    sheetPartialExpandedHeight.roundToPx()
  }
  val sheetPartialHiddenHeightPx = with(LocalDensity.current) {
    sheetPartialHiddenHeight.roundToPx()
  }
  BottomSheetScaffoldLayout(
    modifier = modifier,
    topBar = topBar,
    body = content,
    snackbarHost = {
      snackbarHost(scaffoldState.snackbarHostState)
    },
    sheetPartialExpandedHeight = sheetPartialExpandedHeight,
    sheetPartialHiddenHeight = sheetPartialHiddenHeight,
    sheetOffset = { scaffoldState.bottomSheetState.requireOffset() },
    sheetState = scaffoldState.bottomSheetState,
    containerColor = containerColor,
    contentColor = contentColor,
    bottomSheet = { layoutHeight ->
      StandardBottomSheet(
        state = scaffoldState.bottomSheetState,
        sheetPartialExpandedHeight = sheetPartialExpandedHeight,
        sheetPartialHiddenHeight = sheetPartialHiddenHeight,
        sheetSwipeEnabled = sheetSwipeEnabled,
        calculateAnchors = { sheetSize ->
          val sheetHeight = sheetSize.height
          DraggableAnchors {
            if (!scaffoldState.bottomSheetState.skipPartiallyExpanded) {
              PartiallyExpanded at (layoutHeight - sheetPartialExpandedHeightPx).toFloat()
            }
            if (!scaffoldState.bottomSheetState.skipPartiallyHidden) {
              PartiallyHidden at (layoutHeight - sheetPartialHiddenHeightPx).toFloat()
            }
            if (sheetHeight != sheetPartialExpandedHeightPx) {
              Expanded at maxOf(layoutHeight - sheetHeight, 0).toFloat()
            }
            if (!scaffoldState.bottomSheetState.skipHiddenState) {
              Hidden at layoutHeight.toFloat()
            }
          }
        },
        shape = sheetShape,
        containerColor = sheetContainerColor,
        contentColor = sheetContentColor,
        tonalElevation = sheetTonalElevation,
        shadowElevation = sheetShadowElevation,
        dragHandle = sheetDragHandle,
        content = sheetContent
      )
    }
  )
}

/**
 * State of the [BottomSheetScaffold] composable.
 *
 * @param bottomSheetState the state of the persistent bottom sheet
 * @param snackbarHostState the [SnackbarHostState] used to show snackbars inside the scaffold
 */
class BottomSheetScaffoldState(
  val bottomSheetState: SheetState,
  val snackbarHostState: SnackbarHostState
)

/**
 * Create and [remember] a [BottomSheetScaffoldState].
 *
 * @param bottomSheetState the state of the standard bottom sheet. See
 * [rememberStandardBottomSheetState]
 * @param snackbarHostState the [SnackbarHostState] used to show snackbars inside the scaffold
 */
@Composable
fun rememberBottomSheetScaffoldState(
  bottomSheetState: SheetState = rememberStandardBottomSheetState(),
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): BottomSheetScaffoldState {
  return remember(bottomSheetState, snackbarHostState) {
    BottomSheetScaffoldState(
      bottomSheetState = bottomSheetState,
      snackbarHostState = snackbarHostState
    )
  }
}

/**
 * Create and [remember] a [SheetState] for [BottomSheetScaffold].
 *
 * @param initialValue the initial value of the state. Should be either [PartiallyExpanded] or
 * [Expanded] if [skipHiddenState] is true
 * @param confirmValueChange optional callback invoked to confirm or veto a pending state change
 * @param [skipHiddenState] whether Hidden state is skipped for [BottomSheetScaffold]
 */
@Composable
fun rememberStandardBottomSheetState(
  initialValue: SheetValue = PartiallyExpanded,
  confirmValueChange: (SheetValue) -> Boolean = { true },
  skipHiddenState: Boolean = true,
) = rememberSheetState(
  skipPartiallyExpanded = false,
  skipPartiallyHidden = false,
  confirmValueChange = confirmValueChange,
  initialValue = initialValue,
  skipHiddenState = skipHiddenState
)

@Composable
private fun StandardBottomSheet(
  state: SheetState,
  @Suppress("PrimitiveInLambda")
  calculateAnchors: (sheetSize: IntSize) -> DraggableAnchors<SheetValue>,
  sheetPartialExpandedHeight: Dp,
  sheetPartialHiddenHeight: Dp,
  sheetSwipeEnabled: Boolean,
  shape: Shape,
  containerColor: Color,
  contentColor: Color,
  tonalElevation: Dp,
  shadowElevation: Dp,
  dragHandle: @Composable (() -> Unit)?,
  content: @Composable ColumnScope.() -> Unit
) {
  val scope = rememberCoroutineScope()

  val orientation = Vertical

  val requireHeight = when (state.currentValue) {
    PartiallyExpanded -> sheetPartialExpandedHeight
    PartiallyHidden -> sheetPartialHiddenHeight
    else -> 0.dp
  }

  Surface(
    modifier = Modifier
      .widthIn(max = BottomSheetMaxWidth)
      .fillMaxWidth()
      .requiredHeightIn(min = requireHeight)
      .nestedScroll(
        remember(state.anchoredDraggableState) {
          ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
            sheetState = state,
            orientation = orientation,
            onFling = { scope.launch { state.settle(it) } }
          )
        }
      )
      .anchoredDraggable(
        state = state.anchoredDraggableState,
        orientation = orientation,
        enabled = sheetSwipeEnabled
      )
      .onSizeChanged { layoutSize ->
        val newAnchors = calculateAnchors(layoutSize)
        val newTarget = when (state.anchoredDraggableState.targetValue) {
          Hidden, PartiallyExpanded -> PartiallyExpanded
          Expanded -> {
            if (newAnchors.hasAnchorFor(Expanded)) Expanded else PartiallyExpanded
          }
          PartiallyHidden -> {
            if (newAnchors.hasAnchorFor(PartiallyHidden)) PartiallyHidden else PartiallyExpanded
          }
        }
        state.anchoredDraggableState.updateAnchors(newAnchors, newTarget)
      },
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    shadowElevation = shadowElevation,
  ) {
    Column(Modifier.fillMaxWidth()) {
      if (dragHandle != null) {
        val partialExpandActionLabel = ""
        val partialHideActionLabel = ""
        val dismissActionLabel = "getString(Strings.BottomSheetDismissDescription)"
        val expandActionLabel = "getString(Strings.BottomSheetExpandDescription)"
        Box(
          Modifier
            .align(CenterHorizontally)
            .semantics(mergeDescendants = true) {
              with(state) {
                // Provides semantics to interact with the bottomsheet if there is more
                // than one anchor to swipe to and swiping is enabled.
                // TODO: update semantics to support swiping to / from partial hidden state

                if (anchoredDraggableState.anchors.size > 1 && sheetSwipeEnabled) {
                  if (currentValue == PartiallyExpanded) {
                    expand(expandActionLabel) {
                      scope.launch { expand() }; true
                    }
                  } else {
                    collapse(partialExpandActionLabel) {
                      scope.launch { partialExpand() }; true
                    }
                  }
                  if (!state.skipHiddenState) {
                    dismiss(dismissActionLabel) {
                      scope.launch { hide() }
                      true
                    }
                  }
                }
              }
            },
        ) {
          dragHandle()
        }
      }
      content()
    }
  }
}

@Composable
private fun BottomSheetScaffoldLayout(
  modifier: Modifier,
  topBar: @Composable (() -> Unit)?,
  body: @Composable (innerPadding: PaddingValues) -> Unit,
  bottomSheet: @Composable (layoutHeight: Int) -> Unit,
  snackbarHost: @Composable () -> Unit,
  sheetPartialExpandedHeight: Dp,
  sheetPartialHiddenHeight: Dp,
  sheetOffset: () -> Float,
  sheetState: SheetState,
  containerColor: Color,
  contentColor: Color,
) {
  // b/291735717 Remove this once deprecated methods without density are removed
  val density = LocalDensity.current
  SideEffect {
    sheetState.density = density
  }
  SubcomposeLayout { constraints ->
    val layoutWidth = constraints.maxWidth
    val layoutHeight = constraints.maxHeight
    val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

    val sheetPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Sheet) {
      bottomSheet(layoutHeight)
    }[0].measure(looseConstraints)

    val topBarPlaceable = topBar?.let {
      subcompose(BottomSheetScaffoldLayoutSlot.TopBar) { topBar() }[0]
        .measure(looseConstraints)
    }
    val topBarHeight = topBarPlaceable?.height ?: 0

    val bodyConstraints = looseConstraints.copy(maxHeight = layoutHeight - topBarHeight)
    val bodyPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Body) {
      Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
      ) { body(PaddingValues(bottom = sheetPartialHiddenHeight)) }
    }[0].measure(bodyConstraints)

    val snackbarPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Snackbar, snackbarHost)[0]
      .measure(looseConstraints)

    layout(layoutWidth, layoutHeight) {
      val sheetOffsetY = sheetOffset().roundToInt()
      val sheetOffsetX = Integer.max(0, (layoutWidth - sheetPlaceable.width) / 2)

      val snackbarOffsetX = (layoutWidth - snackbarPlaceable.width) / 2
      val snackbarOffsetY = when (sheetState.currentValue) {
        PartiallyExpanded, PartiallyHidden  -> sheetOffsetY - snackbarPlaceable.height
        Expanded, Hidden -> layoutHeight - snackbarPlaceable.height
      }

      // Placement order is important for elevation
      bodyPlaceable.placeRelative(0, topBarHeight)
      topBarPlaceable?.placeRelative(0, 0)
      sheetPlaceable.placeRelative(sheetOffsetX, sheetOffsetY)
      snackbarPlaceable.placeRelative(snackbarOffsetX, snackbarOffsetY)
    }
  }
}

private enum class BottomSheetScaffoldLayoutSlot {
  TopBar,
  Body,
  Sheet,
  Snackbar
}

/**
 * State of a sheet composable, such as [ModalBottomSheet]
 *
 * Contains states relating to its swipe position as well as animations between state values.
 *
 * @param skipPartiallyExpanded Whether the partially expanded state, if the sheet is large
 * enough, should be skipped. If true, the sheet will always expand to the [Expanded] state and move
 * to the [Hidden] state if available when hiding the sheet, either programmatically or by user
 * interaction.
 * @param initialValue The initial value of the state.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 * @param skipHiddenState Whether the hidden state should be skipped. If true, the sheet will always
 * expand to the [Expanded] state and move to the [PartiallyExpanded] if available, either
 * programmatically or by user interaction.
 */
@Stable
@ExperimentalMaterial3Api
class SheetState(
  internal val skipPartiallyExpanded: Boolean,
  internal val skipPartiallyHidden: Boolean,
  internal var density: Density? = null,
  initialValue: SheetValue = Hidden,
  confirmValueChange: (SheetValue) -> Boolean = { true },
  internal val skipHiddenState: Boolean = false,
) {
  init {
    if (skipPartiallyExpanded) {
      require(initialValue != PartiallyExpanded) {
        "The initial value must not be set to PartiallyExpanded if skipPartiallyExpanded " +
            "is set to true."
      }
    }
    if(skipPartiallyHidden) {
      require(initialValue != PartiallyHidden) {
        "The initial value must not be set to PartiallyHidden if skipPartiallyHidden " +
            "is set to true."
      }
    }
    if (skipHiddenState) {
      require(initialValue != Hidden) {
        "The initial value must not be set to Hidden if skipHiddenState is set to true."
      }
    }
  }

  /**
   * The current value of the state.
   *
   * If no swipe or animation is in progress, this corresponds to the state the bottom sheet is
   * currently in. If a swipe or an animation is in progress, this corresponds the state the sheet
   * was in before the swipe or animation started.
   */

  val currentValue: SheetValue get() = anchoredDraggableState.currentValue

  /**
   * The target value of the bottom sheet state.
   *
   * If a swipe is in progress, this is the value that the sheet would animate to if the
   * swipe finishes. If an animation is running, this is the target value of that animation.
   * Finally, if no swipe or animation is in progress, this is the same as the [currentValue].
   */
  val targetValue: SheetValue get() = anchoredDraggableState.targetValue

  /**
   * Whether the modal bottom sheet is visible.
   */
  val isVisible: Boolean
    get() = anchoredDraggableState.currentValue != Hidden

  /**
   * Require the current offset (in pixels) of the bottom sheet.
   *
   * The offset will be initialized during the first measurement phase of the provided sheet
   * content.
   *
   * These are the phases:
   * Composition { -> Effects } -> Layout { Measurement -> Placement } -> Drawing
   *
   * During the first composition, an [IllegalStateException] is thrown. In subsequent
   * compositions, the offset will be derived from the anchors of the previous pass. Always prefer
   * accessing the offset from a LaunchedEffect as it will be scheduled to be executed the next
   * frame, after layout.
   *
   * @throws IllegalStateException If the offset has not been initialized yet
   */
  fun requireOffset(): Float = anchoredDraggableState.requireOffset()

  /**
   * Whether the sheet has an expanded state defined.
   */

  val hasExpandedState: Boolean
    get() = anchoredDraggableState.anchors.hasAnchorFor(Expanded)

  /**
   * Whether the modal bottom sheet has a partially expanded state defined.
   */
  val hasPartiallyExpandedState: Boolean
    get() = anchoredDraggableState.anchors.hasAnchorFor(PartiallyExpanded)

  /**
   * Whether the modal bottom sheet has a partially hidden state defined.
   */
  val hasPartiallyHiddenState: Boolean
    get() = anchoredDraggableState.anchors.hasAnchorFor(PartiallyHidden)

  /**
   * Fully expand the bottom sheet with animation and suspend until it is fully expanded or
   * animation has been cancelled.
   * *
   * @throws [CancellationException] if the animation is interrupted
   */
  suspend fun expand() {
    anchoredDraggableState.animateTo(Expanded)
  }

  /**
   * Animate the bottom sheet and suspend until it is partially expanded or animation has been
   * cancelled.
   * @throws [CancellationException] if the animation is interrupted
   * @throws [IllegalStateException] if [skipPartiallyExpanded] is set to true
   */
  suspend fun partialExpand() {
    check(!skipPartiallyExpanded) {
      "Attempted to animate to partial expanded when skipPartiallyExpanded was enabled. Set" +
          " skipPartiallyExpanded to false to use this function."
    }
    animateTo(PartiallyExpanded)
  }

  /**
   * Animate the bottom sheet and suspend until it is partially hidden or animation has been
   * cancelled.
   * @throws [CancellationException] if the animation is interrupted
   * @throws [IllegalStateException] if [skipPartiallyHidden] is set to true
   */
  suspend fun partialHide() {
    check(!skipPartiallyHidden) {
      "Attempted to animate to partial hidden when skipPartiallyHidden was enabled. Set" +
          " skipPartiallyHidden to false to use this function."
    }
    animateTo(PartiallyHidden)
  }

  /**
   * Expand the bottom sheet with animation and suspend until it is [PartiallyExpanded] if defined
   * else [Expanded].
   * @throws [CancellationException] if the animation is interrupted
   */
  suspend fun show() {
    val targetValue = when {
      hasPartiallyExpandedState -> PartiallyExpanded
      else -> Expanded
    }
    animateTo(targetValue)
  }

  /**
   * Hide the bottom sheet with animation and suspend until it is fully hidden or animation has
   * been cancelled.
   * @throws [CancellationException] if the animation is interrupted
   */
  suspend fun hide() {
    check(!skipHiddenState) {
      "Attempted to animate to hidden when skipHiddenState was enabled. Set skipHiddenState" +
          " to false to use this function."
    }
    animateTo(Hidden)
  }

  /**
   * Animate to a [targetValue].
   * If the [targetValue] is not in the set of anchors, the [currentValue] will be updated to the
   * [targetValue] without updating the offset.
   *
   * @throws CancellationException if the interaction interrupted by another interaction like a
   * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
   *
   * @param targetValue The target value of the animation
   */
  private suspend fun animateTo(
    targetValue: SheetValue,
    velocity: Float = anchoredDraggableState.lastVelocity
  ) {
    anchoredDraggableState.animateTo(targetValue, velocity)
  }

  /**
   * Snap to a [targetValue] without any animation.
   *
   * @throws CancellationException if the interaction interrupted by another interaction like a
   * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
   *
   * @param targetValue The target value of the animation
   */
  private suspend fun snapTo(targetValue: SheetValue) {
    anchoredDraggableState.snapTo(targetValue)
  }

  /**
   * Find the closest anchor taking into account the velocity and settle at it with an animation.
   */
  internal suspend fun settle(velocity: Float) {
    anchoredDraggableState.settle(velocity)
  }

  internal var anchoredDraggableState = AnchoredDraggableState(
    initialValue = initialValue,
    animationSpec = SpringSpec(),
    confirmValueChange = confirmValueChange,
    positionalThreshold = { with(requireDensity()) { 56.dp.toPx() } },
    velocityThreshold = { with(requireDensity()) { 125.dp.toPx() } }
  )

  internal val offset: Float? get() = anchoredDraggableState.offset

  private fun requireDensity() = requireNotNull(density) {
    "SheetState did not have a density attached. Are you using SheetState with " +
        "BottomSheetScaffold or ModalBottomSheet component?"
  }

  companion object {
    /**
     * The default [Saver] implementation for [SheetState].
     */
    fun Saver(
      skipPartiallyExpanded: Boolean,
      skipPartiallyHidden: Boolean,
      confirmValueChange: (SheetValue) -> Boolean,
      density: Density
    ) = Saver<SheetState, SheetValue>(
      save = { it.currentValue },
      restore = { savedValue ->
        SheetState(skipPartiallyExpanded, skipPartiallyHidden,density, savedValue, confirmValueChange)
      }
    )
  }
}

@Composable
internal fun rememberSheetState(
  skipPartiallyExpanded: Boolean = false,
  skipPartiallyHidden: Boolean = false,
  confirmValueChange: (SheetValue) -> Boolean = { true },
  initialValue: SheetValue = Hidden,
  skipHiddenState: Boolean = false,
): SheetState {

  val density = LocalDensity.current
  return rememberSaveable(
    skipPartiallyExpanded,
    confirmValueChange,
    saver = SheetState.Saver(
      skipPartiallyExpanded = skipPartiallyExpanded,
      skipPartiallyHidden = skipPartiallyHidden,
      confirmValueChange = confirmValueChange,
      density = density
    )
  ) {
    SheetState(
      skipPartiallyExpanded,
      skipPartiallyHidden,
      density,
      initialValue,
      confirmValueChange,
      skipHiddenState
    )
  }
}