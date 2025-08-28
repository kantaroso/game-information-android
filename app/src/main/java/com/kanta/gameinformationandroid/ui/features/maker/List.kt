package com.kanta.gameinformationandroid.ui.features.maker

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kanta.gameinformationandroid.R // Rクラスのインポートを忘れずに
import com.kanta.gameinformationandroid.data.model.Maker
import com.kanta.gameinformationandroid.ui.components.AppDropdownMenu
import com.kanta.gameinformationandroid.ui.components.getDefaultAppMenuItems
import com.kanta.gameinformationandroid.ui.theme.GameInformationAndroidTheme
import com.kanta.gameinformationandroid.ui.features.maker.viewmodel.MakerListViewModel
import com.kanta.gameinformationandroid.ui.features.maker.viewmodel.PreviewEmptyMakerListViewModel
import com.kanta.gameinformationandroid.ui.features.maker.viewmodel.PreviewErrorMakerListViewModel
import com.kanta.gameinformationandroid.ui.features.maker.viewmodel.PreviewLoadingMakerListViewModel
import com.kanta.gameinformationandroid.ui.features.maker.viewmodel.PreviewMakerListViewModel

// ViewModel (実際のデータ取得ロジックはViewModelに持たせるのが一般的)
// ここでは仮のViewModelとデータを使用
// 実際にはHiltなどを使ってViewModelを注入します
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakerListScreen(
    navController: NavController,
    onMakerClick: (Maker) -> Unit,
    viewModel: MakerListViewModel = hiltViewModel() // 仮のViewModel
) {
    val makers by viewModel.makers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.page_title_maker_list)) }, // TopAppBarのタイトル
                actions = {
                    // AppDropdownMenu が未解決の場合はインポートを確認
                    AppDropdownMenu(menuItems = getDefaultAppMenuItems(navController))
                },
                colors = TopAppBarDefaults.topAppBarColors( // オプション: TopAppBarの色設定
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer // ナビゲーションアイコンを使う場合
                )
            )
        }
    ) { innerPadding ->
        if (makers.isEmpty()) {
            EmptyState(modifier = Modifier.padding(innerPadding))
        } else {
            MakerList(
                makers = makers,
                onMakerClick = onMakerClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun MakerList(
    makers: List<Maker>,
    onMakerClick: (Maker) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(makers, key = { maker -> maker.code }) { maker ->
            MakerListItem(
                maker = maker,
                onClick = { onMakerClick(maker) }
            )
        }
    }
}

@Composable
fun MakerListItem(
    maker: Maker,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ここにアイコンなどを追加することも可能
            Text(
                text = maker.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.maker_list_not_found), // strings.xmlに定義
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Gray
    )
}


// --- Preview ---
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun MakerListScreenPreview() {
    GameInformationAndroidTheme {
        val navController = rememberNavController()
        MakerListScreen(
            onMakerClick = {},
            viewModel = PreviewMakerListViewModel(),
            navController = navController
        )
    }
}

// ローディング状態のプレビュー
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Maker List - Loading")
@Composable
fun MakerListScreenLoadingPreview() {
    GameInformationAndroidTheme {
        val navController = rememberNavController()
        MakerListScreen(
            onMakerClick = {},
            viewModel = PreviewLoadingMakerListViewModel(),
            navController = navController
        )
    }
}

// 空状態のプレビュー
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Maker List - Empty")
@Composable
fun MakerListScreenEmptyPreview() {
    GameInformationAndroidTheme {
        val navController = rememberNavController()
        MakerListScreen(
            onMakerClick = {},
            viewModel = PreviewEmptyMakerListViewModel(),
            navController = navController
        )
    }
}

// エラー状態のプレビュー
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Maker List - Error")
@Composable
fun MakerListScreenErrorPreview() {
    GameInformationAndroidTheme {
        val navController = rememberNavController()
        MakerListScreen(
            onMakerClick = {},
            viewModel = PreviewErrorMakerListViewModel(),
            navController = navController
        )
    }
}