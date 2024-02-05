package com.ridhaaf.attendify.feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ridhaaf.attendify.feature.data.models.auth.User

@Composable
fun DefaultPhotoProfile(
    modifier: Modifier = Modifier,
    user: User? = null,
    iconSize: Dp = 32.dp,
) {
    val photoModifier = modifier.clip(CircleShape)
    val isPhotoUrlValid =
        (user?.photoUrl != null) && (user.photoUrl?.isNotBlank() == true) && (user.photoUrl?.isNotEmpty() == true) && (user.photoUrl != "null") && (user.photoUrl != "NULL") && (user.photoUrl != "")

    if (isPhotoUrlValid) {
        AsyncImage(
            model = user?.photoUrl,
            contentDescription = user?.displayName,
            modifier = photoModifier.size(iconSize),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = photoModifier.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = user?.displayName ?: "User photo profile",
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}