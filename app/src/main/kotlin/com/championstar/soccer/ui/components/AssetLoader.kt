package com.championstar.soccer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size

/**
 * A reusable image loader that fetches high-quality placeholders for game assets.
 * Uses specific keywords to get relevant images (e.g., "football", "stadium", "avatar").
 */
@Composable
fun GameAssetImage(
    keyword: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://source.unsplash.com/random/400x400/?$keyword")
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build()
    )

    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )

        if (painter.state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Specifically loads a player avatar based on gender/style if needed.
 * For now, uses a generic "portrait" or "face" keyword.
 */
@Composable
fun PlayerAssetImage(
    modifier: Modifier = Modifier
) {
    GameAssetImage(
        keyword = "face,portrait,athlete", // specific keywords for avatars
        contentDescription = "Player Avatar",
        modifier = modifier
    )
}

/**
 * Loads a team logo or club crest placeholder.
 */
@Composable
fun ClubAssetImage(
    modifier: Modifier = Modifier
) {
    GameAssetImage(
        keyword = "shield,crest,logo,sport",
        contentDescription = "Club Logo",
        modifier = modifier
    )
}
