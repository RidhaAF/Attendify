package com.ridhaaf.attendify.feature.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.feature.presentation.routes.Routes

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
                .pullRefresh(pullRefreshState)
                .verticalScroll(verticalScrollState)
                .padding(it),
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {}
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
    state.userSuccess?.let { user ->
        val text = if (state.isUserLoading) "Loading..."
        else "Welcome, ${user.displayName}"
        Text(text)
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