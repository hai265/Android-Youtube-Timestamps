package com.hai265.timestamper

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hai265.timestamper.screens.FileController
import com.hai265.timestamper.screens.InsetsController
import com.hai265.timestamper.screens.Navigables
import com.hai265.timestamper.screens.OrientationController
import com.hai265.timestamper.screens.ShareTimestampsSheet
import com.hai265.timestamper.screens.editor.TimestampViewerScreen
import com.hai265.timestamper.screens.list.VideoListScreen
import com.hai265.timestamper.screens.signin.LogInScreen
import com.hai265.timestamper.screens.signin.SignUpScreen
import com.hai265.timestamper.theme.AppTheme

@Composable
fun App(
    insetsController: InsetsController,
    orientationController: OrientationController,
    fileController: FileController,
    shareTimestampSheet: ShareTimestampsSheet,
    customTheme: ColorScheme?
) {
    AppTheme(customColor = customTheme) {
        AppContent(insetsController, orientationController, fileController, shareTimestampSheet)
    }
}

const val SCREEN_TRANSITION_MILLIS = 500

@Composable
private fun AppContent(
    insetsController: InsetsController,
    orientationController: OrientationController,
    fileController: FileController,
    shareTimestampSheet: ShareTimestampsSheet,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Navigables.ListScreen,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(
                    SCREEN_TRANSITION_MILLIS
                )
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(
                    SCREEN_TRANSITION_MILLIS
                )
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(
                    SCREEN_TRANSITION_MILLIS
                )
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(
                    SCREEN_TRANSITION_MILLIS
                )
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
                fileController = fileController,
                shareTimestampSheet = shareTimestampSheet,
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
