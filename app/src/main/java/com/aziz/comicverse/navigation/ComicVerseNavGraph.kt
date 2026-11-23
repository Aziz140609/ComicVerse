package com.aziz.comicverse.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aziz.comicverse.ui.detail.ComicDetailScreen
import com.aziz.comicverse.ui.explore.ExploreScreen
import com.aziz.comicverse.ui.home.HomeScreen
import com.aziz.comicverse.ui.library.LibraryScreen
import com.aziz.comicverse.ui.rankings.RankingsScreen
import com.aziz.comicverse.ui.reader.ReaderScreen
import com.aziz.comicverse.ui.settings.SettingsScreen

@Composable
fun ComicVerseNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide BottomBar on Detail & Reader screens
    val isBottomBarVisible = currentRoute == Screen.Home.route ||
            currentRoute?.startsWith("explore") == true ||
            currentRoute == Screen.Library.route ||
            currentRoute == Screen.Settings.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = when (screen) {
                            Screen.Explore -> currentRoute?.startsWith("explore") == true
                            else -> currentRoute == screen.route
                        }
                        val icon = if (isSelected) screen.selectedIcon else screen.unselectedIcon

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (screen == Screen.Home) {
                                    if (currentRoute != Screen.Home.route) {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Home.route) {
                                                inclusive = false
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                } else {
                                    val targetRoute = if (screen == Screen.Explore) Screen.Explore.createRoute(null) else screen.route
                                    val isAlreadyOnScreen = if (screen == Screen.Explore) {
                                        currentRoute?.startsWith("explore") == true
                                    } else {
                                        currentRoute == targetRoute
                                    }
                                    if (!isAlreadyOnScreen) {
                                        navController.navigate(targetRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            },
                            icon = {
                                if (icon != null) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = {
                                screen.title?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToDetail = { comicId ->
                        navController.navigate(Screen.Detail.createRoute(comicId))
                    },
                    onNavigateToExplore = { genre ->
                        navController.navigate(Screen.Explore.createRoute(genre))
                    },
                    onNavigateToRankings = {
                        navController.navigate(Screen.Rankings.route)
                    }
                )
            }

            composable(
                route = Screen.Explore.routeWithArgs,
                arguments = listOf(
                    navArgument("genre") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val genre = backStackEntry.arguments?.getString("genre")
                ExploreScreen(
                    onNavigateToDetail = { comicId ->
                        navController.navigate(Screen.Detail.createRoute(comicId))
                    },
                    initialGenre = genre,
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    onNavigateToDetail = { comicId ->
                        navController.navigate(Screen.Detail.createRoute(comicId))
                    },
                    onNavigateToReader = { comicId, chapterId ->
                        navController.navigate(Screen.Reader.createRoute(comicId, chapterId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(Screen.Rankings.route) {
                RankingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { comicId ->
                        navController.navigate(Screen.Detail.createRoute(comicId))
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("comicId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val comicId = backStackEntry.arguments?.getString("comicId") ?: ""
                ComicDetailScreen(
                    comicId = comicId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReader = { cId, chId ->
                        navController.navigate(Screen.Reader.createRoute(cId, chId))
                    }
                )
            }

            composable(
                route = Screen.Reader.route,
                arguments = listOf(
                    navArgument("comicId") { type = NavType.StringType },
                    navArgument("chapterId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val comicId = backStackEntry.arguments?.getString("comicId") ?: ""
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                ReaderScreen(
                    comicId = comicId,
                    chapterId = chapterId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
