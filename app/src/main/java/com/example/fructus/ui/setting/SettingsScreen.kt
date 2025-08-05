import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.fructus.ui.setting.SettingsScreenContent
import com.example.fructus.ui.setting.SettingsViewModel
import com.example.fructus.util.DataStoreManager
import com.example.fructus.util.navigateToNotificationSettings

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember {
        SettingsViewModel(context, DataStoreManager(context))
    }
    val state by viewModel.state.collectAsState()

    SettingsScreenContent(
        state = state,
        onNavigateUp = onNavigateUp,
        onToggleNotifications = viewModel::onToggleNotifications,
        onEnableNotifications = {
            navigateToNotificationSettings(context)
            viewModel.markReturnedFromSettings()
        },
        onDismissSheet = viewModel::hideBottomSheet,
        onShowClearDialog = viewModel::showClearDialog,
        onClearAll = viewModel::hideClearDialog,
        onDismissClearDialog = viewModel::hideClearDialog
    )
}
