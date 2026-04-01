package com.hai265.timestamper.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hai265.timestamper.ui.screens.editor.TimestampEditorScreen
import com.hai265.timestamper.ui.screens.list.VideoListScreen
import kotlinx.serialization.Serializable

sealed class Navigables {
    @Serializable
    object ListScreen

    @Serializable
    data class VideoScreen(val id: String) : Navigables()
}

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    Scaffold(
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavGraph(navController)
        }
    }
}

//TODO: Introduct multiple back stacks for the character list and support list
// reason: want to go  back to list page if I tap on the tab again
@Composable
private fun NavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Navigables.ListScreen,
    ) {
        composable<Navigables.ListScreen> {
            VideoListScreen(onTapVideo = { id ->
                navController.navigateSingleTopTo(
                    Navigables.VideoScreen(
                        id
                    )
                )
            })
        }
        composable<Navigables.VideoScreen> {
            TimestampEditorScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: Navigables) =
    this.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
    }