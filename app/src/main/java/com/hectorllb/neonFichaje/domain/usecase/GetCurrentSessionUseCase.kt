package com.hectorllb.neonFichaje.domain.usecase

import com.hectorllb.neonFichaje.domain.model.TimeEntry
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentSessionUseCase @Inject constructor(
    private val repository: TimeRepository
) {
    operator fun invoke(): Flow<TimeEntry?> {
        return repository.getOpenEntry()
    }
}
