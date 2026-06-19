package com.hai265.timestamper.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hai265.timestamper.screens.InsetsController
import com.hai265.timestamper.screens.Navigables
import com.hai265.timestamper.screens.OrientationController
import com.hai265.timestamper.screens.editor.TimestampViewerScreen
import com.hai265.timestamper.screens.signin.LogInScreen
import com.hai265.timestamper.screens.signin.SignUpScreen
import com.hai265.timestamper.ui.screens.list.VideoListScreen

//TODO: Introduct multiple back stacks for the character list and support list
// reason: want to go  back to list page if I tap on the tab again
@Composable
fun App(
    windowSize: WindowWidthSizeClass,
    insetsController: InsetsController,
    orientationController: OrientationController,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Navigables.ListScreen,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(700)
            )
        }
    ) {
        composable<Navigables.ListScreen> {
            VideoListScreen(
                onTapVideo = { id ->
                    navController.navigateSingleTopTo(
                        Navigables.VideoScreen(
                            id
                        )
                    )
                },
                onTapSignUp = { navController.navigateSingleTopTo(Navigables.SignUpScreen) },
                windowSize = windowSize
            )
        }
        composable<Navigables.VideoScreen> {
            TimestampViewerScreen(
                insetsController = insetsController,
                orientationController = orientationController
            )
        }
        composable<Navigables.SignUpScreen> {
            SignUpScreen(
                onClickLogin = { navController.navigateSingleTopTo(Navigables.LogInScreen) },
                onSignUpSuccess = {
                    navController.navigateSingleTopTo(
                        Navigables.ListScreen
                    )
                })
        }
        composable<Navigables.LogInScreen> {
            LogInScreen(
                onClickSignUp = { navController.navigateSingleTopTo(Navigables.SignUpScreen) },
                onLogInSuccess = { navController.navigateSingleTopTo(Navigables.ListScreen) })
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


// Source - https://stackoverflow.com/a/66919245
// Posted by Константин Семочкин, modified by community. See post 'Timeline' for change history
// Retrieved 2026-04-01, License - CC BY-SA 4.0

@Composable
fun EnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = MutableTransitionState(
            initialState = false
        ).apply { targetState = true },
        modifier = Modifier,
        enter = slideInVertically(
            initialOffsetY = { -40 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut(),
    ) {
        content()
    }
}
