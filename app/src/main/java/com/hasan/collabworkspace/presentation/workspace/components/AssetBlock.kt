package com.hasan.collabworkspace.presentation.workspace.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hasan.collabworkspace.domain.model.Asset
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun AssetBlock(
    asset: Asset,
    onTransform: (x: Float, y: Float, rotation: Float, scale: Float) -> Unit,
    onDelete: () -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }
    var pointerCount by remember { mutableIntStateOf(0) }
    val showRotationHud = pointerCount >= 3

    Box(
        modifier = Modifier
            .offset { IntOffset(asset.x.roundToInt(), asset.y.roundToInt()) }
            .graphicsLayer(
                scaleX = asset.scale,
                scaleY = asset.scale,
                rotationZ = asset.rotation
            )
            .pointerInput(asset.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        pointerCount = event.changes.count { it.pressed }
                        if (pointerCount >= 1) isSelected = true
                        
                        // Requirement: Finger 1 selects/focuses
                        // Requirement: Finger 2 enables rotation
                        // Requirement: Finger 3 shows HUD
                    }
                }
            }
            .pointerInput(asset.id) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    // Logic based on requirements
                    val canRotate = pointerCount >= 2
                    val canZoom = pointerCount >= 2
                    
                    val newRotation = if (canRotate) asset.rotation + rotation else asset.rotation
                    val newScale = if (canZoom) (asset.scale * zoom).coerceIn(0.5f, 5f) else asset.scale
                    
                    onTransform(
                        asset.x + pan.x,
                        asset.y + pan.y,
                        newRotation,
                        newScale
                    )
                }
            }
            .size(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .shadow(if (isSelected) 12.dp else 2.dp, RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = asset.imageUrl.ifEmpty { "https://picsum.photos/seed/${asset.id}/400" },
            contentDescription = "Workspace Asset",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        if (isSelected) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                    .size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Directional/Rotation HUD (Requirement: Finger 3)
        if (showRotationHud) {
            RotationHudOverlay(rotation = asset.rotation)
        }
    }
}

@Composable
fun RotationHudOverlay(rotation: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                val primaryColor = MaterialTheme.colorScheme.primary
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Circular Track
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        style = Stroke(width = 4.dp.toPx())
                    )
                    
                    // Rotation Indicator
                    val angleRad = Math.toRadians(rotation.toDouble() - 90.0)
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2
                    val indicatorPos = Offset(
                        center.x + (radius * cos(angleRad)).toFloat(),
                        center.y + (radius * sin(angleRad)).toFloat()
                    )
                    
                    drawLine(
                        color = primaryColor,
                        start = center,
                        end = indicatorPos,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    
                    drawCircle(
                        color = primaryColor,
                        center = indicatorPos,
                        radius = 8.dp.toPx()
                    )
                }
                Text(
                    text = "${rotation.roundToInt()}°",
                    color = Color.White,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ROTATION HUD",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
