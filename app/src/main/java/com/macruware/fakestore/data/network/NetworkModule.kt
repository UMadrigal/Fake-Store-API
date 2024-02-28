package com.macruware.fakestore.data.network

import com.macruware.fakestore.data.RepositoryImpl
import com.macruware.fakestore.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient{
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideFakeStoreApiService(retrofit: Retrofit): FakeStoreApiService{
        return retrofit.create(FakeStoreApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(apiService: FakeStoreApiService): Repository{
        return RepositoryImpl(apiService)
    }
}