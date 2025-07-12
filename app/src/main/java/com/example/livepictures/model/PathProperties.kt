package com.example.livepictures.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

class PathProperties(
    var strokeWidth: Float = 10f,
    var color: Color = com.example.livepictures.ui.theme.Blue,
    var alpha: Float = 1f,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
    var eraseMode: Boolean = false,
    var brushType: BrushType = BrushType.PENCIL,
    var brushTexture: BrushTexture = BrushTexture.SOLID,
    var pressureSensitive: Boolean = false
) {

    fun copy(
        strokeWidth: Float = this.strokeWidth,
        color: Color = this.color,
        alpha: Float = this.alpha,
        strokeCap: StrokeCap = this.strokeCap,
        strokeJoin: StrokeJoin = this.strokeJoin,
        eraseMode: Boolean = this.eraseMode,
        brushType: BrushType = this.brushType,
        brushTexture: BrushTexture = this.brushTexture,
        pressureSensitive: Boolean = this.pressureSensitive
    ) = PathProperties(
        strokeWidth, color, alpha, strokeCap, strokeJoin, eraseMode, 
        brushType, brushTexture, pressureSensitive
    )

    fun copyFrom(properties: PathProperties) {
        this.strokeWidth = properties.strokeWidth
        this.color = properties.color
        this.strokeCap = properties.strokeCap
        this.strokeJoin = properties.strokeJoin
        this.eraseMode = properties.eraseMode
        this.brushType = properties.brushType
        this.brushTexture = properties.brushTexture
        this.pressureSensitive = properties.pressureSensitive
    }
    
    fun applyBrushType(brushType: BrushType) {
        this.brushType = brushType
        this.strokeWidth = brushType.strokeWidth
        this.strokeCap = brushType.strokeCap
        this.alpha = brushType.alpha
        this.brushTexture = brushType.texture
        this.pressureSensitive = brushType.pressureSensitive
    }
}