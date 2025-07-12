package com.example.livepictures

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
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
import com.example.livepictures.menu.DrawingPropertiesMenuApp
import com.example.livepictures.menu.DrawingPropertiesMenuBottom
import com.example.livepictures.mode.DrawMode
import com.example.livepictures.mode.MotionEvent
import com.example.livepictures.mode.dragMotionEvent
import com.example.livepictures.model.PathProperties
import com.example.livepictures.model.BrushType
import com.example.livepictures.model.BrushTexture
import kotlinx.coroutines.delay
import kotlin.math.pow
import java.io.IOException


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
    


    var canvasWidth: Int = 0

    var canvasHeight: Int = 0

    var fadeEffectEnabled by remember { mutableStateOf(false) }

    var uiVisibility by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
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
            /*.paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f
            )*/
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
                .background(colorScheme.background)
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
                // Создаем bitmap из текущих путей
                if (canvasWidth > 0 && canvasHeight > 0) {
                    val bitmap = generateCurrentBitmap(paths, canvasWidth, canvasHeight)
                    frames.add(bitmap)
                    currentFrameIndex = frames.size - 1
                    
                    fadeEffectEnabled = true
                    Toast.makeText(context, "Frame added: ${currentFrameIndex + 1}", Toast.LENGTH_SHORT)
                        .show()
                    
                    // Очищаем пути после добавления фрейма
                    paths.clear()
                    pathsUndone.clear()
                } else {
                    Toast.makeText(context, "Canvas not ready", Toast.LENGTH_SHORT).show()
                }
            },
            deleteFrame = {
                if (isPlaying || currentBitmap != null) {
                    // Режим просмотра - удаляем фрейм
                    if (frames.isNotEmpty()) {
                        frames.removeAt(currentFrameIndex)
                        currentFrameIndex = if (frames.isNotEmpty()) {
                            if (currentFrameIndex >= frames.size) frames.size - 1 else currentFrameIndex
                        } else {
                            -1
                        }
                        currentBitmap = frames.getOrNull(currentFrameIndex)
                        Toast.makeText(
                            context,
                            "Frame deleted. Total frames: ${frames.size}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "No frames to delete", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Режим рисования - очищаем полотно
                    paths.clear()
                    pathsUndone.clear()
                    currentBitmap = null
                    motionEvent = MotionEvent.Idle
                    Toast.makeText(context, "Полотно очищено", Toast.LENGTH_SHORT).show()
                }
            },
            onStop = {
                isPlaying = false
                uiVisibility = true
                // Показываем последний фрейм при остановке
                currentBitmap = frames.getOrNull(currentFrameIndex)
            },
            onPlay = {
                if (frames.isNotEmpty() && !isPlaying) {
                    isPlaying = true
                    uiVisibility = false
                    currentFrameIndex = 0 // Начинаем с первого фрейма
                } else if (frames.isEmpty()) {
                    Toast.makeText(context, "No frames to play", Toast.LENGTH_SHORT).show()
                }
            },
            uiVisibility = uiVisibility,
            frameCount = frames.size,
            currentFrameIndex = currentFrameIndex
        )
        Canvas(
            modifier = drawModifier

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

                    //currentBitmap = generateCurrentBitmap(paths, canvasWidth, canvasHeight)
                }

                else -> Unit
            }

            with(drawContext.canvas.nativeCanvas) {

                val checkPoint = saveLayer(null, null)

                paths.forEach { (path, property) ->
                    drawPathWithBrushEffect(path, property)
                }

                if (motionEvent != MotionEvent.Idle) {
                    drawPathWithBrushEffect(currentPath, currentPathProperty)
                }
                restoreToCount(checkPoint)
            }

            currentBitmap?.let {
                drawImage(it.asImageBitmap())
            }

        }

        LaunchedEffect(isPlaying) {
            if (isPlaying && frames.isNotEmpty()) {
                while (isPlaying && currentFrameIndex < frames.size) {
                    currentBitmap = frames[currentFrameIndex]
                    delay(1000) // 1 секунда на фрейм
                    currentFrameIndex++
                }
                // Анимация завершена - остаемся в режиме просмотра
                isPlaying = false
                uiVisibility = true
                currentFrameIndex = if (frames.isNotEmpty()) frames.size - 1 else -1
                currentBitmap = frames.getOrNull(currentFrameIndex) // Показываем последний фрейм
                motionEvent = MotionEvent.Idle
            }
        }
        // Нижнее меню
        DrawingPropertiesMenuBottom(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(colorScheme.background)
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
                
                // При переключении на режим Brush применяем случайную кисть
                if (drawMode == DrawMode.Brush) {
                    val brushTypes = com.example.livepictures.model.BrushType.values()
                    val randomBrush = brushTypes.random()
                    currentPathProperty.applyBrushType(randomBrush)
                }
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

private fun DrawScope.drawPathWithBrushEffect(path: Path, property: PathProperties) {
    if (property.eraseMode) {
        // Режим стирания
        drawPath(
            color = Color.Transparent,
            path = path,
            style = Stroke(
                width = property.strokeWidth,
                cap = property.strokeCap,
                join = property.strokeJoin
            ),
            blendMode = BlendMode.Clear
        )
        return
    }

    // Безопасная функция для создания цвета с проверкой alpha
    fun safeColor(alpha: Float): Color {
        val safeAlpha = alpha.coerceIn(0f, 1f)
        return property.color.copy(alpha = safeAlpha)
    }

    when (property.brushTexture) {
        BrushTexture.SOLID -> {
            // Обычная сплошная линия
            drawPath(
                color = safeColor(property.alpha),
                path = path,
                style = Stroke(
                    width = property.strokeWidth,
                    cap = property.strokeCap,
                    join = property.strokeJoin
                )
            )
        }
        BrushTexture.MARKER -> {
            // Маркер с эффектом растекания - более заметный эффект
            drawPath(
                color = safeColor(property.alpha * 0.4f),
                path = path,
                style = Stroke(
                    width = property.strokeWidth + 8,
                    cap = property.strokeCap,
                    join = property.strokeJoin
                )
            )
            drawPath(
                color = safeColor(property.alpha * 0.7f),
                path = path,
                style = Stroke(
                    width = property.strokeWidth + 4,
                    cap = property.strokeCap,
                    join = property.strokeJoin
                )
            )
            drawPath(
                color = safeColor(property.alpha),
                path = path,
                style = Stroke(
                    width = property.strokeWidth,
                    cap = property.strokeCap,
                    join = property.strokeJoin
                )
            )
        }
        BrushTexture.BRUSH -> {
            // Текстурированная кисть - более выраженная текстура
            for (i in 0..4) {
                val alphaMultiplier = (1f - i * 0.2f).coerceIn(0.1f, 1f)
                val widthMultiplier = (1f - i * 0.15f).coerceIn(0.3f, 1f)
                
                drawPath(
                    color = safeColor(property.alpha * alphaMultiplier),
                    path = path,
                    style = Stroke(
                        width = property.strokeWidth * widthMultiplier,
                        cap = property.strokeCap,
                        join = property.strokeJoin
                    )
                )
            }
        }
    }
}

// Функция для захвата текущего Canvas в Bitmap
fun generateCurrentBitmap(
    paths: List<Pair<Path, PathProperties>>,
    canvasWidth: Int,
    canvasHeight: Int
): Bitmap {
    // Создаем Bitmap на основе размеров Canvas
    val bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    
    // Заполняем белым фоном
    canvas.drawColor(android.graphics.Color.WHITE)

    // Рендерим все пути на созданном Canvas
    paths.forEach { (path, property) ->
        val paint = Paint().apply {
            if (property.eraseMode) {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                color = android.graphics.Color.TRANSPARENT
            } else {
                color = property.color.toArgb()
                strokeWidth = property.strokeWidth
                style = Paint.Style.STROKE
                isAntiAlias = true
                strokeCap = when (property.strokeCap) {
                    androidx.compose.ui.graphics.StrokeCap.Butt -> Paint.Cap.BUTT
                    androidx.compose.ui.graphics.StrokeCap.Round -> Paint.Cap.ROUND
                    androidx.compose.ui.graphics.StrokeCap.Square -> Paint.Cap.SQUARE
                    else -> Paint.Cap.BUTT
                }
                strokeJoin = when (property.strokeJoin) {
                    androidx.compose.ui.graphics.StrokeJoin.Bevel -> Paint.Join.BEVEL
                    androidx.compose.ui.graphics.StrokeJoin.Miter -> Paint.Join.MITER
                    androidx.compose.ui.graphics.StrokeJoin.Round -> Paint.Join.ROUND
                    else -> Paint.Join.MITER
                }
            }
        }
        canvas.drawPath(path.asAndroidPath(), paint)
    }
    
    return bitmap
}

/** Функция сохранения изображений**/
fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): Uri? {
    val imageUri: Uri
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyDrawings")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return null
    imageUri.let { uri ->
        try {
            val outputStream = resolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            outputStream?.close()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
            return uri


        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            return null
        }
    }
}
/*
fun MyCanvas() {
    val context = LocalContext.current
    val density = LocalDensity.current
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    val canvasWidth = 500
    val canvasHeight = 500

    val saveImage = {
        currentBitmap?.let { bitmap ->
            val fileName = "drawing_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.png"
            saveBitmapToFile(context, bitmap, fileName)
        }
    }

    // Пример использования:
    // 1. Создайте currentBitmap
    currentBitmap = generateCurrentBitmap(paths, canvasWidth, canvasHeight, density)
    // 2. Вызовите saveImage
    Button(onClick = saveImage) {
        Text("Save Image")
    }
}*/