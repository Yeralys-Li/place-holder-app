package cu.lidev.placeholderapp.data.di

import cu.lidev.core.common.network.RequestHandler
import cu.lidev.placeholderapp.data.api.ApiService
import cu.lidev.placeholderapp.data.repository.PostRepoImpl
import cu.lidev.placeholderapp.domain.repository.PostRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun providePostRepo(apiService: ApiService, requestHandler: RequestHandler): PostRepo =
        PostRepoImpl(apiService = apiService, requestHandler = requestHandler)
}