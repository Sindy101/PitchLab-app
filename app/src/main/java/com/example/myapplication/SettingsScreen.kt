package com.example.myapplication

import android.R
import android.icu.text.Transliterator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.viewmodel.TunerViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: TunerViewModel) {

    val currentInstrument by viewModel.instrument.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(30, 30, 30))
            .padding(top = 30.dp, end = 20.dp, start = 20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Настройки", fontSize = 28.sp, color = Color.White)

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("❌")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(Modifier.fillMaxWidth()) {

            Text("Выберите инструмент:", fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            InstrumentOption(
                text = "Гитара",
                isSelected = currentInstrument?.type?.name == "GUITAR",
                onSelect = {
                    viewModel.setInstrument("Guitar")
                }
            )

            InstrumentOption(
                text = "Укулеле",
                isSelected = currentInstrument?.type?.name == "UKULELE",
                onSelect = {
                    viewModel.setInstrument("Ukulele")
                }
            )

            InstrumentOption(
                text = "Балалайка",
                isSelected = currentInstrument?.type?.name == "BALALAIKA",
                onSelect = {
                    viewModel.setInstrument("Balalaika")
                }
            )
        }
    }
}

@Composable
fun InstrumentOption(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Круглая кнопка выбора
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (isSelected) Color.Green else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) Color.Green else Color.Gray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}
