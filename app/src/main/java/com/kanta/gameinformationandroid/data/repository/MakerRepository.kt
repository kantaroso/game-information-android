package com.kanta.gameinformationandroid.data.repository

import com.kanta.gameinformationandroid.data.model.Maker
import com.kanta.gameinformationandroid.data.model.MakerDetail // 追加
import com.kanta.gameinformationandroid.data.model.MakerVideo // 追加
import com.kanta.gameinformationandroid.data.remote.MakerApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface IMakerRepository {
    fun getMakers(): Flow<Result<List<Maker>>>
    fun getMakerDetail(makerCode: String): Flow<Result<MakerDetail>> // 追加
    fun getMakerVideos(makerCode: String): Flow<Result<List<MakerVideo>>> // 追加
}

@Singleton
class MakerRepository @Inject constructor(
    private val apiService: MakerApiService
) : IMakerRepository {

    override fun getMakers(): Flow<Result<List<Maker>>> = flow {
        try {
            val makers = apiService.getMakers()
            emit(Result.success(makers))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // 追加: ブランド詳細を取得するメソッドの実装
    override fun getMakerDetail(makerCode: String): Flow<Result<MakerDetail>> = flow {
        try {
            val makerDetail = apiService.getMakerDetail(makerCode)
            emit(Result.success(makerDetail))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // 追加: ブランドの動画リストを取得するメソッドの実装
    override fun getMakerVideos(makerCode: String): Flow<Result<List<MakerVideo>>> = flow {
        try {
            val makerVideos = apiService.getMakerVideos(makerCode)
            emit(Result.success(makerVideos))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}

