package com.kanta.gameinformationandroid.di

import com.kanta.gameinformationandroid.data.repository.IMakerRepository
import com.kanta.gameinformationandroid.data.repository.MakerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton // MakerRepository が @Singleton なので、こちらも合わせることが一般的
    abstract fun bindMakerRepository(
        makerRepository: MakerRepository
    ): IMakerRepository
}