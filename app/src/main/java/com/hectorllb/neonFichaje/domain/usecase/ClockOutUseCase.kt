package com.hectorllb.neonFichaje.domain.usecase

import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import java.time.Instant
import javax.inject.Inject

class ClockOutUseCase @Inject constructor(
    private val repository: TimeRepository
) {
    suspend operator fun invoke(endTime: Instant = Instant.now()): Result<Unit> {
        val openEntry = repository.getOpenEntryOneShot() ?: return Result.failure(Exception("No open session found"))

        val updatedEntry = openEntry.copy(endTime = endTime)
        repository.clockOut(updatedEntry)
        return Result.success(Unit)
    }
}
