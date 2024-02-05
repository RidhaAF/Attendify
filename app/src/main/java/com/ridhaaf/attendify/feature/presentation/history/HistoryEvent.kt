package com.ridhaaf.attendify.feature.presentation.history

sealed class HistoryEvent {
    data object Refresh : HistoryEvent()
}
