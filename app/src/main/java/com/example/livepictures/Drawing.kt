package com.example.livepictures

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.View
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    //Для перемещения
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    //Отслеживание текущей позиции касания
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }
    // Список фреймов
    var frames by remember { mutableStateOf(mutableListOf<Bitmap>()) }
    // Текущий фрейм
    var currentFrameIndex by remember { mutableStateOf(-1) }
    // Состояние воспроизведения
    var isPlaying by remember { mutableStateOf(false) }
    // Текущий битмап
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var canvasWidth:Int = 0

    var canvasHeight:Int = 0

    var fadeEffectEnabled by remember { mutableStateOf(false) }

    var uiVisibility by remember { mutableStateOf(true) }

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
                alpha = 0.5f
            )
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
            addFrame = {

                currentBitmap?.let { bitmap ->
                    frames.add(bitmap)
                    currentFrameIndex = frames.size - 1
                    currentBitmap = null

                    fadeEffectEnabled = true
                    Toast.makeText(context, "AddFrame: ${currentFrameIndex}", Toast.LENGTH_SHORT).show()

                    if (paths.isNotEmpty()) {
                       val lastItem = paths.last()
                       val lastPath = lastItem.first
                       val lastPathProperty = lastItem.second
                       paths.remove(lastItem)
                       pathsUndone.add(Pair(lastPath, lastPathProperty))
                   }
                }
            },
            deleteFrame = {
                if(frames.isNotEmpty() && currentFrameIndex >= 0) {
                    frames.removeAt(currentFrameIndex)
                    currentFrameIndex = if (currentFrameIndex > 0) currentFrameIndex - 1 else 0
                    currentBitmap = frames.getOrNull(currentFrameIndex)
                    Toast.makeText(context, "DeleteFrame: ${currentFrameIndex} CurrentBitmap: ${currentBitmap}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No Frame to Delete", Toast.LENGTH_SHORT).show()
                }
            },
            onStop = {
                isPlaying = false
                uiVisibility = true
                Toast.makeText(context, "Stop: ${isPlaying}", Toast.LENGTH_SHORT).show()
            },
            onPlay = {
                if (frames.isNotEmpty() && !isPlaying) {
                    isPlaying = true
                    uiVisibility = false
                    Toast.makeText(context, "Play: ${isPlaying}", Toast.LENGTH_SHORT).show()
                }
            },
            uiVisibility = uiVisibility
        )
        Canvas(modifier = drawModifier
            .onSizeChanged {
                canvasWidth = it.width
                canvasHeight = it.height
            }
        ) {
            if (fadeEffectEnabled) {
                drawRect(
                    color = Color.White.copy(alpha = 0.1f), // Настройка прозрачности затухания
                    size = size
                )
                // Сбрасываем эффект после отрисовки
                fadeEffectEnabled = false
            }

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

                        paths.add(Pair(currentPath, currentPathProperty))

                        currentPath = Path()

                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )
                    }

                    pathsUndone.clear()

                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle

                    currentBitmap = generateCurrentBitmap(paths, canvasWidth, canvasHeight)
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
            currentBitmap?.let {
                drawImage(it.asImageBitmap())
            }

        }
        LaunchedEffect(isPlaying) {
            if (isPlaying) {
                while (isPlaying && frames.isNotEmpty()) {
                    currentBitmap = frames[currentFrameIndex]
                    currentFrameIndex = (currentFrameIndex + 1) % frames.size
                    delay(1000)
                }
                isPlaying = false // Останавливаем после завершения анимации
                currentBitmap = frames.getOrNull(currentFrameIndex) // Возвращаем последний кадр
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
            },
            uiVisibility = uiVisibility
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

// Функция для захвата текущего Canvas в Bitmap
fun generateCurrentBitmap(paths: List<Pair<Path, PathProperties>>, canvasWidth: Int, canvasHeight: Int) : Bitmap{
    // Создаем Bitmap на основе размеров Canvas
    val bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Рендерим все пути на созданном Canvas
    paths.forEach { (path, property) ->
        val androidPath = android.graphics.Path()
        path.asAndroidPath().apply {
            androidPath.addPath(this)
        }
        val paint = Paint().apply {
            if(property.eraseMode) {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
                color = property.color.toArgb()
                strokeWidth = property.strokeWidth
                style = Paint.Style.STROKE // Replace Stroke with Paint.Style.STROKE
                isAntiAlias = true
            }
        }
        canvas.drawPath(path.asAndroidPath(), paint)
    }
    // Сохраняем результат в currentBitmap
    return bitmap
}