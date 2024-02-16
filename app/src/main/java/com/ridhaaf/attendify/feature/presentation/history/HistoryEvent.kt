package com.ridhaaf.attendify.feature.presentation.history

sealed class HistoryEvent {
    data class Refresh(val sort: String = "latest") : HistoryEvent()
}
