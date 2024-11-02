package com.example.livepictures

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.livepictures.point.Point
import com.example.livepictures.ui.theme.LivePicturesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LivePicturesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}


@Composable
fun MainScreen(modifier: Modifier) {
    Column(
        modifier = modifier
            .background(color = Color.Black)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TopRow(modifier.weight(1f))
        CanvasTest(modifier = Modifier.weight(10f))
        BottomRow(modifier.weight(1f))
    }
}

@Composable
private fun BottomRow(modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var selectedIcon by rememberSaveable { mutableStateOf(-1) }
        IconButton(onClick = { selectedIcon = 0 }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.pencel),
                contentDescription = " ",
                tint = if (selectedIcon == 0) com.example.livepictures.ui.theme.Green else Color.White //Исправить цвета
            )
        }
        IconButton(onClick = { selectedIcon = 1 }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.brush),
                contentDescription = " ",
                tint = if (selectedIcon == 1) com.example.livepictures.ui.theme.Green else Color.White //Исправить цвета
            )
        }
        IconButton(onClick = { selectedIcon = 2 }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.erase),
                contentDescription = " ",
                tint = if (selectedIcon == 2) com.example.livepictures.ui.theme.Green else Color.White //Исправить цвета
            )
        }
        IconButton(onClick = { selectedIcon = 3 }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.instruments),
                contentDescription = " ",
                tint = if (selectedIcon == 3) com.example.livepictures.ui.theme.Green else Color.White //Исправить цвета
            )
        }
        IconButton(onClick = { selectedIcon = 4 }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
                contentDescription = " ",
                tint = if (selectedIcon == 4) com.example.livepictures.ui.theme.Green else Color.White //Исправть, только окружность
            )
        }
    }
}

@Composable
private fun TopRow(modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.right_unactive),
                    contentDescription = " ",
                    tint = Color.Gray // Условия для изменения цвета
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.left_unactive),
                    contentDescription = " ",
                    tint = Color.Gray // Условия для изменения цвета
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(2f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.trash),
                    contentDescription = " ",
                    tint = Color.White
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.file_plus),
                    contentDescription = " ",
                    tint = Color.White
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.layers),
                    contentDescription = " ",
                    tint = Color.White
                )
            }
        }
        Row(modifier = Modifier.weight(1f)) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.stop_unactive),
                    contentDescription = " ",
                    tint = Color.Gray // Условия для изменения цвета
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.play_unactive),
                    contentDescription = " ",
                    tint = Color.Gray // Условия для изменения цвета
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CanvasTest(modifier: Modifier) {
    var ponints by rememberSaveable {
        mutableStateOf<List<Point>>(listOf())
    }
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = Color.White)
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        Log.d("Drag", "DragStart: $offset")
                        ponints =
                            ponints + Point(Offset(offset.x, offset.y), isStartedPosition = true)
                    },
                    onDrag = { change, dragAmount ->
                        Log.d("Drag", "onDrag: $change, drag: $dragAmount")
                        ponints = ponints + change.historical.map { historicalChange ->
                            Point(
                                offset = Offset(
                                    historicalChange.position.x,
                                    historicalChange.position.y
                                ), isStartedPosition = false
                            )
                        }
                    }
                )
            }
    ) {
        Log.d("Draw", "ponints: $ponints")
        if (ponints.isNotEmpty()) {
            val path = Path()
            ponints.forEach {
                if (it.isStartedPosition) {
                    path.moveTo(it.offset.x, it.offset.y)
                } else {
                    path.lineTo(it.offset.x, it.offset.y)
                }
            }
            drawPath(
                path = path,
                brush = Brush.linearGradient(listOf(Color.Red, Color.Blue)),
                style = Stroke(10.dp.toPx())
            )
        } else {
            Log.d("Draw", "No points available to draw.")
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(modifier = Modifier)
}


@Composable
fun Dp.toPx() = with(LocalDensity.current) {
    this@toPx.toPx()
}



