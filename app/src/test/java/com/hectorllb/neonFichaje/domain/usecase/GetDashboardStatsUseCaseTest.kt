package com.hectorllb.neonFichaje.domain.usecase

import com.hectorllb.neonFichaje.domain.model.TimeEntry
import com.hectorllb.neonFichaje.domain.model.UserConfig
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class GetDashboardStatsUseCaseTest {

    private val fakeRepository = FakeTimeRepository()
    private val useCase = GetDashboardStatsUseCase(fakeRepository)

    @Test
    fun `invoke returns correct stats when no entries`() = runTest {
        val stats = useCase().first()
        
        assertEquals(0L, stats.completedTodaySeconds)
        assertEquals(0L, stats.completedWeekSeconds)
        assertEquals(false, stats.isClockedIn)
    }

    @Test
    fun `invoke correctly sums completed entries for today`() = runTest {
        val today = LocalDate.now()
        val now = Instant.now()
        
        // 1 hour entry
        fakeRepository.addEntry(
            TimeEntry(
                id = 1,
                startTime = now.minusSeconds(3600),
                endTime = now,
                date = today
            )
        )
        
        val stats = useCase().first()
        assertEquals(3600L, stats.completedTodaySeconds)
    }

    @Test
    fun `invoke identifies open session`() = runTest {
        val today = LocalDate.now()
        val now = Instant.now()
        
        fakeRepository.setOpenEntry(
            TimeEntry(
                id = 2,
                startTime = now,
                date = today
            )
        )
        
        val stats = useCase().first()
        assertEquals(true, stats.isClockedIn)
        assertNotNull(stats.currentSessionStartTime)
    }

    @Test
    fun `invoke calls correct repository method and excludes old entries`() = runTest {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val lastWeek = startOfWeek.minusWeeks(1)
        val now = Instant.now()

        // Entry from last week (1 hour)
        fakeRepository.addEntry(
            TimeEntry(
                id = 10,
                startTime = now.minusSeconds(3600*24*8), // 8 days ago approx
                endTime = now.minusSeconds(3600*24*8 - 3600),
                date = lastWeek
            )
        )

        // Entry from today (2 hours)
        fakeRepository.addEntry(
            TimeEntry(
                id = 11,
                startTime = now.minusSeconds(7200),
                endTime = now,
                date = today
            )
        )

        val stats = useCase().first()

        // Assert correctness
        assertEquals(7200L, stats.completedTodaySeconds)
        assertEquals(7200L, stats.completedWeekSeconds) // Should ignore last week

        // Verify Optimization
        assertTrue("Should call getEntriesForDateRange", fakeRepository.wasGetEntriesForDateRangeCalled)
        assertFalse("Should NOT call getAllEntries", fakeRepository.wasGetAllEntriesCalled)
    }
}

class FakeTimeRepository : TimeRepository {
    private val entriesFlow = MutableStateFlow<List<TimeEntry>>(emptyList())
    private val configFlow = MutableStateFlow(UserConfig(40.0, true))
    private val openEntryFlow = MutableStateFlow<TimeEntry?>(null)

    var wasGetAllEntriesCalled = false
    var wasGetEntriesForDateRangeCalled = false

    fun addEntry(entry: TimeEntry) {
        val current = entriesFlow.value.toMutableList()
        current.add(entry)
        entriesFlow.value = current
    }

    fun setOpenEntry(entry: TimeEntry?) {
        openEntryFlow.value = entry
    }

    override suspend fun clockIn(entry: TimeEntry) {
        setOpenEntry(entry)
    }

    override suspend fun clockOut(entry: TimeEntry) {
        setOpenEntry(null)
        addEntry(entry)
    }

    override suspend fun updateEntry(entry: TimeEntry) {
        // Simple mock implementation
    }

    override fun getOpenEntry(): Flow<TimeEntry?> = openEntryFlow

    override suspend fun getOpenEntryOneShot(): TimeEntry? = openEntryFlow.value

    override fun getEntriesForDateRange(start: LocalDate, end: LocalDate): Flow<List<TimeEntry>> {
        wasGetEntriesForDateRangeCalled = true
        return entriesFlow.map { list ->
            list.filter {
                !it.date.isBefore(start) && !it.date.isAfter(end)
            }
        }
    }

    override fun getAllEntries(): Flow<List<TimeEntry>> {
        wasGetAllEntriesCalled = true
        return entriesFlow
    }

    override fun getUserConfig(): Flow<UserConfig> = configFlow

    override suspend fun updateUserConfig(config: UserConfig) {
        configFlow.value = config
    }
}
