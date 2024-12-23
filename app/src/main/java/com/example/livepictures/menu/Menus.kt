package com.example.livepictures.menu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.livepictures.R
import com.example.livepictures.mode.DrawMode
import com.example.livepictures.model.PathProperties
import com.example.livepictures.selectColors.ColorSlider
import com.example.livepictures.selectColors.ColorWheel
import com.example.livepictures.ui.theme.Blue
import com.example.livepictures.ui.theme.Gray
import com.example.livepictures.ui.theme.White
import kotlin.math.roundToInt

@Composable
fun DrawingPropertiesMenuApp(
    modifier: Modifier = Modifier,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    pathProperties: PathProperties,
    onPathPropertiesChange: (PathProperties) -> Unit,
    deleteFrame: () -> Unit,
    addFrame: () -> Unit,
    onStop: () -> Unit,
    onPlay: () -> Unit,
    uiVisibility: Boolean

){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        if(uiVisibility) {
            Row(modifier = Modifier.weight(1f)) {
                IconButton(onClick = {
                    onUndo()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.right_unactive),
                        contentDescription = " ",
                        tint = Color.White // Условия для изменения цвета
                    )
                }
                IconButton(onClick = {
                    onRedo()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.left_unactive),
                        contentDescription = " ",
                        tint = Color.White // Условия для изменения цвета
                    )
                }
            }
        }
        if(uiVisibility) {
            Row(
                modifier = Modifier
                    .weight(2f),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    deleteFrame()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.trash),
                        contentDescription = " ",
                        tint = Color.White
                    )
                }
                IconButton(onClick = {
                    addFrame()
                }) {
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
        }
        Row(modifier = Modifier.weight(1f)) {
            IconButton(onClick = {
                onStop()
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.stop_unactive),
                    contentDescription = " ",
                    tint = if (uiVisibility) Gray else Color.White // Условия для изменения цвета
                )
            }
            IconButton(onClick = {
                onPlay()
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.play_unactive),
                    contentDescription = " ",
                    tint = if (uiVisibility) White else Color.Gray // Условия для изменения цвета
                )
            }
        }
    }

}
@Composable
fun DrawingPropertiesMenuBottom(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    onPathPropertiesChange: (PathProperties) -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit,
    uiVisibility: Boolean
){
    val properties by rememberUpdatedState(newValue = pathProperties)

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(uiVisibility) {
            IconButton(onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Touch) {
                    DrawMode.Draw
                } else {
                    DrawMode.Touch
                }
                onDrawModeChanged(currentDrawMode)
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.pencel),
                    contentDescription = " ",
                    tint = if (currentDrawMode == DrawMode.Draw) com.example.livepictures.ui.theme.Green else Color.White
                )
            }
            IconButton(onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Brush) {
                    DrawMode.Draw
                } else {
                    DrawMode.Brush
                }
                onDrawModeChanged(currentDrawMode)
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.brush),
                    contentDescription = " ",
                    tint = if (currentDrawMode == DrawMode.Brush) com.example.livepictures.ui.theme.Green else Color.White
                )
            }
            IconButton(onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Erase) {
                    DrawMode.Draw
                } else {
                    DrawMode.Erase
                }
                onDrawModeChanged(currentDrawMode)
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.erase),
                    contentDescription = " ",
                    tint = if (currentDrawMode == DrawMode.Erase) com.example.livepictures.ui.theme.Green else Color.White
                )
            }
            IconButton(onClick = {

            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.instruments),
                    contentDescription = " ",
                    //tint = if (selectedIcon == 3) com.example.livepictures.ui.theme.Green else Color.White //Исправить цвета
                )
            }
            IconButton(onClick = { showColorDialog = !showColorDialog }) {
                ColorWheel(modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = {
                showPropertiesDialog = !showPropertiesDialog
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
                    contentDescription = " ",
                    tint = properties.color,
                    modifier = Modifier
                )
            }
        }
    }

    if (showColorDialog) {
        ColorSelectionDialog(
            properties.color,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                properties.color = color
            }
        )
    }

    if (showPropertiesDialog) {
        PropertiesMenuDialog(properties) {
            showPropertiesDialog = !showPropertiesDialog
        }
    }

}

