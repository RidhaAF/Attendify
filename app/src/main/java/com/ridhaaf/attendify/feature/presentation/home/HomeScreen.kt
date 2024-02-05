package com.ridhaaf.attendify.feature.presentation.home

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ridhaaf.attendify.core.utils.getCurrentDate
import com.ridhaaf.attendify.core.utils.getCurrentTime
import com.ridhaaf.attendify.core.utils.timeFormatter
import com.ridhaaf.attendify.feature.presentation.components.DefaultPhotoProfile
import com.ridhaaf.attendify.feature.presentation.components.DefaultProgressIndicator
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.feature.presentation.routes.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val userError = state.userError
    val attendanceError = state.attendanceError
    val context = LocalContext.current
    val refreshing = viewModel.isRefreshing.value
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.onEvent(HomeEvent.Refresh) },
    )
    val verticalScrollState = rememberScrollState()

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                defaultToast(
                    context,
                    "Permission denied, please allow the permission from Settings",
                )
            }
        }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val isPermissionGranted = ContextCompat.checkSelfPermission(
                    context, POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!isPermissionGranted) {
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    LaunchedEffect(key1 = userError, key2 = attendanceError) {
        if (userError.isNotBlank()) {
            defaultToast(context, userError)
        }

        if (attendanceError.isNotBlank()) {
            defaultToast(context, attendanceError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendify") },
                actions = {
                    SignOutButton(viewModel, state, navController)
                },
            )
        },
        floatingActionButton = {
            FloatingClockInOutButton(state, navController, context)
        },
    ) {
        if (state.isUserLoading) {
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
                    .verticalScroll(verticalScrollState)
                    .padding(it),
            ) {
                HomeContent(state)
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
private fun SignOutButton(
    viewModel: HomeViewModel,
    state: HomeState,
    navController: NavController?,
) {
    IconButton(onClick = { viewModel.onEvent(HomeEvent.SignOut) }) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Logout,
            contentDescription = "Sign Out",
        )
    }

    LaunchedEffect(key1 = state.signOutSuccess) {
        if (state.signOutSuccess) {
            navController?.navigate(Routes.SIGN_IN) {
                popUpTo(Routes.HOME) {
                    inclusive = true
                }
            }
        }
    }
}

@Composable
private fun FloatingClockInOutButton(
    state: HomeState,
    navController: NavController?,
    context: Context,
) {
    val time = getCurrentTime() in "09:00:00".."21:00:00"
    val status = state.userSuccess?.status ?: false
    val containerColor =
        if (status) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.primary

    if (state.isUserLoading) return
    FloatingActionButton(
        onClick = {
            if (time) {
                val dateTime = System.currentTimeMillis()
                navController?.navigate("location/$status/$dateTime")
            } else {
                defaultToast(context, "You can only clock in after 9:00 AM")
            }
        },
        containerColor = containerColor,
    ) {
        if (status) {
            Icon(
                imageVector = Icons.Rounded.AccessTime,
                contentDescription = "Clock Out",
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.MoreTime,
                contentDescription = "Clock In",
            )
        }
    }
}

@Composable
private fun HomeContent(state: HomeState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        UserSection(state)
        DefaultSpacer(size = 32)
        AttendanceSection(state)
    }
}

@Composable
private fun UserSection(state: HomeState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserPhoto(state)
        DefaultSpacer(horizontal = true)
        UserDisplayName(state)
    }
}

@Composable
private fun UserPhoto(state: HomeState) {
    val user = state.userSuccess

    user?.let {
        DefaultPhotoProfile(user = it, iconSize = 64.dp)
    }
}

@Composable
private fun UserDisplayName(state: HomeState) {
    val user = state.userSuccess
    val text = user?.displayName

    Text(
        text ?: "",
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}

@Composable
private fun AttendanceSection(state: HomeState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isAttendanceLoading) {
            DefaultProgressIndicator()
        } else {
            AttendanceContent(state)
        }
    }
}

@Composable
private fun AttendanceContent(state: HomeState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DateSection()
        TimeSection()
        ClockInOutSection(state)
    }
}

@Composable
private fun DateSection() {
    val currentDate = getCurrentDate()

    Text(currentDate)
}

@Composable
private fun TimeSection() {
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
private fun ClockInOutSection(state: HomeState) {
    val currentTime = System.currentTimeMillis()
    val gmt = 7 * 60 * 60 * 1000
    val localeTime = currentTime - gmt
    val time by remember { mutableLongStateOf(localeTime) }

    val user = state.userSuccess
    val attendance = state.attendanceSuccess

    val clockInTime = if (user?.status == true) attendance?.clockInDateTime ?: 0L else 0L
    val clockOutTime = if (user?.status == true) attendance?.clockOutDateTime ?: 0L else 0L

    val workingHours = if (user?.status == true) {
        if (clockOutTime == 0L) {
            time.minus(clockInTime)
        } else {
            clockOutTime.minus(clockInTime)
        }
    } else {
        0L
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ClockNotesColumn(
            icon = Icons.Rounded.AccessTime,
            time = timeFormatter(clockInTime),
            title = "Clock In",
        )
        ClockNotesColumn(
            icon = Icons.Rounded.MoreTime,
            time = timeFormatter(clockOutTime),
            title = "Clock Out",
        )
        ClockNotesColumn(
            icon = Icons.Rounded.Timelapse,
            time = timeFormatter(workingHours),
            title = "Working H",
        )
    }
}

@Composable
private fun ClockNotesColumn(icon: ImageVector, time: String, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.secondary,
        )
        Text(time)
        Text(
            title,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}