package com.example.livepictures.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.livepictures.R
import com.example.livepictures.model.BrushType
import com.example.livepictures.model.BrushTexture
import com.example.livepictures.ui.theme.Black

@Composable
fun BrushSelectionDialog(
    currentBrushType: BrushType,
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (BrushType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Выберите кисть",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(BrushType.values()) { brushType ->
                        BrushItem(
                            brushType = brushType,
                            isSelected = brushType == currentBrushType,
                            onClick = { onPositiveClick(brushType) }
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onNegativeClick) {
                        Text("Отмена")
                    }
                }
            }
        }
    }
}

@Composable
fun BrushItem(
    brushType: BrushType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.Blue else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Иконка кисти
            Icon(
                imageVector = when (brushType) {
                    BrushType.PENCIL -> ImageVector.vectorResource(id = R.drawable.pencel)
                    BrushType.PEN -> ImageVector.vectorResource(id = R.drawable.pencel)
                    BrushType.MARKER -> ImageVector.vectorResource(id = R.drawable.brush)
                    BrushType.BRUSH -> ImageVector.vectorResource(id = R.drawable.brush)
                },
                contentDescription = brushType.displayName,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) Color.Blue else Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = brushType.displayName,
                fontSize = 12.sp,
                color = if (isSelected) Color.Blue else Color.Black
            )
            
            // Предварительный просмотр линии
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(vertical = 4.dp)
            ) {
                // Безопасная функция для создания цвета
                fun safeColor(alpha: Float): Color {
                    return Color.Black.copy(alpha = alpha.coerceIn(0f, 1f))
                }
                
                when (brushType.texture) {
                    BrushTexture.SOLID -> {
                        // Обычная сплошная линия
                        drawLine(
                            color = safeColor(brushType.alpha),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                            strokeWidth = brushType.strokeWidth,
                            cap = brushType.strokeCap
                        )
                    }
                    BrushTexture.MARKER -> {
                        // Маркер с эффектом растекания - более заметный
                        drawLine(
                            color = safeColor(brushType.alpha * 0.4f),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2 + 3),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2 + 3),
                            strokeWidth = brushType.strokeWidth + 8,
                            cap = brushType.strokeCap
                        )
                        drawLine(
                            color = safeColor(brushType.alpha * 0.7f),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2 + 1),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2 + 1),
                            strokeWidth = brushType.strokeWidth + 4,
                            cap = brushType.strokeCap
                        )
                        drawLine(
                            color = safeColor(brushType.alpha),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                            strokeWidth = brushType.strokeWidth,
                            cap = brushType.strokeCap
                        )
                    }
                    BrushTexture.BRUSH -> {
                        // Текстурированная кисть - более выраженная
                        for (i in 0..4) {
                            val alphaMultiplier = (1f - i * 0.2f).coerceIn(0.1f, 1f)
                            val widthMultiplier = (1f - i * 0.15f).coerceIn(0.3f, 1f)
                            
                            drawLine(
                                color = safeColor(brushType.alpha * alphaMultiplier),
                                start = androidx.compose.ui.geometry.Offset(0f, size.height / 2 - i * 0.8f),
                                end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2 - i * 0.8f),
                                strokeWidth = brushType.strokeWidth * widthMultiplier,
                                cap = brushType.strokeCap
                            )
                        }
                    }


                }
            }
        }
    }
} 