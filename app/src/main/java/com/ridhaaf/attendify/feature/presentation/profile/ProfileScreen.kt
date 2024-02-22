package com.ridhaaf.attendify.feature.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ridhaaf.attendify.feature.presentation.components.DefaultBackButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultPhotoProfile
import com.ridhaaf.attendify.feature.presentation.components.DefaultProgressIndicator
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer
import com.ridhaaf.attendify.feature.presentation.components.defaultToast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val userError = state.userError
    val context = LocalContext.current
    val refreshing = viewModel.isRefreshing.value
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.onEvent(ProfileEvent.Refresh) },
    )
    val verticalScrollState = rememberScrollState()

    LaunchedEffect(key1 = userError) {
        if (userError.isNotBlank()) {
            defaultToast(context, userError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profile") }, navigationIcon = {
                DefaultBackButton(navController)
            })
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
                ProfileContent(state)
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
private fun ProfileContent(state: ProfileState) {
    UserSection(state)
}

@Composable
private fun UserSection(state: ProfileState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserPhoto(state)
        DefaultSpacer()
        UserDisplayName(state)
        UserEmail(state)
    }
}

@Composable
private fun UserPhoto(state: ProfileState) {
    val user = state.userSuccess

    DefaultPhotoProfile(user = user, iconSize = 120.dp)
}

@Composable
private fun UserDisplayName(state: ProfileState) {
    val user = state.userSuccess
    val text = user?.displayName

    Text(
        text ?: "",
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun UserEmail(state: ProfileState) {
    val user = state.userSuccess
    val text = user?.email

    Text(
        text ?: "",
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.secondary,
    )
}