package ru.startandroid.todoapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ru.startandroid.todoapp.R

@Composable
fun mulishFontFamily() = FontFamily(
    Font(R.font.mulish_black, FontWeight.Black),
    Font(R.font.mulish_light, FontWeight.Light),
    Font(R.font.mulish_bold, FontWeight.Bold),
    Font(R.font.mulish_regular, FontWeight.Normal),
    Font(R.font.mulish_extrabold, FontWeight.ExtraBold),
    Font(R.font.mulish_semibold, FontWeight.SemiBold)
)
