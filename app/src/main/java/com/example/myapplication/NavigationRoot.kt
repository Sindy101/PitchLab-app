import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.MainScreenContent
import com.example.myapplication.SettingsScreen
import com.example.myapplication.AboutScreen
import com.example.myapplication.TuningTipsScreen  // Добавить этот импорт
import com.example.myapplication.data.audio.AudioRecorder
import com.example.myapplication.data.repository.AssetsTuningDataSource
import com.example.myapplication.data.repository.InstrumentRepository
import com.example.myapplication.data.repository.TunerRepositoryImpl
import com.example.myapplication.domain.usecase.DetectNoteUseCase
import com.example.myapplication.presentation.viewmodel.TunerViewModel
import com.example.myapplication.presentation.viewmodel.TunerViewModelFactory

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // --- Dependencies ---
    val recorder = remember { AudioRecorder() }
    val assetsDataSource = remember { AssetsTuningDataSource(context) }
    val instrumentRepository = remember { InstrumentRepository(assetsDataSource) }
    val tunerRepository = remember { TunerRepositoryImpl(recorder) }
    val detectUseCase = remember { DetectNoteUseCase(tunerRepository) }

    // --- Shared ViewModel Factory for all screens ---
    val factory = remember {
        TunerViewModelFactory(
            detectNoteUseCase = detectUseCase,
            recorder = recorder,
            context = context,
            instrumentRepository = instrumentRepository
        )
    }

    NavHost(
        navController = navController,
        startDestination = "main_graph"
    ) {
        mainNavigationGraph(navController, factory)
    }
}

private fun NavGraphBuilder.mainNavigationGraph(
    navController: androidx.navigation.NavController,
    factory: TunerViewModelFactory
) {
    navigation(
        startDestination = "main",
        route = "main_graph"
    ) {
        composable("main") { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("main_graph")
            }

            val viewModel: TunerViewModel = viewModel(
                parentEntry,
                factory = factory
            )

            MainScreenContent(
                viewModel = viewModel,
                onOpenSettings = { navController.navigate("settings") },
                onOpenAbout = { navController.navigate("about") },
                onOpenTuningTips = { navController.navigate("tuning_tips") }  // Добавить этот параметр
            )
        }

        composable("settings") { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("main_graph")
            }

            val viewModel: TunerViewModel = viewModel(
                parentEntry,
                factory = factory
            )

            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("about") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("tuning_tips") {  // Добавить этот экран
            TuningTipsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}