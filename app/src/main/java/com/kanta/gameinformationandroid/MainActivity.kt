package com.kanta.gameinformationandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kanta.gameinformationandroid.ui.features.main.MainAppScreenWithTopBar
import com.kanta.gameinformationandroid.ui.features.maker.MakerListScreen
import com.kanta.gameinformationandroid.ui.theme.GameInformationAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

// ルート名を定義するオブジェクト
object AppDestinations {
    const val TOP_SCREEN_ROUTE = "top_screen"
    const val MAKER_LIST_SCREEN_ROUTE = "maker_list_screen"
}

@Composable
fun MyAppNavHost(navController: NavController = rememberNavController()) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = AppDestinations.TOP_SCREEN_ROUTE
    ) {
        composable(AppDestinations.TOP_SCREEN_ROUTE) {
            MainAppScreenWithTopBar(navController = navController) // NavControllerを渡す
        }
        composable(AppDestinations.MAKER_LIST_SCREEN_ROUTE) {
            MakerListScreen(
                navController = navController,
                onMakerClick = {}
            )
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameInformationAndroidTheme {
                MyAppNavHost() // NavHostを呼び出す
            }
        }
    }
}
