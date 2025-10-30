package com.kanta.gameinformationandroid.data.remote

import com.kanta.gameinformationandroid.data.model.Maker // MakerListのレスポンス
import com.kanta.gameinformationandroid.data.model.MakerDetail // MakerDetailのレスポンス
import com.kanta.gameinformationandroid.data.model.MakerVideo // MakerVideoのレスポンス
import retrofit2.http.GET
import retrofit2.http.Path // @Pathアノテーションのために追加

interface MakerApiService {
    @GET("maker/list")
    suspend fun getMakers(): List<Maker>

    @GET("maker/detail/{code}") // 新しいエンドポイント
    suspend fun getMakerDetail(@Path("code") makerCode: String): MakerDetail

    @GET("maker/videos/{code}") // 新しいエンドポイント
    suspend fun getMakerVideos(@Path("code") makerCode: String): List<MakerVideo>
}