package com.aziz.comicverse.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aziz.comicverse.ui.components.ComicSectionHeader
import com.aziz.comicverse.ui.components.SearchInputField

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToExplore: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "ComicVerse",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SearchInputField(
            query = "",
            onQueryChange = {},
            placeholderText = "Cari komik, manga, atau author...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ComicSectionHeader(
            title = "Komik Populer",
            subtitle = "Paling banyak dibaca minggu ini",
            actionLabel = "Lihat Semua",
            onActionClick = { onNavigateToExplore(null) }
        )

        Box(modifier = Modifier.weight(1f))
    }
}
