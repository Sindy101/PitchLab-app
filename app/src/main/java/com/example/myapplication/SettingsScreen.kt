package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(30, 30, 30))
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Назад")
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Настройки",
            fontSize = 28.sp,
            color = Color.White
        )

        // Здесь можно добавлять любые настройки
    }
}
