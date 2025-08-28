package com.kanta.gameinformationandroid.ui.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kanta.gameinformationandroid.R
import com.kanta.gameinformationandroid.ui.components.AppDropdownMenu
import com.kanta.gameinformationandroid.ui.components.getDefaultAppMenuItems
import com.kanta.gameinformationandroid.ui.theme.GameInformationAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreenWithTopBar(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.page_title_top)) },
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
        // Surface は Scaffold のコンテンツエリアに配置しても良いし、
        // ServiceTitleScreen が既に背景色などを持っているなら直接配置しても良い
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Scaffoldからのpaddingを適用
            color = MaterialTheme.colorScheme.background
        ) {
            ServiceTitleScreen() // 元のコンテンツを表示
        }
    }
}

@Composable
fun ServiceTitleScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // このpaddingはTopAppBarの高さを考慮して調整が必要な場合もある
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.service_title),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainAppScreenWithTopBarPreview() {
    GameInformationAndroidTheme {
        val navController = rememberNavController()
        MainAppScreenWithTopBar(navController = navController)
    }
}