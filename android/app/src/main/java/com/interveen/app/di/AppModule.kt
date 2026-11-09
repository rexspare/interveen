package com.interveen.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.interveen.app.data.local.InterveenPreferences
import com.interveen.app.data.matcher.KeywordDomainMatcher
import com.interveen.app.domain.repository.ContentMatcher
import com.interveen.app.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.interveenDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "interveen_preferences",
)

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: InterveenPreferences): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindContentMatcher(impl: KeywordDomainMatcher): ContentMatcher
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.interveenDataStore
}
