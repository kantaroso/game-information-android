package com.kanta.gameinformationandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    // 必要であればonCreate等で初期化処理
}