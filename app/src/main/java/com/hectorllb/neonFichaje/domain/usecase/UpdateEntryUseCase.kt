package com.hectorllb.neonFichaje.domain.usecase

import com.hectorllb.neonFichaje.domain.model.TimeEntry
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import javax.inject.Inject

class UpdateEntryUseCase @Inject constructor(
    private val repository: TimeRepository
) {
    suspend operator fun invoke(entry: TimeEntry) {
        repository.updateEntry(entry)
    }
}
