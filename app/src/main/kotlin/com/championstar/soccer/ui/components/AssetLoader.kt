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
 * Uses Picsum as a reliable fallback since Unsplash Source is deprecated.
 */
@Composable
fun GameAssetImage(
    keyword: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Generates a consistent random image based on the keyword hash to simulate "persistent" assets
    val seed = keyword.hashCode()
    val url = "https://picsum.photos/seed/$seed/400/400"

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
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
 */
@Composable
fun PlayerAssetImage(
    modifier: Modifier = Modifier
) {
    GameAssetImage(
        keyword = "player_avatar_placeholder",
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
        keyword = "club_logo_placeholder",
        contentDescription = "Club Logo",
        modifier = modifier
    )
}
