package cu.lidev.core.common.network

import cu.lidev.core.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val MAX_REQUEST_COUNT = 8
    private const val TIME_OUT_CONNECT = 25L
    private const val TIME_OUT_WRITE = 25L
    private const val TIME_OUT_READ = 25L

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideConverter(): Converter.Factory = GsonConverterFactory.create()

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    fun provideRequestHandler(): RequestHandler = RequestHandlerImpl()


}