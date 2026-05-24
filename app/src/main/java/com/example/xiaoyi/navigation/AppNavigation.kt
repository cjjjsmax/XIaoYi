package com.example.xiaoyi.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.xiaoyi.ui.screens.ConversationDetailScreen
import com.example.xiaoyi.ui.screens.ConversationsScreen
import com.example.xiaoyi.ui.screens.LoginScreen
import com.example.xiaoyi.ui.screens.ProductDetailScreen
import com.example.xiaoyi.ui.screens.ProductListScreen
import com.example.xiaoyi.ui.screens.ProfileScreen
import com.example.xiaoyi.ui.screens.RegisterScreen
import com.example.xiaoyi.ui.screens.SellScreen
import com.example.xiaoyi.ui.screens.DetailType
import com.example.xiaoyi.ui.screens.EditProductScreen
import com.example.xiaoyi.ui.screens.EditWantedScreen
import com.example.xiaoyi.ui.screens.MyOrdersScreen
import com.example.xiaoyi.ui.screens.MyPostsScreen
import com.example.xiaoyi.ui.screens.MyWantedScreen
import com.example.xiaoyi.ui.screens.SettingsScreen
import com.example.xiaoyi.ui.screens.EditProfileScreen
import com.example.xiaoyi.ui.screens.AccountSecurityScreen
import com.example.xiaoyi.ui.screens.AddAddressScreen
import com.example.xiaoyi.ui.screens.AddressManagementScreen
import com.example.xiaoyi.ui.screens.EditAddressScreen
import com.example.xiaoyi.utils.UserManager

sealed class Screen(val route: String){
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductList : Screen("product_list")
    object ProductDetail : Screen("product_detail/{productId}")
    object Sell  : Screen("sell")
    object Profile : Screen("profile")
    object UserProfile : Screen("user_profile/{userId}")
    object Conversations : Screen("conversations")
    object ConversationDetail : Screen("conversation_detail/{conversationId}")
    object PurchaseRequestDetail : Screen("purchase_request_detail/{purchaseRequestId}")
    object MyPosts : Screen("my_posts/{userId}")
    object MyWanted : Screen("my_wanted/{userId}")
    object EditProduct : Screen("edit_product/{productId}")
    object EditWanted : Screen("edit_wanted/{requestId}")
    object MyOrders : Screen("my_orders/{userId}")
    object Settings : Screen("settings/{userId}")
    object EditProfile : Screen("edit_profile/{userId}")
    object AccountSecurity : Screen("account_security/{userId}")
    object AddressManagement : Screen("address_management/{userId}")
    object AddAddress : Screen("add_address/{userId}")
    object EditAddress : Screen("edit_address/{addressId}")
}

@Composable
fun AppNavigation(
    navController: androidx.navigation.NavHostController,
    startDestination: String = Screen.Login.route,
    onLoginSuccess: () -> Unit = {},
    onLogout: () -> Unit = {},
    userId: Long,
    paddingValues: PaddingValues
){
    NavHost(
      navController = navController,
        startDestination = startDestination
    ){
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
            )
        }
        composable ( Screen.Register.route ){
            RegisterScreen(
                navController=navController,
                onRegisterSuccess = {
                    onLoginSuccess()
                    navController.navigate(Screen.Home.route){
                        popUpTo (Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable (Screen.Home.route) {
            ProductListScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }
        composable (Screen.Sell.route) {
            SellScreen(navController = navController, userId = userId, paddingValues = paddingValues)
        }
        composable (Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(
                navController = navController,
                productId = productId,
                paddingValues = paddingValues,
                detailType = DetailType.PRODUCT
            )
        }
        composable(Screen.UserProfile.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0L
            ProfileScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues
            )
        }
        composable(Screen.Conversations.route) {
            ConversationsScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }

        composable(
            route = Screen.ConversationDetail.route,
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
            ConversationDetailScreen(
                navController = navController,
                conversationId = conversationId,
                paddingValues = paddingValues
            )
        }

        composable(
            route = Screen.PurchaseRequestDetail.route,
            arguments = listOf(
                navArgument("purchaseRequestId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val purchaseRequestId = backStackEntry.arguments?.getLong("purchaseRequestId") ?: 0L
            ProductDetailScreen(
                navController = navController,
                productId = purchaseRequestId,
                paddingValues = paddingValues,
                detailType = DetailType.PURCHASE_REQUEST
            )
        }

        composable(
            route = Screen.MyPosts.route,
            arguments = listOf(
                navArgument("userId"){
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            MyPostsScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues
            )

        }

        composable(
            route = Screen.MyWanted.route,
            arguments = listOf(
                navArgument("userId"){
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            MyWantedScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues
            )

        }

        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            EditProductScreen(
                navController = navController,
                productId = productId,
                paddingValues = paddingValues
            )
        }

        composable(
            route = Screen.EditWanted.route,
            arguments = listOf(
                navArgument("requestId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getLong("requestId") ?: 0L
            EditWantedScreen(
                navController = navController,
                requestId = requestId,
                paddingValues = paddingValues
            )
        }

        composable(
            route = Screen.MyOrders.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            MyOrdersScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues
            )
        }

        composable(
            route = Screen.Settings.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            SettingsScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues,
                onLogout = {
                    UserManager.logout()
                    onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            EditProfileScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues
            )
        }

        composable(
            route = Screen.AccountSecurity.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            AccountSecurityScreen(
                navController = navController,
                userId = userId,
                paddingValues = paddingValues
            )
        }
        composable(
            route = Screen.AddressManagement.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            AddressManagementScreen(
                navController = navController,
                userId = userId
            )
        }


        composable(
            route = Screen.AddAddress.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            AddAddressScreen(navController = navController, userId = userId)
        }

        composable(
            route = Screen.EditAddress.route,
            arguments = listOf(
                navArgument("addressId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getLong("addressId") ?: 0L
            EditAddressScreen(navController = navController, addressId = addressId)
        }
    }
}