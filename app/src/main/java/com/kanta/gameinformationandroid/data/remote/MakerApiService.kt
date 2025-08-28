package com.kanta.gameinformationandroid.data.remote

import com.kanta.gameinformationandroid.data.model.Maker
import retrofit2.http.GET

interface MakerApiService {
    @GET("maker/list") // API仕様に合わせてエンドポイントを修正
    suspend fun getMakers(): List<Maker> // レスポンスは Maker オブジェクトのリスト

}
