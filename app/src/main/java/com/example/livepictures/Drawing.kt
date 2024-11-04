package com.example.livepictures

import android.graphics.Paint
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.livepictures.menu.DrawingPropertiesMenu
import com.example.livepictures.menu.DrawingPropertiesMenuApp
import com.example.livepictures.menu.DrawingPropertiesMenuBottom
import com.example.livepictures.mode.DrawMode
import com.example.livepictures.mode.MotionEvent
import com.example.livepictures.mode.dragMotionEvent
import com.example.livepictures.model.PathProperties


@RequiresApi(35)
@Composable
fun Drawing(modifier: Modifier) {

    val context = LocalContext.current
    // Пути для рисования
    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    // Пути для отмены рисования (возврата при нажатии на кнопку)
    val pathsUndone = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    //Текущее положение указателя, который нажат на экране
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    //Предыдущее событие движения перед следующим касанием сохраняется в этой текущей позиции
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    //Режим рисования, режим стирания или режим касания для
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    var currentPath by remember { mutableStateOf(Path()) }

    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    val canvasText = remember { StringBuilder() }
    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = Color.Black.toArgb()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        val drawModifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp
            )
            .shadow(1.dp)
            .fillMaxWidth()
            .weight(1f)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.White)
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f)
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consumeDownChange()

                },
                onDrag = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position

                    if (drawMode == DrawMode.Touch) {
                        val change = pointerInputChange.positionChange()
                        println("DRAG: $change")
                        paths.forEach { entry ->
                            val path: Path = entry.first
                            path.translate(change)
                        }
                        currentPath.translate(change)
                    }
                    pointerInputChange.consumePositionChange()

                },
                onDragEnd = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    pointerInputChange.consumeDownChange()
                }
            )
        // Верхнее меню
        DrawingPropertiesMenuApp(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(Color.Black)
                .padding(4.dp),
            onUndo = {
                if (paths.isNotEmpty()) {

                    val lastItem = paths.last()
                    val lastPath = lastItem.first
                    val lastPathProperty = lastItem.second
                    paths.remove(lastItem)

                    pathsUndone.add(Pair(lastPath, lastPathProperty))

                }
            },
            onRedo = {
                if (pathsUndone.isNotEmpty()) {

                    val lastPath = pathsUndone.last().first
                    val lastPathProperty = pathsUndone.last().second
                    pathsUndone.removeLast()
                    paths.add(Pair(lastPath, lastPathProperty))
                }
            },
            pathProperties = currentPathProperty,
            onPathPropertiesChange = {
                motionEvent = MotionEvent.Idle
            },
        )
        Canvas(modifier = drawModifier) {

            when (motionEvent) {

                MotionEvent.Down -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                    }

                    previousPosition = currentPosition

                }
                MotionEvent.Move -> {

                    if (drawMode != DrawMode.Touch) {
                        currentPath.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )
                    }

                    previousPosition = currentPosition
                }

                MotionEvent.Up -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)

                        // Pointer is up save current path
//                        paths[currentPath] = currentPathProperty
                        paths.add(Pair(currentPath, currentPathProperty))

                        // Since paths are keys for map, use new one for each key
                        // and have separate path for each down-move-up gesture cycle
                        currentPath = Path()

                        // Create new instance of path properties to have new path and properties
                        // only for the one currently being drawn
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )
                    }

                    // Since new path is drawn no need to store paths to undone
                    pathsUndone.clear()

                    // If we leave this state at MotionEvent.Up it causes current path to draw
                    // line from (0,0) if this composable recomposes when draw mode is changed
                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }
                else -> Unit
            }

            with(drawContext.canvas.nativeCanvas) {

                val checkPoint = saveLayer(null, null)

                paths.forEach {

                    val path = it.first
                    val property = it.second

                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {

                        // Source
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                if (motionEvent != MotionEvent.Idle) {

                    if (!currentPathProperty.eraseMode) {
                        drawPath(
                            color = currentPathProperty.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }

        }
        // Нижнее меню
        DrawingPropertiesMenuBottom(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(Color.Black)
                .padding(4.dp),
            pathProperties = currentPathProperty,
            drawMode = drawMode,
            onPathPropertiesChange = {
                motionEvent = MotionEvent.Idle
            },
            onDrawModeChanged = {
                motionEvent = MotionEvent.Idle
                drawMode = it
                currentPathProperty.eraseMode = (drawMode == DrawMode.Erase)
                Toast.makeText(
                    context, "pathProperty: ${currentPathProperty.hashCode()}, " +
                            "Erase Mode: ${currentPathProperty.eraseMode}", Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}


private fun DrawScope.drawText(text: String, x: Float, y: Float, paint: Paint) {

    val lines = text.split("\n")

    val nativeCanvas = drawContext.canvas.nativeCanvas

    lines.indices.withIndex().forEach { (posY, i) ->
        nativeCanvas.drawText(lines[i], x, posY * 40 + y, paint)
    }
}