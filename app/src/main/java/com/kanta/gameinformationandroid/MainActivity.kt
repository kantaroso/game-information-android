package com.kanta.gameinformationandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType // NavTypeをインポート
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // navArgumentをインポート
import com.kanta.gameinformationandroid.ui.features.main.MainAppScreenWithTopBar
import com.kanta.gameinformationandroid.ui.features.maker.MakerListScreen
import com.kanta.gameinformationandroid.ui.features.maker.MakerDetailScreen // MakerDetailScreenをインポート
import com.kanta.gameinformationandroid.ui.theme.GameInformationAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

// ルート名を定義するオブジェクト
object AppDestinations {
    const val TOP_SCREEN_ROUTE = "top_screen"
    const val MAKER_LIST_SCREEN_ROUTE = "maker_list_screen"
    // ブランド詳細画面のベースルートと引数名
    const val MAKER_DETAIL_BASE_ROUTE = "maker_detail_screen"
    const val MAKER_CODE_ARG = "makerCode"
    // 引数付きのルート文字列を生成
    val MAKER_DETAIL_SCREEN_ROUTE = "$MAKER_DETAIL_BASE_ROUTE/{$MAKER_CODE_ARG}"

    // オプショナル: 詳細画面へのルートを生成するヘルパー関数
    fun createMakerDetailRoute(makerCode: String) = "$MAKER_DETAIL_BASE_ROUTE/$makerCode"
}

@Composable
fun MyAppNavHost(navController: NavController = rememberNavController()) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = AppDestinations.TOP_SCREEN_ROUTE
    ) {
        composable(AppDestinations.TOP_SCREEN_ROUTE) {
            MainAppScreenWithTopBar(navController = navController)
        }
        composable(AppDestinations.MAKER_LIST_SCREEN_ROUTE) {
            // MakerListScreenから詳細画面へ遷移できるように、navControllerを渡す
            // onMakerClick の具体的な実装は List.kt 側で行う
            MakerListScreen(
                navController = navController,
                onMakerClick = { maker ->
                    navController.navigate(AppDestinations.createMakerDetailRoute(maker.code))
                }
            )
        }
        // ブランド詳細画面へのルートを追加
        composable(
            route = AppDestinations.MAKER_DETAIL_SCREEN_ROUTE,
            arguments = listOf(navArgument(AppDestinations.MAKER_CODE_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            MakerDetailScreen(
                // makerCode = makerCode, // ViewModelがSavedStateHandleから取得するので不要
                navController = navController, // 戻るボタンなどのため
                onNavigateBack = { navController.popBackStack() }
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
                MyAppNavHost()
            }
        }
    }
}

