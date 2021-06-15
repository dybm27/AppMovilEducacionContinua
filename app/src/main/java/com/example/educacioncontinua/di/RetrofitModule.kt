package com.example.educacioncontinua.di

import com.example.educacioncontinua.data.RetrofitApi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val URL_BASE = "https://studentsprojects.cloud.ufps.edu.co/geduco/app/"

    @Provides
    @Reusable
    fun provideInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    @Provides
    @Reusable
    fun provideClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient().newBuilder().addInterceptor(interceptor).build()


    @Provides
    @Reusable
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()


    @Provides
    @Reusable
    fun provideRetrofit(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .build()


    @Provides
    @Reusable
    fun provideRetrofitApi(retrofit: Retrofit): RetrofitApi =
        retrofit.create(RetrofitApi::class.java)

}