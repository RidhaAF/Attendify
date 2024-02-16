package com.ridhaaf.attendify.feature.presentation.history

import com.ridhaaf.attendify.core.utils.SortOption

sealed class HistoryEvent {
    data class Refresh(val sort: SortOption = SortOption.LATEST) : HistoryEvent()
}
