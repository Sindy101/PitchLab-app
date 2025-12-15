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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuningTipsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Советы по настройке") },
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

            // Основные советы
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // 1. Общая информация
                TipCard(
                    title = "Основные принципы настройки",
                    tips = listOf(
                        "1. Начинайте настройку с самой толстой струны (6-ой)",
                        "2. Настраивайте в тихом помещении без фонового шума",
                        "3. Используйте качественный тюнер для точности",
                        "4. После настройки всех струн проверьте их еще раз"
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Проблемы и решения
                TipCard(
                    title = "Частые проблемы и решения",
                    tips = listOf(
                        "• Струна не держит строй - возможно, струна старая или плохо закреплена",
                        "• Фальшивые обертоны - проверьте высоту струн (анкерный стержень)",
                        "• Дребезжание - отрегулируйте высоту струн на бридже",
                        "• Слишком тугие струны - проверьте калибр струн и мензуру"
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Для электрогитары
                TipCard(
                    title = "Особенности настройки электрогитары",
                    tips = listOf(
                        "1. Настройте гитару без подключения к усилителю",
                        "2. Проверьте интонацию на 12-ом ладу",
                        "3. При использовании дисторшона настройте на чистый звук",
                        "4. Регулярно проверяйте состояние ладов и порожков"
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Для акустической гитары
                TipCard(
                    title = "Особенности настройки акустической гитары",
                    tips = listOf(
                        "• Натягивайте струны постепенно, не перекручивайте колки",
                        "• После замены струн дайте им время растянуться (30-60 минут)",
                        "• Проверяйте состояние бриджа и накладки грифа",
                        "• Учитывайте влажность и температуру помещения"
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Использование тюнера
                TipCard(
                    title = "Как правильно пользоваться тюнером",
                    tips = listOf(
                        "✓ Держите гитару в обычном положении при настройке",
                        "✓ Бейте по струне с нормальной силой - не слишком сильно и не слабо",
                        "✓ Ждите стабилизации показаний перед регулировкой",
                        "✓ Настраивайте по основной частоте, а не по обертонам"
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 6. Стандартный строй
                TipCard(
                    title = "Стандартный строй (EADGBE)",
                    tips = listOf(
                        "6 струна (самая толстая) - E (Ми) - 82.41 Гц",
                        "5 струна - A (Ля) - 110.00 Гц",
                        "4 струна - D (Ре) - 146.83 Гц",
                        "3 струна - G (Соль) - 196.00 Гц",
                        "2 струна - B (Си) - 246.94 Гц",
                        "1 струна (самая тонкая) - E (Ми) - 329.63 Гц"
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 7. Альтернативные строи
                TipCard(
                    title = "Популярные альтернативные строи",
                    tips = listOf(
                        "• Drop D: DADGBE - для тяжелой музыки",
                        "• Open G: DGDGBD - для слайд-гитары",
                        "• DADGAD: DADGAD - для фолк-музыки",
                        "• Полутон ниже: D#G#C#F#A#D# - для вокала"
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Заключение
                Text(
                    text = "Полезные советы:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val finalTips = listOf(
                    "Регулярно меняйте струны (раз в 1-3 месяца в зависимости от использования)",
                    "Храните гитару в чехле при нормальной влажности (45-55%)",
                    "Раз в год показывайте гитару профессионалу для полной настройки",
                    "Тренируйте слух - старайтесь настраивать на слух, используя тюнер только для проверки"
                )

                finalTips.forEachIndexed { index, tip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "•",
                            color = Color.Green,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = tip,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Предупреждение
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(40, 40, 40)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⚠️ Важно",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "При сильном отклонении от строя (более 50 центов) подкручивайте колки постепенно, по 10-20 центов за раз. Резкая настройка может порвать струну или повредить гриф.",
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TipCard(
    title: String,
    tips: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(40, 40, 40)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            tips.forEach { tip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "•",
                        color = Color.Green,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = tip,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}