package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Emerald80
import com.example.ui.theme.EmeraldDeep
import com.example.ui.viewmodel.FarmScreen

@Composable
fun BottomNav(
    currentScreen: FarmScreen,
    onScreenSelected: (FarmScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItem(
            screen = FarmScreen.DASHBOARD,
            label = "Overview",
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard,
            testTag = "nav_dashboard"
        ),
        NavigationItem(
            screen = FarmScreen.INVENTORY,
            label = "Flock",
            selectedIcon = Icons.Filled.Inventory2,
            unselectedIcon = Icons.Outlined.Inventory2,
            testTag = "nav_inventory"
        ),
        NavigationItem(
            screen = FarmScreen.GESTATION,
            label = "Gestation",
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth,
            testTag = "nav_gestation"
        ),
        NavigationItem(
            screen = FarmScreen.FINANCIAL,
            label = "Valuation",
            selectedIcon = Icons.Filled.Assessment,
            unselectedIcon = Icons.Outlined.Assessment,
            testTag = "nav_financial"
        ),
        NavigationItem(
            screen = FarmScreen.CHAT,
            label = "Field Log",
            selectedIcon = Icons.Filled.Forum,
            unselectedIcon = Icons.Outlined.Forum,
            testTag = "nav_chat"
        )
    )

    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("main_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = (currentScreen == item.screen) ||
                (currentScreen == FarmScreen.ANIMAL_DETAIL && item.screen == FarmScreen.INVENTORY) ||
                (currentScreen == FarmScreen.SCANNER && item.screen == FarmScreen.INVENTORY)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldDeep,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Emerald80,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

private data class NavigationItem(
    val screen: FarmScreen,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
