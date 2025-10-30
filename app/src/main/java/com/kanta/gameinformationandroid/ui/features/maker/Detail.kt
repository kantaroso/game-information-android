package com.kanta.gameinformationandroid.ui.features.maker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kanta.gameinformationandroid.data.model.MakerDetail
import com.kanta.gameinformationandroid.data.model.MakerVideo
import com.kanta.gameinformationandroid.ui.components.AppDropdownMenu
import com.kanta.gameinformationandroid.ui.components.getDefaultAppMenuItems
import com.kanta.gameinformationandroid.ui.features.maker.viewmodel.MakerDetailViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakerDetailScreen(
    navController: NavController,
    viewModel: MakerDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val makerDetail by viewModel.makerDetail.collectAsState()
    val makerVideos by viewModel.makerVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    var selectedVideoId by remember { mutableStateOf<String?>(null) }

    selectedVideoId?.let { videoId ->
        YouTubePlayer(
            videoId = videoId,
            onDismiss = { selectedVideoId = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (isLoading && makerDetail == null) {
                        "" // 初期ローディング中は空文字
                    } else {
                        makerDetail?.name?.let { "ブランド詳細：$it" } ?: "ブランド詳細"
                    }
                    Text(text = titleText)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    AppDropdownMenu(menuItems = getDefaultAppMenuItems(navController))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && makerDetail == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = "エラー: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (makerDetail != null) {
                MakerDetailContent(
                    makerDetail = makerDetail!!,
                    makerVideos = makerVideos,
                    onVideoClick = { videoId ->
                        selectedVideoId = videoId
                    },
                    onLinkClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(Intent.createChooser(intent, "ブラウザで開く"))
                    }
                )
            } else {
                Text(
                    text = "情報が見つかりません。",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
        }
    }
}

// --- UI Content Composable ---
@Composable
fun MakerDetailContent(
    makerDetail: MakerDetail,
    makerVideos: List<MakerVideo>,
    onVideoClick: (String) -> Unit,
    onLinkClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = makerDetail.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        makerDetail.ohp?.takeIf { it.isNotBlank() }?.let { ohpUrl ->
            item {
                LinkText(
                    text = "公式サイト",
                    url = ohpUrl,
                    onClick = { onLinkClick(ohpUrl) }
                )
            }
        }

        makerDetail.twitterScreenName?.takeIf { it.isNotBlank() }?.let { screenName ->
            item {
                Text(
                    text = "Twitter",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                TwitterTimeline(
                    screenName = screenName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                )
            }
        }

        if (makerVideos.isNotEmpty()) {
            item {
                Text(
                    text = "関連動画",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(makerVideos, key = { it.id }) { video ->
                        VideoItem(video = video, onClick = { onVideoClick(video.id) })
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "関連動画はありません。",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TwitterTimeline(screenName: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
                }

                // 重要：スクロールを有効化
                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = true

                // NestedScrollingを有効化
                isNestedScrollingEnabled = true

                webViewClient = WebViewClient()
                loadUrl("https://twitter.com/$screenName")
            }
        },
        update = { webView ->
            // WebView内でのタッチイベントを処理
            webView.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        // WebViewがタッチイベントを処理することを親に通知
                        view.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        // タッチ終了時は親にイベントを返す
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
        }
    )
}

@Composable
fun LinkText(text: String, url: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    )
}

@Composable
fun VideoItem(video: MakerVideo, onClick: () -> Unit) {
    val thumbnailUrl = "https://img.youtube.com/vi/${video.id}/sddefault.jpg"
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun YouTubePlayer(videoId: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(videoId) {
        // YouTubeアプリで開く、なければブラウザで開く
        val youtubeAppUri = Uri.parse("vnd.youtube:$videoId")
        val youtubeWebUri = Uri.parse("https://www.youtube.com/watch?v=$videoId")

        val appIntent = Intent(Intent.ACTION_VIEW, youtubeAppUri)
        val webIntent = Intent(Intent.ACTION_VIEW, youtubeWebUri)

        try {
            context.startActivity(appIntent)
        } catch (e: Exception) {
            // YouTubeアプリがない場合はブラウザで開く
            context.startActivity(webIntent)
        }

        // 即座に閉じる
        onDismiss()
    }
}


// --- Preview ViewModels (for Jetpack Compose Preview) ---

interface IPreviewMakerDetailViewModel {
    val makerDetail: StateFlow<MakerDetail?>
    val makerVideos: StateFlow<List<MakerVideo>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
}

class PreviewMakerDetailViewModel : IPreviewMakerDetailViewModel {
    private val _makerDetail = MutableStateFlow<MakerDetail?>(
        MakerDetail(
            code = "prev_code",
            name = "プレビュー・ブランド",
            ohp = "https://example.com",
            twitterScreenName = "preview_twitter_user"
        )
    )
    override val makerDetail: StateFlow<MakerDetail?> = _makerDetail.asStateFlow()

    private val _makerVideos = MutableStateFlow(
        listOf(
            MakerVideo("vid1", "プレビュー動画 1 タイトル"),
            MakerVideo("vid2", "プレビュー動画 2 結構長めのタイトルで折り返しが発生するかどうかなどを確認するためのものです。")
        )
    )
    override val makerVideos: StateFlow<List<MakerVideo>> = _makerVideos.asStateFlow()

    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    override val error: StateFlow<String?> = MutableStateFlow<String?>(null).asStateFlow()
}

class PreviewLoadingMakerDetailViewModel : IPreviewMakerDetailViewModel {
    override val makerDetail: StateFlow<MakerDetail?> = MutableStateFlow<MakerDetail?>(null).asStateFlow()
    override val makerVideos: StateFlow<List<MakerVideo>> = MutableStateFlow(emptyList<MakerVideo>()).asStateFlow()
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(true).asStateFlow()
    override val error: StateFlow<String?> = MutableStateFlow<String?>(null).asStateFlow()
}

class PreviewErrorMakerDetailViewModel : IPreviewMakerDetailViewModel {
    override val makerDetail: StateFlow<MakerDetail?> = MutableStateFlow<MakerDetail?>(null).asStateFlow()
    override val makerVideos: StateFlow<List<MakerVideo>> = MutableStateFlow(emptyList<MakerVideo>()).asStateFlow()
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    override val error: StateFlow<String?> = MutableStateFlow("プレビュー用エラーメッセージ: データの取得に失敗しました。").asStateFlow()
}

class PreviewEmptyVideosMakerDetailViewModel : IPreviewMakerDetailViewModel {
    private val _makerDetail = MutableStateFlow<MakerDetail?>(
        MakerDetail(
            code = "prev_novid_code",
            name = "動画なしプレビューブランド",
            ohp = "https://example.com/novideos",
            twitterScreenName = "novid_twitter"
        )
    )
    override val makerDetail: StateFlow<MakerDetail?> = _makerDetail.asStateFlow()
    override val makerVideos: StateFlow<List<MakerVideo>> = MutableStateFlow(emptyList<MakerVideo>()).asStateFlow()
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    override val error: StateFlow<String?> = MutableStateFlow<String?>(null).asStateFlow()
}


// --- Preview Composable Wrapper ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakerDetailScreenForPreview(
    navController: NavController,
    viewModel: IPreviewMakerDetailViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val makerDetail by viewModel.makerDetail.collectAsState()
    val makerVideos by viewModel.makerVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // プレビュー用にモーダル表示の状態を管理
    var selectedVideoId by remember { mutableStateOf<String?>(null) }

    selectedVideoId?.let { videoId ->
        YouTubePlayer(
            videoId = videoId,
            onDismiss = { selectedVideoId = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (isLoading && makerDetail == null) {
                        "" // 初期ローディング中は空文字
                    } else {
                        makerDetail?.name?.let { "ブランド詳細：$it" } ?: "ブランド詳細"
                    }
                    Text(text = titleText)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "戻る")
                    }
                },
                actions = {
                    AppDropdownMenu(menuItems = getDefaultAppMenuItems(navController))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && makerDetail == null) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = "エラー: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (makerDetail != null) {
                MakerDetailContent(
                    makerDetail = makerDetail!!,
                    makerVideos = makerVideos,
                    onVideoClick = { videoId -> selectedVideoId = videoId },
                    onLinkClick = { /* Preview: No action */ }
                )
            } else {
                Text(
                    text = "情報が見つかりません。",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
        }
    }
}

// --- Preview Provider ---
class MakerDetailViewModelProvider : PreviewParameterProvider<IPreviewMakerDetailViewModel> {
    override val values = sequenceOf(
        PreviewMakerDetailViewModel(),
        PreviewLoadingMakerDetailViewModel(),
        PreviewErrorMakerDetailViewModel(),
        PreviewEmptyVideosMakerDetailViewModel()
    )
}

// --- Previews ---
@Preview(showBackground = true, name = "Default Preview")
@Composable
fun MakerDetailScreenPreview(
    @PreviewParameter(MakerDetailViewModelProvider::class) viewModel: IPreviewMakerDetailViewModel
) {
    MaterialTheme {
        MakerDetailScreenForPreview(
            navController = rememberNavController(),
            viewModel = viewModel
        )
    }
}