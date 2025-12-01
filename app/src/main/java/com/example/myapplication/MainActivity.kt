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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.data.repository.AssetsTuningDataSource
import com.example.myapplication.data.repository.InstrumentRepository
import com.example.myapplication.data.repository.TunerRepositoryImpl
import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.domain.usecase.DetectNoteUseCase
import com.example.myapplication.presentation.viewmodel.TunerViewModel
import com.example.myapplication.presentation.viewmodel.TunerViewModelFactory
import com.example.myapplication.ui.theme.TextLarge1
import com.example.myapplication.ui.theme.TextLarge2

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
    onOpenSettings: () -> Unit
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
    micEnabledExternal: Boolean,
    onMicToggle: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var micEnabled by remember { mutableStateOf(false) }
    var posit by remember { mutableStateOf(90f) }

    // ================================
    // ✔ СТАБИЛИЗАЦИЯ ЧАСТОТЫ (Hz)
    // ================================
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


    // ================================
    // ✔ СТАБИЛИЗАЦИЯ CENTS
    // ================================
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


    // ==================================================
    // 🔥 СБРОС ПРИ ВЫБОРЕ СТРУНЫ
    // ==================================================
    val selectedString by viewModel.selectedStringIndex.collectAsState()

    LaunchedEffect(selectedString) {
        displayedFreq = 0.0
        displayedCents = 0.0

        lastStableFreq = System.currentTimeMillis()
        lastStableCents = System.currentTimeMillis()

        // Сброс индикатора в центр
        posit = 90f
    }
    // ==================================================


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
                }
            }

            Button(
                onClick = {
                    if (!hasPermission) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        micEnabled = !micEnabled
                    } else {
                        viewModel.toggleTuning()
                        micEnabled = !micEnabled
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
                    color = if (hasPermission) Color.Green else Color.Red,
                    shape = RoundedCornerShape(50)
                )
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            if (!hasPermission) {
                Text(
                    "Дай разрешение на микрофон!",
                    style = TextLarge2.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
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
                        onClick = { viewModel.selectString(num) },
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