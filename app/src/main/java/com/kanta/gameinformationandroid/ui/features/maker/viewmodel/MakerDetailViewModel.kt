package com.kanta.gameinformationandroid.ui.features.maker.viewmodel // パッケージ名を指定のパスに合わせました

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanta.gameinformationandroid.data.model.MakerDetail
import com.kanta.gameinformationandroid.data.model.MakerVideo
import com.kanta.gameinformationandroid.data.repository.IMakerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakerDetailViewModel @Inject constructor(
    private val makerRepository: IMakerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val makerCode: String = savedStateHandle.get<String>("makerCode") ?: ""

    private val _makerDetail = MutableStateFlow<MakerDetail?>(null)
    val makerDetail: StateFlow<MakerDetail?> = _makerDetail.asStateFlow()

    private val _makerVideos = MutableStateFlow<List<MakerVideo>>(emptyList())
    val makerVideos: StateFlow<List<MakerVideo>> = _makerVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        if (makerCode.isNotEmpty()) {
            loadMakerDetails()
        } else {
            _error.value = "ブランドコードが指定されていません。"
            _isLoading.value = false // コードがない場合はロード終了
        }
    }

    private fun loadMakerDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            makerRepository.getMakerDetail(makerCode)
                .catch { e ->
                    _error.value = "ブランド詳細の取得に失敗しました: ${e.message}"
                    _isLoading.value = false
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { detail ->
                            _makerDetail.value = detail
                            // 詳細取得成功後に動画リストを取得 (ローディングは継続)
                            loadMakerVideos()
                        },
                        onFailure = { e ->
                            _error.value = "ブランド詳細の取得に失敗しました: ${e.message}"
                            _isLoading.value = false
                        }
                    )
                }
        }
    }

    private fun loadMakerVideos() {
        viewModelScope.launch {
            // ここでは isLoading を true に戻さず、詳細取得からの連続処理とみなす
            makerRepository.getMakerVideos(makerCode)
                .catch { e ->
                    // 動画取得エラーは詳細表示に影響しないようにエラーメッセージに追加する形も考慮
                    val currentError = _error.value
                    _error.value = if (currentError != null) {
                        "$currentError\n動画リストの取得に失敗しました: ${e.message}"
                    } else {
                        "動画リストの取得に失敗しました: ${e.message}"
                    }
                    _isLoading.value = false // すべてのロードが終わったとみなす
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { videos ->
                            _makerVideos.value = videos
                        },
                        onFailure = { e ->
                            val currentError = _error.value
                            _error.value = if (currentError != null) {
                                "$currentError\n動画リストの取得に失敗しました: ${e.message}"
                            } else {
                                "動画リストの取得に失敗しました: ${e.message}"
                            }
                        }
                    )
                    _isLoading.value = false // すべてのロードが終わったとみなす
                }
        }
    }
}
