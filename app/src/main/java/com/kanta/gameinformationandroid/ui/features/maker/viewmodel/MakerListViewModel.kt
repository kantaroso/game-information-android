package com.kanta.gameinformationandroid.ui.features.maker.viewmodel

import android.util.Log // Logをインポート
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanta.gameinformationandroid.data.model.Maker
import com.kanta.gameinformationandroid.data.model.MakerDetail
import com.kanta.gameinformationandroid.data.model.MakerVideo
// MakerApiService の代わりに IMakerRepository をインポート
import com.kanta.gameinformationandroid.data.repository.IMakerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch // catchオペレータをインポート
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class MakerListViewModel @Inject constructor(
    private val makerRepository: IMakerRepository // IMakerRepository を注入
) : ViewModel() {
    private val _makers = MutableStateFlow<List<Maker>>(emptyList())
    val makers: StateFlow<List<Maker>> = _makers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        Log.d("MakerListViewModel", "ViewModel initialized, calling loadMakers()")
        loadMakers()
    }

    open fun loadMakers() {
        Log.d("MakerListViewModel", "loadMakers() called")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            Log.d("MakerListViewModel", "Attempting to get makers from repository")

            makerRepository.getMakers() // RepositoryからFlowを取得
                .catch { exception -> // Flowのcatchオペレータでエラーを処理
                    Log.e("MakerListViewModel", "Error in getMakers flow (catch)", exception)
                    _error.value = "データの取得に失敗しました: ${exception.message}"
                    _makers.value = emptyList() // エラー時はリストをクリア
                    _isLoading.value = false // ローディング終了
                }
                .collect { result -> // Flowから結果を収集
                    result.fold(
                        onSuccess = { makerList ->
                            Log.d("MakerListViewModel", "Successfully loaded makers: ${makerList.size} items")
                            _makers.value = makerList
                            _isLoading.value = false // ローディング終了
                        },
                        onFailure = { exception ->
                            Log.e("MakerListViewModel", "Failed to load makers (onFailure)", exception)
                            _error.value = "データの取得に失敗しました(onFailure): ${exception.message}"
                            _makers.value = emptyList()
                            _isLoading.value = false // ローディング終了
                        }
                    )
                }
            // `flowOn(Dispatchers.IO)` はリポジトリ内で行われるため、ViewModel側で `withContext` は通常不要
            // ただし、collect の後の処理が重い場合は適切にディスパッチャを指定する
        }
    }
}

// Preview用のリポジトリモック
private class PreviewMakerRepository : IMakerRepository {
    override fun getMakers(): Flow<Result<List<Maker>>> = kotlinx.coroutines.flow.flowOf(
        Result.success(
            listOf(
                Maker(code = "prev_1", name = "プレビューメーカー１（株）"),
                Maker(code = "prev_2", name = "株式会社プレビューカンパニー"),
                Maker(code = "prev_3", name = "プレビューゲームズ"),
                Maker(code = "prev_4", name = "サンプルソフト"),
                Maker(code = "prev_5", name = "スタジオプレビュー")
            )
        )
    )

    override fun getMakerDetail(makerCode: String): Flow<Result<MakerDetail>> {
        return kotlinx.coroutines.flow.flowOf(
            Result.success(
                MakerDetail(
                    code = makerCode,
                    name = "プレビュー詳細",
                    ohp = "http://3rdeye.jp",
                    twitterScreenName = "3rdEye_tubuyaki",
                )
            )
        )
    }

    override fun getMakerVideos(makerCode: String): Flow<Result<List<MakerVideo>>> {
        return kotlinx.coroutines.flow.flowOf(
            Result.success(
                listOf(
                    MakerVideo(id = "r_7YDTEbAcg", title = "(仮)3rdEye 5th『レイルロアの略奪者』公式2ndOPMovie"),
                    MakerVideo(id = "P2G7zzggE-o", title = "(仮)3rdEye 5th『レイルロアの略奪者』公式1stOPMovie"),
                )
            )
        )
    }
}

// 通常のプレビュー用ViewModel
class PreviewMakerListViewModel : MakerListViewModel(PreviewMakerRepository())

// ローディング状態用ViewModel - MakerListViewModelを継承
class PreviewLoadingMakerListViewModel : MakerListViewModel(PreviewMakerRepository()) {
    init {
        // loadMakersをオーバーライドせず、初期状態をローディング中に設定
        viewModelScope.launch {
            _isLoading.value = true
            _makers.value = emptyList()
        }
    }

    override fun loadMakers() {
        // 何もしない（ローディング状態を維持）
    }

    // プロテクテッドプロパティにアクセスするための内部プロパティ
    private val _isLoading: MutableStateFlow<Boolean>
        get() = try {
            val field = MakerListViewModel::class.java.getDeclaredField("_isLoading")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            field.get(this) as MutableStateFlow<Boolean>
        } catch (e: Exception) {
            MutableStateFlow(true)
        }

    private val _makers: MutableStateFlow<List<Maker>>
        get() = try {
            val field = MakerListViewModel::class.java.getDeclaredField("_makers")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            field.get(this) as MutableStateFlow<List<Maker>>
        } catch (e: Exception) {
            MutableStateFlow(emptyList())
        }
}

// 空状態用ViewModel - MakerListViewModelを継承
class PreviewEmptyMakerListViewModel : MakerListViewModel(
    object : IMakerRepository {
        override fun getMakers(): Flow<Result<List<Maker>>> = kotlinx.coroutines.flow.flowOf(
            Result.success(emptyList())
        )

        override fun getMakerDetail(makerCode: String): Flow<Result<MakerDetail>> {
            return kotlinx.coroutines.flow.flowOf(Result.failure(NotImplementedError()))
        }

        override fun getMakerVideos(makerCode: String): Flow<Result<List<MakerVideo>>> {
            return kotlinx.coroutines.flow.flowOf(Result.failure(NotImplementedError()))
        }
    }
)

// エラー状態用ViewModel - MakerListViewModelを継承
class PreviewErrorMakerListViewModel : MakerListViewModel(
    object : IMakerRepository {
        override fun getMakers(): Flow<Result<List<Maker>>> = kotlinx.coroutines.flow.flowOf(
            Result.failure(Exception("ネットワークエラーが発生しました"))
        )

        override fun getMakerDetail(makerCode: String): Flow<Result<MakerDetail>> {
            return kotlinx.coroutines.flow.flowOf(Result.failure(Exception("ネットワークエラーが発生しました")))
        }

        override fun getMakerVideos(makerCode: String): Flow<Result<List<MakerVideo>>> {
            return kotlinx.coroutines.flow.flowOf(Result.failure(Exception("ネットワークエラーが発生しました")))
        }
    }
)
