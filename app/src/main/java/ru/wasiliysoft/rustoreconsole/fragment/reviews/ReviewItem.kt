package ru.wasiliysoft.rustoreconsole.fragment.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.wasiliysoft.rustoreconsole.data.AppInfo
import ru.wasiliysoft.rustoreconsole.data.DeveloperComment
import ru.wasiliysoft.rustoreconsole.data.UserReview
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier
) {

    val userReview = review.userReview
    val appInfo = review.appInfo

    val date = userReview.commentDate
//    val cardColor = if (date.toLocalDate() == LocalDate.now()) CardDefaults.cardColors()
//    else CardDefaults.outlinedCardColors()
    Card(
//        colors = cardColor,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = appInfo.appName, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${userReview.firstName} ${userReview.appRating}",
                    modifier = Modifier.weight(1f)
                )
                Text(text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
            }
            Text(text = userReview.commentText)
            Text(
                text = "like ${userReview.likeCounter} / dislike ${userReview.dislikeCounter}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
            userReview.devResponse?.let { devResponse ->
                DeveloperResponseListView(
                    devResponse = devResponse,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DeveloperResponseListView(
    devResponse: List<DeveloperComment>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            devResponse.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = it.status, modifier = Modifier.weight(1f))
                    Text(text = it.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                }
                Text(text = it.text)
            }

        }
    }
}

@Preview()
@Composable
private fun Preview(modifier: Modifier = Modifier) {
    ReviewItem(
        review = Review(
            appInfo = AppInfo.demo(),
            userReview = UserReview.demo(),
        )
    )
}