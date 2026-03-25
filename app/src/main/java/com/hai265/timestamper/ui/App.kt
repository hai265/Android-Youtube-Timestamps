package com.hai265.timestamper.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hai265.timestamper.R
import com.hai265.timestamper.ui.screens.ListScreen
import kotlinx.serialization.Serializable

sealed class Navigables {
    @Serializable
    object ListPage : Navigables()
}

enum class NavigationBarNavigables(
    val route: Navigables,
    val label: String,
    //TODO: Add icon
    @DrawableRes
    val icon: Int // drawable

) {
    ListPage(
        Navigables.ListPage,
        "Characters",
        //Icons taken from flaticon.com
        R.drawable.black_head_horse_side_view_with_horsehair
    )
}

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            BottomBar(onTabSelected = {
                navController.navigate(it.route)
            })
        },
        modifier = Modifier
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavGraph(navController, it)
        }
    }
}

//TODO: Introduct multiple back stacks for the character list and support list
// reason: want to go  back to list page if I tap on the tab again
@Composable
private fun NavGraph(
    navController: NavHostController,
    values: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Navigables.ListPage,
        modifier = Modifier.padding(
            bottom = values.calculateBottomPadding()
        )
    ) {
        composable<Navigables.ListPage> {
            ListScreen()
        }
    }
}

//TODO: Better icons
@Composable
fun BottomBar(onTabSelected: (NavigationBarNavigables) -> Unit) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(NavigationBarNavigables.ListPage.ordinal) }

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarNavigables.entries.forEachIndexed { index, tab ->
            NavigationBarItem(
                onClick = {
                    onTabSelected(tab)
                    selectedTabIndex = index
                },
                selected = index == selectedTabIndex,
                icon = {
                    Image(
                        painter = painterResource(tab.icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(tab.label) }
            )
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