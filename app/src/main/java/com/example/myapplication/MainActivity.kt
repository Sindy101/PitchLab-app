package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.Transliterator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.ui.theme.TextLarge1
import com.example.myapplication.ui.theme.TextLarge2
import kotlinx.coroutines.launch
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned


@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    content()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Получаем контекст, он нужен для проверки разрешений
    val context = LocalContext.current
    // Состояние для отслеживания, есть ли у нас разрешение
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Создаем лаунчер, который будет запрашивать разрешение
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Этот блок выполнится после того, как пользователь ответит на запрос
        hasPermission = isGranted
    }

    // Этот блок запустится один раз при отображении экрана
    LaunchedEffect(key1 = Unit) {
        if (!hasPermission) {
            // Если разрешения нет, запускаем запрос
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(red = 25, green = 25, blue = 25))
    ) {
        CircleContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            // Передаем статус разрешения в дочерний компонент
            hasPermission = hasPermission
        )
        BottomPanel()
    }
}
@Composable
fun CircleContent(modifier: Modifier = Modifier, hasPermission: Boolean) {
    var textV by remember { mutableStateOf("A") }
    var expanded by remember { mutableStateOf(false) }
    var buttonHeight = 0
    Box(
        modifier = modifier.background(Color(red = 25, green = 25, blue = 25))
        //contentAlignment = Alignment.Center
    ) {
        // Сам круг
        Button(
            onClick = {
                //textV = "B"
                expanded = true
            },
            modifier = Modifier.align(Alignment.TopCenter).padding(32.dp),

            //modifier.background(Color(red = 70, green = 29, blue = 30)),
        ) {
            Text("Кнопошка")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            DropdownMenuItem(
                text = {Text("Пункт 1")},
                onClick = {
                    textV = "Выбран пункт 1"
                    expanded = false
                }
            )
        }
        Box(
            Modifier
                .size(260.dp)
                .border(
                    width = 8.dp,
                    // Цвет круга зависит от того, есть ли разрешение
                    color = if (hasPermission) Color.Green else Color.Red,
                    shape = RoundedCornerShape(50)
                ).align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {

            // Отображаем разный текст в зависимости от наличия разрешения
            if (hasPermission) {
                Text(
                    text = textV,
                    style = TextLarge1
                )
                // Здесь можно инициализировать и использовать AudioRecorder,
                // так как разрешение уже получено.
            } else {
                Text(
                    text = "Дай разрешение на микрофон!",
                    style = TextLarge2.copy(fontSize = TextLarge2.fontSize / 2),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun BottomPanel() {
    Box (
        Modifier
            .fillMaxWidth()
            .background(Color(red = 21, green = 23, blue = 28))
            .height(200.dp),

        )
    {

        Image(
            painter = painterResource(id = R.mipmap.scale),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}