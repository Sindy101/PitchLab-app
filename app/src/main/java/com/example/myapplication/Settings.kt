//package com.example.myapplication
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontVariation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//@Composable
//fun FontVariation.Settings(onBack: () -> Unit) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(red = 40, green = 40, blue = 40)),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text("Это второй интерфейс!", color = Color.White, fontSize = 24.sp)
//
//            // Пример другого контента
//            Box(
//                modifier = Modifier
//                    .size(200.dp)
//                    .background(Color.Blue, RoundedCornerShape(16.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("Новый контент", color = Color.White)
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = onBack
//            ) {
//                Text("Вернуться назад")
//            }
//        }
//    }
//}