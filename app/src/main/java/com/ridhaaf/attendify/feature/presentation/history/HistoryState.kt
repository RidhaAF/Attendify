package com.ridhaaf.attendify.feature.presentation.history

import com.ridhaaf.attendify.feature.data.models.attendance.Attendance

data class HistoryState(
    val isHistoryLoading: Boolean = false,
    val history: List<Attendance>? = null,
    val historyError: String = "",
)
