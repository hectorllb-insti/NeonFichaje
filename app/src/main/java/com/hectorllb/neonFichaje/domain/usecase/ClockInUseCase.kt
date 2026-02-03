package com.hectorllb.neonFichaje.domain.usecase

import com.hectorllb.neonFichaje.domain.model.TimeEntry
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class ClockInUseCase @Inject constructor(
    private val repository: TimeRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val openEntry = repository.getOpenEntryOneShot()
        if (openEntry != null) {
            return Result.failure(Exception("Already clocked in"))
        }

        val now = Instant.now()
        val today = LocalDate.now()
        
        val newEntry = TimeEntry(
            startTime = now,
            date = today
        )
        
        repository.clockIn(newEntry)
        return Result.success(Unit)
    }
}
