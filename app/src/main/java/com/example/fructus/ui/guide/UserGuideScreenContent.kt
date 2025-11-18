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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fructus.ui.guide.components.UserGuideInformation
import com.example.fructus.ui.home.components.Dropdown
import com.example.fructus.ui.shared.ScreenTopBar
import com.example.fructus.ui.theme.appColors
import com.example.fructus.util.ClickGuard

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
    val clickGuard = remember { ClickGuard() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = colors.bg,
        topBar = {
            ScreenTopBar(
                title = "Ripeness Guide",
                onNavigateUp = onNavigateUp,
                colors = colors,
                showArchive = false,
                clickGuard = clickGuard,
                coroutineScope = coroutineScope
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    enabled = isProcessEnabled,
                    clickGuard = clickGuard,
                    coroutineScope = coroutineScope
                )

                Spacer(modifier = Modifier.width(16.dp))

                Dropdown(
                    selectedItem = selectedGuide,
                    items = listOf("Banana", "Mango", "Tomato"),
                    onItemSelected = { onGuideChange(it) },
                    clickGuard = clickGuard,
                    coroutineScope = coroutineScope
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                UserGuideInformation(
                        modifier = Modifier.padding(bottom = 16.dp),
                        selectedFruit = selectedGuide,
                        selectedProcess = selectedProcess
                )
            }
            Text(
                text = "Shelf Life acquired from Bureau of Plant Industries",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}


