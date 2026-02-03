package com.hectorllb.neonFichaje.di

import android.app.Application
import androidx.room.Room
import com.hectorllb.neonFichaje.data.database.AppDatabase
import com.hectorllb.neonFichaje.data.database.dao.ScheduleDao
import com.hectorllb.neonFichaje.data.database.dao.TimeEntryDao
import com.hectorllb.neonFichaje.data.database.dao.UserConfigDao
import com.hectorllb.neonFichaje.data.repository.ScheduleRepositoryImpl
import com.hectorllb.neonFichaje.data.repository.TimeRepositoryImpl
import com.hectorllb.neonFichaje.domain.repository.ScheduleRepository
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "neon_fichaje_db"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .build()
    }

    @Provides
    @Singleton
    fun provideTimeEntryDao(db: AppDatabase): TimeEntryDao {
        return db.timeEntryDao()
    }

    @Provides
    @Singleton
    fun provideUserConfigDao(db: AppDatabase): UserConfigDao {
        return db.userConfigDao()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(db: AppDatabase): ScheduleDao {
        return db.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideTimeRepository(
        timeEntryDao: TimeEntryDao,
        userConfigDao: UserConfigDao
    ): TimeRepository {
        return TimeRepositoryImpl(timeEntryDao, userConfigDao)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(dao: ScheduleDao): ScheduleRepository {
        return ScheduleRepositoryImpl(dao)
    }
}
