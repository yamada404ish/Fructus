package com.example.fructus.ui.guide

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fructus.ui.guide.components.UserGuideInformation
import com.example.fructus.ui.home.components.Dropdown
import com.example.fructus.ui.shared.ScreenTopBar
import com.example.fructus.ui.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreenContent(
    onNavigateUp: () -> Unit,
    selectedGuide: String,
    onGuideChange: (String) -> Unit,
    selectedProcess: String,
    onProcessChange: (String) -> Unit
) {
    val colors = MaterialTheme.appColors
    val isProcessEnabled = selectedGuide !in listOf("Banana", "Tomato")

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            ScreenTopBar(
                title = "Ripeness Guide",
                onNavigateUp = onNavigateUp,
                colors = colors,
                showArchive = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .padding(start = 24.dp ,end = 24.dp, top = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Dropdown(
                    selectedItem = selectedProcess,
                    items = listOf("Natural", "Artificial"),
                    onItemSelected = { onProcessChange(it) },
                    enabled = isProcessEnabled
                )

                Spacer(modifier = Modifier.width(16.dp))

                Dropdown(
                    selectedItem = selectedGuide,
                    items = listOf("Banana", "Mango", "Tomato"),
                    onItemSelected = { onGuideChange(it) }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                UserGuideInformation(
                        modifier = Modifier.padding(bottom = 16.dp),
                        selectedFruit = selectedGuide,
                        selectedProcess = selectedProcess
                )
            }
        }
    }
}


