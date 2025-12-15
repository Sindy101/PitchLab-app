package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О приложении") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(25, 25, 25),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(25, 25, 25)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Заголовок
            Text(
                text = "Гитарный тюнер",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Версия 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Разделитель
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            )

            // Информация о приложении
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                InfoItem(
                    title = "Назначение",
                    description = "Профессиональный тюнер для настройки гитары и других струнных инструментов. Приложение определяет ноту и точность настройки в центах."
                )

                Spacer(modifier = Modifier.height(24.dp))

                InfoItem(
                    title = "Как пользоваться",
                    description = "1. Дайте разрешение на использование микрофона\n" +
                            "2. Выберите струну для настройки\n" +
                            "3. Включите микрофон и играйте на струне\n" +
                            "4. Следите за индикатором и настраивайте струну"
                )

                Spacer(modifier = Modifier.height(24.dp))

                InfoItem(
                    title = "Особенности",
                    description = "• Точное определение частоты\n" +
                            "• Показ отклонения в центах\n" +
                            "• Поддержка разных строев\n" +
                            "• Простой и интуитивный интерфейс"
                )

                Spacer(modifier = Modifier.height(24.dp))

                InfoItem(
                    title = "Технологии",
                    description = "Приложение разработано с использованием:\n" +
                            "• Jetpack Compose\n" +
                            "• Kotlin Coroutines\n" +
                            "• Android Audio API\n" +
                            "• FFT анализ звука"
                )
            }

            // Разделитель
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            )

            // Контакты
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Контакты",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                InfoItem(
                    title = "Поддержка",
                    description = "support@tunerapp.com"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoItem(
                    title = "Веб-сайт",
                    description = "www.tunerapp.com"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoItem(
                    title = "Версия",
                    description = "1.0.0 (Build 1001)"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Копирайт
            Text(
                text = "© 2024 Tuner App. Все права защищены.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun InfoItem(
    title: String,
    description: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Green,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            lineHeight = 20.sp
        )
    }
}