package com.example.myapplication

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.myapplication.data.audio.AudioRecorder
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
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {

        composable("main") {
            MainScreenContent(
                onOpenSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainScreenContent(onOpenSettings: () -> Unit) {
    val context = LocalContext.current

    val recorder = remember { AudioRecorder() }
    val repository = remember { TunerRepositoryImpl(recorder) }
    val useCase = remember { DetectNoteUseCase(repository) }
    val viewModel: TunerViewModel = viewModel(
        factory = TunerViewModelFactory(
            detectNoteUseCase = useCase,
            recorder = recorder,
            context = LocalContext.current
        )
    )

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // ⚠️ ИСПРАВЛЕНО → после выдачи разрешения сразу включаем микрофон и анализ
    var micEnabled by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            recorder.prepareRecorder()
            micEnabled = true
            viewModel.toggleTuning()    // <<< ЗАПУСК АНАЛИЗА СРАЗУ
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
    var selectedString by remember { mutableStateOf("0") }

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

            // ⚠️ Кнопка микрофона — логика прежняя
            Button(
                onClick = {
                    if (!hasPermission) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        viewModel.toggleTuning()
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
                        if (!micEnabledExternal) R.drawable.mic else R.drawable.microaaa
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
                val note = tuning?.detectedNote?.name ?: "—"
                val freq = tuning?.detectedFrequency?.let { "%.1f Hz".format(it) } ?: ""

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(note, style = TextLarge1)
                    Text(freq, style = TextLarge2.copy(fontSize = 22.sp))
                }
            }
        }

        // Блок кнопок выбора струны
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val strings = listOf("E", "B", "G", "D", "A", "E")
                strings.forEachIndexed { index, s ->
                    val strNum = (index + 1).toString()
                    Button(
                        onClick = {
                            selectedString = if (selectedString == strNum) "0" else strNum
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
                                if (selectedString == strNum) Color.Green else Color.Transparent
                        )
                    ) {
                        Text(s, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomPanel() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color(21, 23, 28))
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.scale),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}
