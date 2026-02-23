package com.betting.manager.di

import android.content.Context
import com.betting.manager.database.AppDatabase
import com.betting.manager.database.SettingsDao
import com.betting.manager.database.VoucherDao
import com.betting.manager.database.EntryDao
import com.betting.manager.database.MasterHistoryDao
import com.betting.manager.database.ClearedLimitDao
import com.betting.manager.parsing.BetParser
import com.betting.manager.repository.BettingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase) = database.settingsDao()
    
    @Provides
    @Singleton
    fun provideVoucherDao(database: AppDatabase) = database.voucherDao()
    
    @Provides
    @Singleton
    fun provideEntryDao(database: AppDatabase) = database.entryDao()
    
    @Provides
    @Singleton
    fun provideMasterHistoryDao(database: AppDatabase) = database.masterHistoryDao()
    
    @Provides
    @Singleton
    fun provideClearedLimitDao(database: AppDatabase) = database.clearedLimitDao()
    
    @Provides
    @Singleton
    fun provideBetParser(): BetParser {
        return BetParser(directMultiplier = 80.0, rolledMultiplier = 500.0)
    }
    
    @Provides
    @Singleton
    fun provideBettingRepository(
        settingsDao: SettingsDao,
        voucherDao: VoucherDao,
        entryDao: EntryDao,
        masterHistoryDao: MasterHistoryDao,
        clearedLimitDao: ClearedLimitDao
    ): BettingRepository {
        return BettingRepository(
            settingsDao = settingsDao,
            voucherDao = voucherDao,
            entryDao = entryDao,
            masterHistoryDao = masterHistoryDao,
            clearedLimitDao = clearedLimitDao
        )
    }
}