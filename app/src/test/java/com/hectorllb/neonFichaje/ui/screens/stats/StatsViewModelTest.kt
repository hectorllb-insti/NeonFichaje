package com.hectorllb.neonFichaje.ui.screens.stats

import com.hectorllb.neonFichaje.domain.model.TimeEntry
import com.hectorllb.neonFichaje.domain.model.UserConfig
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldUseOptimizedDateRangeQuery() = runTest {
        // Setup Fake Repo
        val entries = generateDataset()
        val fakeRepository = FakeTimeRepository(entries)

        // Initialize ViewModel
        val viewModel = StatsViewModel(fakeRepository)

        // Allow coroutines to run to completion
        advanceUntilIdle()

        // Verification: Ensure the optimized repository method was called
        assert(fakeRepository.methodCalled == "getEntriesForDateRange") {
            "Expected 'getEntriesForDateRange' to be called, but was '${fakeRepository.methodCalled}'"
        }
    }

    private fun generateDataset(): List<TimeEntry> {
        val list = mutableListOf<TimeEntry>()
        val today = LocalDate.now()
        val startInstant = today.atTime(9, 0).toInstant(java.time.ZoneOffset.UTC)
        val endInstant = today.atTime(17, 0).toInstant(java.time.ZoneOffset.UTC)

        list.add(
            TimeEntry(
                id = 1,
                startTime = startInstant,
                endTime = endInstant,
                date = today,
                notes = "Entry 1"
            )
        )
        return list
    }
}

class FakeTimeRepository(private val entries: List<TimeEntry>) : TimeRepository {

    // Tracking method calls for verification
    var methodCalled = ""

    override fun getAllEntries(): Flow<List<TimeEntry>> = flow {
        methodCalled = "getAllEntries"
        emit(entries)
    }

    override fun getEntriesForDateRange(start: LocalDate, end: LocalDate): Flow<List<TimeEntry>> = flow {
        methodCalled = "getEntriesForDateRange"
        val filtered = entries.filter {
            !it.date.isBefore(start) && !it.date.isAfter(end)
        }
        emit(filtered)
    }

    // Unused methods
    override suspend fun clockIn(entry: TimeEntry) {}
    override suspend fun clockOut(entry: TimeEntry) {}
    override suspend fun updateEntry(entry: TimeEntry) {}
    override fun getOpenEntry(): Flow<TimeEntry?> = flow { emit(null) }
    override suspend fun getOpenEntryOneShot(): TimeEntry? = null
    override fun getUserConfig(): Flow<UserConfig> = flow { emit(UserConfig(40.0, true)) }
    override suspend fun updateUserConfig(config: UserConfig) {}
}
