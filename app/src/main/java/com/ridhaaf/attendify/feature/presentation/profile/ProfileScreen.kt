package com.ridhaaf.attendify.feature.presentation.profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val userError = state.userError
    val uploadPhotoSuccess = state.uploadPhotoSuccess
    val uploadPhotoError = state.uploadPhotoError
    val deletePhotoSuccess = state.deletePhotoSuccess
    val deletePhotoError = state.deletePhotoError
    val context = LocalContext.current
    val refreshing = viewModel.isRefreshing.value
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.onEvent(ProfileEvent.Refresh) },
    )
    val verticalScrollState = rememberScrollState()
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val getContent =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri = it }
        }

    LaunchedEffect(key1 = userError) {
        if (userError.isNotBlank()) {
            defaultToast(context, userError)
        }
    }

    LaunchedEffect(key1 = uploadPhotoSuccess, key2 = uploadPhotoError) {
        if (uploadPhotoSuccess) {
            defaultToast(context, "Photo uploaded successfully")
        }

        if (uploadPhotoError.isNotBlank()) {
            defaultToast(context, uploadPhotoError)
        }
    }

    LaunchedEffect(key1 = deletePhotoSuccess, key2 = deletePhotoError) {
        if (deletePhotoSuccess) {
            defaultToast(context, "Photo deleted successfully")
        }

        if (uploadPhotoError.isNotBlank()) {
            defaultToast(context, uploadPhotoError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    DefaultBackButton(navController)
                },
                actions = {
                    SaveButton(viewModel, context, selectedImageUri)
                },
            )
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
                ProfileContent(state, modalBottomSheetState, getContent, selectedImageUri)
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
private fun SaveButton(
    viewModel: ProfileViewModel,
    context: Context,
    selectedImageUri: Uri? = null,
) {
    selectedImageUri?.let {
        IconButton(
            onClick = {
                viewModel.onEvent(
                    ProfileEvent.UploadPhoto(context, it)
                )
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Save Profile",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    state: ProfileState,
    modalBottomSheetState: SheetState,
    imagePicker: ManagedActivityResultLauncher<String, Uri?>,
    selectedImageUri: Uri?,
) {
    UserSection(state, modalBottomSheetState, imagePicker, selectedImageUri)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSection(
    state: ProfileState,
    modalBottomSheetState: SheetState,
    imagePicker: ManagedActivityResultLauncher<String, Uri?>,
    selectedImageUri: Uri?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserPhoto(state, modalBottomSheetState, imagePicker, selectedImageUri)
        DefaultSpacer()
        UserDisplayName(state)
        UserEmail(state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserPhoto(
    state: ProfileState,
    modalBottomSheetState: SheetState,
    imagePicker: ManagedActivityResultLauncher<String, Uri?>,
    selectedImageUri: Uri?,
) {
    val user = state.userSuccess
    selectedImageUri?.let { uri ->
        user?.photoUrl = uri.toString()
    }

    var isBottomSheetOpen by rememberSaveable { mutableStateOf(false) }

    Box {
        DefaultPhotoProfile(user = user, iconSize = 160.dp)
        IconButton(
            onClick = {
                isBottomSheetOpen = true
            }, modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    color = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape,
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Change profile photo",
            )
        }
    }
    UserPhotoBottomSheetSection(
        isBottomSheetOpen,
        modalBottomSheetState,
        onDismiss = {
            isBottomSheetOpen = false
        },
        imagePicker,
    )
}

@Composable
private fun UserPhotoBottomSheetContent(
    imagePicker: ManagedActivityResultLauncher<String, Uri?>,
) {
    val itemList = listOf(
        PhotoAction(
            icon = Icons.Rounded.Edit,
            title = "Edit photo",
            onClick = { imagePicker.launch("image/*") },
        ),
        PhotoAction(
            icon = Icons.Rounded.Delete,
            title = "Delete photo",
            onClick = { ProfileEvent.DeletePhoto },
        ),
    )

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(itemList) { item ->
            UserPhotoBottomSheetCard(
                icon = item.icon,
                title = item.title,
                onClick = item.onClick,
            )
        }
    }
}

@Composable
private fun UserPhotoBottomSheetCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Icon(imageVector = icon, contentDescription = title)
            DefaultSpacer(horizontal = true)
            Text(title)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserPhotoBottomSheetSection(
    isBottomSheetOpen: Boolean,
    modalBottomSheetState: SheetState,
    onDismiss: () -> Unit,
    imagePicker: ManagedActivityResultLauncher<String, Uri?>,
) {
    if (isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = modalBottomSheetState,
        ) {
            UserPhotoBottomSheetContent(imagePicker)
        }
    }
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
        color = MaterialTheme.colorScheme.secondary,
    )
}

data class PhotoAction(val icon: ImageVector, val title: String, val onClick: () -> Unit)