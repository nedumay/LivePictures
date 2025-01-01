package com.example.livepictures.menu

import android.content.res.Resources.Theme
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
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.livepictures.R
import com.example.livepictures.mode.DrawMode
import com.example.livepictures.model.PathProperties
import com.example.livepictures.selectColors.ColorSlider
import com.example.livepictures.selectColors.ColorWheel
import com.example.livepictures.ui.theme.Black
import com.example.livepictures.ui.theme.Purple40
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

) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        if (uiVisibility) {
            Row(modifier = Modifier.weight(1f)) {
                IconButton(onClick = {
                    onUndo()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.right_unactive),
                        contentDescription = " ",
                        tint = Color.White
                    )
                }
                IconButton(onClick = {
                    onRedo()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.left_unactive),
                        contentDescription = " ",
                        tint = Color.White
                    )
                }
            }
        } /*
        if (uiVisibility) {
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
                    tint = if (uiVisibility) Gray else Color.White
                )
            }
            IconButton(onClick = {
                onPlay()
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.play_unactive),
                    contentDescription = " ",
                    tint = if (uiVisibility) White else Color.Gray
                )
            }
        }*/
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
) {
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
        if (uiVisibility) {
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
                    tint = if (currentDrawMode == DrawMode.Draw) colorScheme.primary else Color.White
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
                    tint = if (currentDrawMode == DrawMode.Brush) colorScheme.primary else Color.White
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
                    tint = if (currentDrawMode == DrawMode.Erase) colorScheme.primary else Color.White
                )
            }
            IconButton(onClick = {

            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.instruments),
                    contentDescription = " ",
                    tint = Color.Gray
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
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = stringResource(id = R.string.properties),
                    color = Color.White,
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
                    text = stringResource(id = R.string.stroke) + " " + stringResource(id = R.string.width) +  " ${strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    color = Color.White,
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


                ExposedSelectionMenu(
                    title = stringResource(id = R.string.stroke) + " " + stringResource(id = R.string.cap),
                    index = when (strokeCap) {
                        StrokeCap.Butt -> 0
                        StrokeCap.Round -> 1
                        else -> 2
                    },
                    options = listOf("Butt", "Round", "Square"),
                    onSelected = {

                        strokeCap = when (it) {
                            0 -> StrokeCap.Butt
                            1 -> StrokeCap.Round
                            else -> StrokeCap.Square
                        }

                        pathOption.strokeCap = strokeCap

                    }
                )

                ExposedSelectionMenu(title = stringResource(id = R.string.stroke) + " " + stringResource(id = R.string.join),
                    index = when (strokeJoin) {
                        StrokeJoin.Miter -> 0
                        StrokeJoin.Round -> 1
                        else -> 2
                    },
                    options = listOf("Miter", "Round", "Bevel"),
                    onSelected = {

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
                    text = stringResource(id = R.string.color),
                    color = Purple40,
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
                            .background(Black),
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Black),
                        onClick = {
                            onPositiveClick(color)
                        },
                    ) {
                        Text(text = stringResource(id = R.string.ok))
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

@Preview(showBackground = true)
@Composable
fun PreviewDrawingPropertiesMenuApp() {
    DrawingPropertiesMenuApp(
        onUndo = {},
        onRedo = {},
        pathProperties = PathProperties(),
        onPathPropertiesChange = {},
        deleteFrame = {},
        addFrame = {},
        onStop = {},
        onPlay = {},
        uiVisibility = true
    )
}

@Preview
@Composable
fun PreviewColorSelection() {
    ColorSelectionDialog(
        initialColor = Color.White,
        onDismiss = {},
        onNegativeClick = {},
        onPositiveClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDrawingPropertiesMenuBottom() {
    DrawingPropertiesMenuBottom(
        modifier = Modifier,
        pathProperties = PathProperties(),
        drawMode = DrawMode.Draw,
        onPathPropertiesChange = {},
        onDrawModeChanged = {},
        uiVisibility = true
    )
}
