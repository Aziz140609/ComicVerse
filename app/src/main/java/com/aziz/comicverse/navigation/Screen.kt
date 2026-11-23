package com.aziz.comicverse.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String? = null,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Explore : Screen(
        route = "explore",
        title = "Explore",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    ) {
        const val routeWithArgs = "explore?genre={genre}"
        fun createRoute(genre: String? = null): String {
            return if (genre != null) "explore?genre=$genre" else "explore"
        }
    }

    data object Library : Screen(
        route = "library",
        title = "Library",
        selectedIcon = Icons.Filled.CollectionsBookmark,
        unselectedIcon = Icons.Outlined.CollectionsBookmark
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    data object Rankings : Screen(
        route = "rankings",
        title = "Peringkat Manga"
    )

    data object Detail : Screen(
        route = "comic_detail/{comicId}"
    ) {
        fun createRoute(comicId: String): String = "comic_detail/$comicId"
    }

    data object Reader : Screen(
        route = "reader/{comicId}/{chapterId}"
    ) {
        fun createRoute(comicId: String, chapterId: String): String = "reader/$comicId/$chapterId"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Library,
    Screen.Settings
)
