package com.ridhaaf.attendify.feature.presentation.home

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ridhaaf.attendify.feature.presentation.components.DefaultButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultPhotoProfile
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.feature.presentation.routes.Routes
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val error = state.error
    val userError = state.userError
    val context = LocalContext.current
    val refreshing = viewModel.isRefreshing.value
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            viewModel.refresh()
        },
    )
    val verticalScrollState = rememberScrollState()

    LaunchedEffect(key1 = error, key2 = userError) {
        if (error.isNotBlank()) {
            defaultToast(context, error)
        }

        if (userError.isNotBlank()) {
            defaultToast(context, userError)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { UserSection(state) },
                actions = {
                    SignOutButton(viewModel, state, navController)
                },
            )
        },
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .verticalScroll(verticalScrollState)
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ClockSection()
                DefaultSpacer(size = 8)
                ClockInOutButton()
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
private fun UserSection(state: HomeState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        UserDisplayName(state)
        UserPhoto(state)
    }
}

@Composable
private fun UserDisplayName(state: HomeState) {
    val user = state.userSuccess
    val text = if (state.isUserLoading) "Loading..."
    else user?.displayName

    text?.let {
        Text(
            it,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
private fun UserPhoto(state: HomeState) {
    val user = state.userSuccess

    user?.let {
        DefaultPhotoProfile(user = it)
    }
}

@Composable
private fun SignOutButton(
    viewModel: HomeViewModel,
    state: HomeState,
    navController: NavController?,
) {
    IconButton(onClick = { viewModel.onEvent(HomeEvent.SignOut) }) {
        Icon(
            imageVector = Icons.Rounded.Logout,
            contentDescription = "Sign Out",
        )
    }

    LaunchedEffect(key1 = state.signOutSuccess) {
        if (state.signOutSuccess) {
            navController?.navigate(Routes.SIGN_IN)
        }
    }
}

@Composable
private fun ClockSection() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000)
        }
    }

    Text(
        currentTime,
        fontSize = 48.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun ClockInOutButton() {
    val clockInTime = isBetween("08:00:00", "17:00:00")
    val clockOutTime = isBetween("17:00:00", "22:00:00")
    val idle = !isBetween("08:00:00", "22:00:00")

    val enabled = !idle

    DefaultButton(
        onClick = {},
        enabled = enabled,
    ) {
        val buttonText = when {
            clockInTime -> "Clock In"
            clockOutTime -> "Clock Out"
            else -> "Clock In"
        }

        Text(buttonText)
    }
}

private fun getCurrentTime(): String {
    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return dateFormat.format(currentTime)
}

private fun isBetween(startTime: String, endTime: String): Boolean {
    val currentTime = getCurrentTime()
    return currentTime in startTime..endTime
}