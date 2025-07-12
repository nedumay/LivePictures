package com.example.livepictures.model

enum class BrushType(
    val displayName: String, 
    val strokeWidth: Float, 
    val strokeCap: androidx.compose.ui.graphics.StrokeCap,
    val alpha: Float = 1f,
    val texture: BrushTexture = BrushTexture.SOLID,
    val pressureSensitive: Boolean = false
) {
    PENCIL("Карандаш", 3f, androidx.compose.ui.graphics.StrokeCap.Round, 0.9f, BrushTexture.SOLID, false),
    PEN("Ручка", 2f, androidx.compose.ui.graphics.StrokeCap.Butt, 1f, BrushTexture.SOLID, false),
    MARKER("Маркер", 12f, androidx.compose.ui.graphics.StrokeCap.Round, 0.7f, BrushTexture.MARKER, false),
    BRUSH("Кисть", 20f, androidx.compose.ui.graphics.StrokeCap.Round, 0.8f, BrushTexture.BRUSH, true)
}

enum class BrushTexture {
    SOLID,      // Обычная сплошная линия
    MARKER,     // Полупрозрачная с эффектом растекания
    BRUSH       // Текстурированная кисть
} 