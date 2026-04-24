package com.hasan.collabworkspace.presentation.workspace.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hasan.collabworkspace.domain.model.Asset
import kotlin.math.roundToInt

@Composable
fun AssetBlock(
    asset: Asset,
    onTransform: (x: Float, y: Float, rotation: Float, scale: Float) -> Unit
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(asset.x.roundToInt(), asset.y.roundToInt()) }
            .graphicsLayer(
                scaleX = asset.scale,
                scaleY = asset.scale,
                rotationZ = asset.rotation
            )
            .pointerInput(asset.id) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    onTransform(
                        asset.x + pan.x,
                        asset.y + pan.y,
                        asset.rotation + rotation,
                        (asset.scale * zoom).coerceIn(0.5f, 5f)
                    )
                }
            }
            .size(200.dp) // Default size, can be customized or loaded from image size
    ) {
        AsyncImage(
            model = asset.imageUrl.ifEmpty { "https://picsum.photos/400" }, // Mock image if empty
            contentDescription = "Workspace Asset",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}
