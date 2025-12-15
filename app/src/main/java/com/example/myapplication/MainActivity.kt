package com.example.myapplication

import NavigationRoot
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.presentation.viewmodel.TunerViewModel
import com.example.myapplication.ui.theme.TextLarge1
import com.example.myapplication.ui.theme.TextLarge2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            NavigationRoot()
        }
    }
}

@Composable
fun MainScreenContent(
    viewModel: TunerViewModel,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTuningTips: () -> Unit
) {
    val context = LocalContext.current
    val recorder = viewModel.recorder

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var micEnabled by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            recorder.prepareRecorder()
            micEnabled = true
            viewModel.toggleTuning()
        }
    }

    val tuning by viewModel.tuningState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(25, 25, 25))
    ) {
        CircleContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            hasPermission = hasPermission,
            viewModel = viewModel,
            tuning = tuning,
            permissionLauncher = permissionLauncher,
            recorder = recorder,
            onOpenSettings = onOpenSettings,
            onOpenAbout = onOpenAbout,
            onOpenTuningTips = onOpenTuningTips,
            micEnabledExternal = micEnabled,
            onMicToggle = { micEnabled = it }
        )
        BottomPanel()
    }
}


@Composable
fun CircleContent(
    modifier: Modifier = Modifier,
    hasPermission: Boolean,
    viewModel: TunerViewModel,
    tuning: TuningResult?,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    recorder: AudioRecorder,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTuningTips: () -> Unit,
    micEnabledExternal: Boolean,
    onMicToggle: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var micEnabled = micEnabledExternal
    var posit by remember { mutableStateOf(90f) }

    var displayedFreq by remember { mutableStateOf(0.0) }
    var lastStableFreq by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(tuning?.detectedFrequency) {
        val raw = tuning?.detectedFrequency ?: 0.0
        val now = System.currentTimeMillis()

        val freq = if (raw == 2205.0) 0.0 else raw

        if (freq > 40) {
            displayedFreq = freq
            lastStableFreq = now
        } else {
            if (now - lastStableFreq > 800) {
                displayedFreq = 0.0
            }
        }
    }

    val freqText =
        if (displayedFreq > 0) "%.1f Hz".format(displayedFreq)
        else ""

    val noteText =
        if (displayedFreq > 0) tuning?.detectedNote?.name ?: "—"
        else "—"


    var displayedCents by remember { mutableStateOf(0.0) }
    var lastStableCents by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(tuning?.differenceCents) {
        val raw = tuning?.differenceCents ?: 0.0
        val now = System.currentTimeMillis()

        if (displayedFreq > 40 && raw != 0.0) {
            displayedCents = raw
            lastStableCents = now
        } else {
            if (now - lastStableCents > 800) {
                displayedCents = 0.0
            }
        }
    }

    val centsText =
        if (displayedCents == 0.0)
            "0 cents"
        else {
            val sign = if (displayedCents > 0) "+" else ""
            "${sign}${"%.1f".format(displayedCents)} cents"
        }



    val selectedString by viewModel.selectedStringIndex.collectAsState()

    LaunchedEffect(selectedString) {
        displayedFreq = 0.0
        displayedCents = 0.0

        lastStableFreq = System.currentTimeMillis()
        lastStableCents = System.currentTimeMillis()

        // Сброс индикатора в центр
        posit = 90f
    }

    val coroutineScope = rememberCoroutineScope()

    // Состояние для мигания
    var isBlinking by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = when {
            // Если нет разрешения - красный цвет
            !hasPermission -> {
                if (isBlinking) Color.Red else Color.Red.copy(alpha = 0.3f)
            }
            // Если есть разрешение и микрофон включен - зеленый
            hasPermission && micEnabled -> Color.Green
            // Если есть разрешение, микрофон выключен, выбрана струна - мигающий красный
            hasPermission && !micEnabled && selectedString > 0 -> {
                if (isBlinking) Color.Red else Color.Red.copy(alpha = 0.3f)
            }
            // Если есть разрешение, микрофон выключен, струна не выбрана - полупрозрачный красный
            hasPermission && !micEnabled -> Color.Red.copy(alpha = 0.3f)
            // Все остальные случаи (на всякий случай) - зеленый
            else -> Color.Green
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    // Функция для запуска мигания
    fun startBlinking() {
        coroutineScope.launch {
            // Сбрасываем состояние мигания
            isBlinking = false

            // Мигаем 3 раза (6 изменений состояния: вкл/выкл/вкл/выкл/вкл/выкл)
            repeat(9) { blinkIndex ->
                isBlinking = !isBlinking
                delay(300L) // Интервал 300ms между изменениями
            }
            // После 3 миганий оставляем без мигания
            isBlinking = false
        }
    }

    // Запускаем мигание при отсутствии разрешения
    LaunchedEffect(hasPermission) {
        if (!hasPermission) {
            startBlinking()
        }
    }

    // Запускаем мигание при выборе струны, когда микрофон выключен
    LaunchedEffect(selectedString) {
        if (hasPermission && !micEnabled && selectedString > 0) {
            startBlinking()
        }
    }

    // Запускаем мигание при выключении микрофона, если выбрана струна
    LaunchedEffect(micEnabled) {
        if (!micEnabled && selectedString > 0) {
            startBlinking()
        }
    }

    Box(modifier.background(Color(25, 25, 25))) {

        // Верхние кнопки
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, end = 20.dp)
        ) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.align(Alignment.TopStart),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Menu, contentDescription = null)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Настройки") },
                        onClick = {
                            expanded = false
                            onOpenSettings()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("О приложении") },
                        onClick = {
                            expanded = false
                            onOpenAbout()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Советы по настройке") },
                        onClick = {
                            expanded = false
                            onOpenTuningTips()
                        }
                    )
                }
            }

            Button(
                onClick = {
                    if (!hasPermission) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        // Обновляем состояние микрофона
                        onMicToggle(!micEnabled)
                    } else {
                        viewModel.toggleTuning()
                        // Обновляем состояние микрофона
                        onMicToggle(!micEnabled)

                        // Если микрофон выключили и выбрана струна - мигаем
                        if (micEnabled && selectedString > 0) {
                            startBlinking()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Image(
                    painter = painterResource(
                        if (!micEnabled) R.drawable.mic else R.drawable.microaaa
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Центральный круг
        Box(
            Modifier
                .size(260.dp)
                .border(
                    width = 8.dp,
                    color = animatedColor, // Используем анимированный цвет
                    shape = RoundedCornerShape(50)
                )
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 1. Нет разрешения - показываем текст и картинку falsemic.png
                !hasPermission -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Текст сверху
                        Text(
                            "Дай разрешение на микрофон!",
                            style = TextLarge2.copy(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Картинка falsemic.png под текстом
                        Image(
                            painter = painterResource(id = R.drawable.falsemic),
                            contentDescription = "Нет разрешения на микрофон",
                            modifier = Modifier
                                .size(100.dp), // Размер картинки
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                // 2. Есть разрешение, но микрофон выключен - показываем картинку falsemic.png
                !micEnabled -> {
                    Image(
                        painter = painterResource(id = R.drawable.falsemic),
                        contentDescription = "Микрофон выключен",
                        modifier = Modifier
                            .size(180.dp) // Размер картинки
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                // 3. Есть разрешение и микрофон включен - показываем информацию о настройке
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(noteText, style = TextLarge1)
                        Text(freqText, style = TextLarge2.copy(fontSize = 22.sp))
                        Text(
                            centsText,
                            style = TextLarge2.copy(fontSize = 18.sp, color = Color.LightGray)
                        )
                    }
                }
            }
        }

        // Кнопки выбора струны
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 45.dp),
            contentAlignment = Alignment.Center
        ) {
            val instrument by viewModel.instrument.collectAsState()

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                instrument?.strings?.forEachIndexed { index, stringNote ->
                    val num = index + 1

                    Button(
                        onClick = {
                            viewModel.selectString(num)
                            // Если микрофон выключен - запускаем мигание
                            if (!micEnabled) {
                                startBlinking()
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Green,
                                shape = CircleShape
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (selectedString == num) Color.Green else Color.Transparent
                        ),
                        shape = CircleShape
                    ) {
                        Text(
                            stringNote.name.replace(Regex("[0-9]"), ""),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp


    val centerPosition = screenWidthDp / 2
    // Ползунок
    tuning?.differenceCents?.let { cents ->
        // Ограничиваем значение 'cents' диапазоном от -100f до 100f
        val offsetX = cents.coerceIn(-150.0, 150.0)

        Box(
            Modifier.offset(x = centerPosition+offsetX.dp)
                .width(5.dp)
                .padding(bottom = 1.dp)
                .background(Color.Green)
                .height(20.dp)
        )
    }
}


@Composable
fun BottomPanel() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color(21, 23, 28))
            .height(175.dp)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.scale),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}