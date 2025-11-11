package com.example.beautyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    activeTab: String,
    onTabChange: (String) -> Unit,
    cartCount: Int = 0
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Home Tab
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (activeTab == "home") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Home",
                    fontSize = 10.sp,
                    color = if (activeTab == "home") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = activeTab == "home",
            onClick = { onTabChange("home") }
        )

        // Products Tab
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Eco,
                    contentDescription = "Products",
                    tint = if (activeTab == "search") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Products",
                    fontSize = 10.sp,
                    color = if (activeTab == "search") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = activeTab == "search",
            onClick = { onTabChange("search") }
        )

        // AR Scan Tab (center with special styling)
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .offset(y = (-6).dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "AR Scan",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    "AR Scan",
                    fontSize = 10.sp,
                    color = if (activeTab == "scan") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = activeTab == "scan",
            onClick = { onTabChange("scan") }
        )

        // Cart Tab
        NavigationBarItem(
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = Color(0xFFF472B6)) {
                                Text(
                                    text = cartCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = if (activeTab == "cart") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            label = {
                Text(
                    "Cart",
                    fontSize = 10.sp,
                    color = if (activeTab == "cart") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = activeTab == "cart",
            onClick = { onTabChange("cart") }
        )

        // Profile Tab
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (activeTab == "profile") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Profile",
                    fontSize = 10.sp,
                    color = if (activeTab == "profile") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = activeTab == "profile",
            onClick = { onTabChange("profile") }
        )
    }
}