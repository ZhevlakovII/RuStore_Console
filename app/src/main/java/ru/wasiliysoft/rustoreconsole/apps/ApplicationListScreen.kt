package ru.wasiliysoft.rustoreconsole.apps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.wasiliysoft.rustoreconsole.data.AppInfo
import ru.wasiliysoft.rustoreconsole.ui.view.ProgressView
import ru.wasiliysoft.rustoreconsole.utils.LoadingResult

@Composable
fun ApplicationListScreen(
    uiSate: State<LoadingResult<List<AppInfo>>>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (uiSate.value) {
                is LoadingResult.Loading -> {
                    ProgressView((uiSate.value as LoadingResult.Loading).description)
                }

                is LoadingResult.Success -> {
                    LazyColumn {
                        items(
                            items = (uiSate.value as LoadingResult.Success<List<AppInfo>>).data,
                            key = { it.packageName }) {
                            AppInfoCard(appInfo = it)
                        }
                    }
                }

                is LoadingResult.Error -> {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val message = (uiSate.value as LoadingResult.Error).exception.message
                        Text(text = message ?: "Неизвестная ошибка")
                    }
                }
            }
        }
        Surface(tonalElevation = 2.dp, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onRefresh, Modifier.padding(16.dp)) {
                Text(text = "Обновить")
            }
        }
    }
}

@Composable
fun AppInfoCard(
    appInfo: AppInfo,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(text = appInfo.appName)
            Text(text = appInfo.appStatus)
            Text(text = appInfo.versionName)
            Text(text = appInfo.versionCode.toString())
        }
    }
}