@Composable
fun PropertiesMenuDialog(pathOption: PathProperties, onDismiss: () -> Unit) {

    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }
    var strokeCap by remember { mutableStateOf(pathOption.strokeCap) }
    var strokeJoin by remember { mutableStateOf(pathOption.strokeJoin) }

    Dialog(onDismissRequest = onDismiss) {

        Card(
            //elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Properties",
                    color = Color.Blue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )

                Canvas(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    val path = Path()
                    path.moveTo(0f, size.height / 2)
                    path.lineTo(size.width, size.height / 2)

                    drawPath(
                        color = pathOption.color,
                        path = path,
                        style = Stroke(
                            width = strokeWidth,
                            cap = strokeCap,
                            join = strokeJoin
                        )
                    )
                }

                Text(
                    text = "Stroke Width ${strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        pathOption.strokeWidth = strokeWidth
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {}
                )


                ExposedSelectionMenu(title = "Stroke Cap",
                    index = when (strokeCap) {
                        StrokeCap.Butt -> 0
                        StrokeCap.Round -> 1
                        else -> 2
                    },
                    options = listOf("Butt", "Round", "Square"),
                    onSelected = {
                        println("STOKE CAP $it")
                        strokeCap = when (it) {
                            0 -> StrokeCap.Butt
                            1 -> StrokeCap.Round
                            else -> StrokeCap.Square
                        }

                        pathOption.strokeCap = strokeCap

                    }
                )

                ExposedSelectionMenu(title = "Stroke Join",
                    index = when (strokeJoin) {
                        StrokeJoin.Miter -> 0
                        StrokeJoin.Round -> 1
                        else -> 2
                    },
                    options = listOf("Miter", "Round", "Bevel"),
                    onSelected = {
                        println("STOKE JOIN $it")

                        strokeJoin = when (it) {
                            0 -> StrokeJoin.Miter
                            1 -> StrokeJoin.Round
                            else -> StrokeJoin.Bevel
                        }

                        pathOption.strokeJoin = strokeJoin
                    }
                )
            }
        }
    }
}


@Composable
fun ColorSelectionDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (Color) -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red * 255) }
    var green by remember { mutableStateOf(initialColor.green * 255) }
    var blue by remember { mutableStateOf(initialColor.blue * 255) }
    var alpha by remember { mutableStateOf(initialColor.alpha * 255) }

    val color = Color(
        red = red.roundToInt(),
        green = green.roundToInt(),
        blue = blue.roundToInt(),
        alpha = alpha.roundToInt()
    )

    Dialog(onDismissRequest = onDismiss) {

        BoxWithConstraints(
            Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {

            val widthInDp = LocalDensity.current.run { maxWidth }


            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Color",
                    color = Blue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )

                // Initial and Current Colors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp, vertical = 20.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(
                                initialColor,
                                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(
                                color,
                                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                            )
                    )
                }

                ColorWheel(
                    modifier = Modifier
                        .width(widthInDp * .8f)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sliders
                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = "Red",
                    titleColor = Color.Red,
                    rgb = red,
                    onColorChanged = {
                        red = it
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = "Green",
                    titleColor = Color.Green,
                    rgb = green,
                    onColorChanged = {
                        green = it
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))

                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = "Blue",
                    titleColor = Color.Blue,
                    rgb = blue,
                    onColorChanged = {
                        blue = it
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                ColorSlider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    title = "Alpha",
                    titleColor = Color.Black,
                    rgb = alpha,
                    onColorChanged = {
                        alpha = it
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Buttons

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(0xffF3E5F5)),
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    TextButton(
                        onClick = onNegativeClick,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(text = "CANCEL")
                    }
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            onPositiveClick(color)
                        },
                    ) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

/**
 * Expandable selection menu
 * @param title of the displayed item on top
 * @param index index of selected item
 * @param options list of [String] options
 * @param onSelected lambda to be invoked when an item is selected that returns
 * its index.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedSelectionMenu(
    title: String,
    index: Int,
    options: List<String>,
    onSelected: (Int) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[index]) }
    var selectedIndex = remember { index }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(title) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                //backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false

            }
        ) {
            options.forEachIndexed { index: Int, selectionOption: String ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        selectedOptionText = selectionOption
                        expanded = false
                        selectedIndex = index
                        onSelected(selectedIndex)

                    },
                    text = { Text(text = selectionOption) },
                )
            }
        }
    }
}


@Composable
fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onPathPropertiesChange: (PathProperties) -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit
) {

    val properties by rememberUpdatedState(newValue = pathProperties)

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode

    Row(
        modifier = modifier
//            .background(getRandomColor())
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Touch) {
                    DrawMode.Draw
                } else {
                    DrawMode.Touch
                }
                onDrawModeChanged(currentDrawMode)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.pencel),
                contentDescription = null,
                tint = if (currentDrawMode == DrawMode.Touch) Color.Black else Color.LightGray
            )
        }
        IconButton(
            onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Erase) {
                    DrawMode.Draw
                } else {
                    DrawMode.Erase
                }
                onDrawModeChanged(currentDrawMode)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.erase),
                contentDescription = null,
                tint = if (currentDrawMode == DrawMode.Erase) Color.Black else Color.LightGray
            )
        }


        IconButton(onClick = { showColorDialog = !showColorDialog }) {
            ColorWheel(modifier = Modifier.size(24.dp))
        }

        IconButton(onClick = { showPropertiesDialog = !showPropertiesDialog }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
                contentDescription = null,
                tint = Color.LightGray
            )
        }

        IconButton(onClick = {
            onUndo()
        }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.right_unactive),
                contentDescription = null,
                tint = Color.LightGray
            )
        }

        IconButton(onClick = {
            onRedo()
        }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.left_unactive),
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }

    if (showColorDialog) {
        ColorSelectionDialog(
            properties.color,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                properties.color = color
            }
        )
    }

    if (showPropertiesDialog) {
        PropertiesMenuDialog(properties) {
            showPropertiesDialog = !showPropertiesDialog
        }
    }
}