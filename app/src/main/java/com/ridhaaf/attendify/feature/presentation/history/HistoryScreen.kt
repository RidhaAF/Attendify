package com.ridhaaf.attendify.feature.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ridhaaf.attendify.core.utils.SortOption
import com.ridhaaf.attendify.core.utils.dateFormatter
import com.ridhaaf.attendify.core.utils.getLocaleTime
import com.ridhaaf.attendify.core.utils.timeFormatter
import com.ridhaaf.attendify.feature.data.models.attendance.Attendance
import com.ridhaaf.attendify.feature.presentation.components.Default404
import com.ridhaaf.attendify.feature.presentation.components.DefaultBackButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultProgressIndicator
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.ui.theme.DarkGreen
import com.ridhaaf.attendify.ui.theme.DarkRed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val error = state.historyError
    val context = LocalContext.current
    val refreshing = viewModel.isRefreshing.value
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.onEvent(HistoryEvent.Refresh()) },
    )

    LaunchedEffect(key1 = error) {
        if (error.isNotBlank()) {
            defaultToast(context, error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    BackButton(navController)
                },
                actions = {
                    var selectedSortHistoryItem by remember { mutableStateOf(SortOption.LATEST) }
                    val sortHistoryItems = listOf(SortOption.LATEST, SortOption.OLDEST)

                    SortHistoryButton(
                        sortHistoryItems,
                        selectedSortHistoryItem,
                        onSortHistoryItemSelected = { item ->
                            selectedSortHistoryItem = item
                            viewModel.onEvent(HistoryEvent.Refresh(selectedSortHistoryItem))
                        },
                    )
                },
            )
        },
    ) {
        if (state.isHistoryLoading) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                DefaultProgressIndicator()
            }
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
                    .padding(it),
            ) {
                HistoryContent(state)
                PullRefreshIndicator(
                    refreshing = refreshing,
                    state = pullRefreshState,
                    modifier = modifier.align(Alignment.TopCenter),
                )
            }
        }
    }
}

@Composable
private fun BackButton(navController: NavController?) {
    DefaultBackButton(navController)
}

@Composable
private fun SortHistoryButton(
    sortHistoryItems: List<SortOption>,
    selectedSortHistoryItem: SortOption,
    onSortHistoryItemSelected: (SortOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            Icons.AutoMirrored.Rounded.Sort,
            contentDescription = "Sort History",
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        sortHistoryItems.forEach { item ->
            DropdownMenuItem(
                {
                    val text = item.name.replaceAfter(
                        item.name.substring(0, 1),
                        item.name.substring(1).lowercase(),
                    )
                    Text(text)
                },
                onClick = {
                    onSortHistoryItemSelected(item)
                    expanded = false
                },
                trailingIcon = {
                    val isSelected = item == selectedSortHistoryItem

                    if (isSelected) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = item.name,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun HistoryContent(state: HistoryState) {
    val attendances = state.history.orEmpty()

    if (attendances.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(attendances.size) { i ->
                val attendance = attendances[i]

                HistoryCard(attendance)
            }
        }
    } else {
        Default404(subtitle = "No history found")
    }
}

@Composable
private fun HistoryCard(attendance: Attendance) {
    val localeTime = getLocaleTime()
    val time by remember { mutableLongStateOf(localeTime) }

    val clockInTime = attendance.clockInDateTime
    val clockOutTime = attendance.clockOutDateTime

    val gmt7Time = clockOutTime - 7 * 60 * 60 * 1000
    val workingHours = if (clockOutTime == 0L) {
        time.minus(clockInTime)
    } else {
        gmt7Time.minus(clockInTime)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp),
        ) {
            Text(
                dateFormatter(clockInTime),
                fontWeight = FontWeight.Bold,
            )
            DefaultSpacer(size = 4)
            HistoryRow("Clock In", timeFormatter(clockInTime), DarkGreen)
            HistoryRow("Clock Out", timeFormatter(clockOutTime), DarkRed)
            HistoryRow(
                "Working Hours",
                timeFormatter(workingHours),
                MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun HistoryRow(title: String, value: String, color: Color) {
    Row {
        Text(
            "$title: ",
            color = color,
        )
        Text(value)
    }
}