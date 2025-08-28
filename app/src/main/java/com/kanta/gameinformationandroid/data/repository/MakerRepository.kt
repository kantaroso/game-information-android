package com.kanta.gameinformationandroid.data.repository

import com.kanta.gameinformationandroid.data.model.Maker
import com.kanta.gameinformationandroid.data.remote.MakerApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface IMakerRepository { // インターフェースを定義するとテストしやすくなります
    fun getMakers(): Flow<Result<List<Maker>>>
}

@Singleton // Hiltを使用する場合
class MakerRepository @Inject constructor( // Hiltを使用する場合
    private val apiService: MakerApiService
) : IMakerRepository {

    override fun getMakers(): Flow<Result<List<Maker>>> = flow {
        try {
            val makers = apiService.getMakers() // suspend関数を呼び出し
            emit(Result.success(makers))
        } catch (e: Exception) {
            // ここで具体的なエラーハンドリングを行う (例: ネットワークエラー、サーバーエラーなど)
            e.printStackTrace() // ログにスタックトレースを出力
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO) // IOスレッドでネットワークリクエストを実行
}