package ru.wasiliysoft.rustoreconsole.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ReviewsResp(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("body")
    val body: Reviews
)

data class Reviews(
    @SerializedName("content")
    val reviews: List<UserReview>
)

data class UserReview(
    @SerializedName("commentId")
    val commentId: Long,
    @SerializedName("appRating")
    val appRating: Int,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("commentDate")
    private val commentDateStr: String,
    @SerializedName("commentText")
    val commentText: String,
    @SerializedName("likeCounter")
    val likeCounter: Int,
    @SerializedName("dislikeCounter")
    val dislikeCounter: Int,
    @SerializedName("deviceInfo")
    val deviceInfo: String?,
    @SerializedName("devResponse")
    val devResponse: List<DeveloperComment>?
) {
    val commentDate: LocalDateTime
        get() = LocalDateTime.parse(
            commentDateStr.take(19).replace(' ', 'T'),
            DateTimeFormatter.ISO_DATE_TIME
        )

    companion object {
        fun demo(commentId: Long = 5) = UserReview(
            commentId = commentId,
            appRating = 5,
            firstName = "firstName",
            commentDateStr = "2023-07-20 19:09:45.045",
            commentText = "commentTextcommentTextcommentText commentText",
            likeCounter = 8,
            dislikeCounter = 3,
            deviceInfo = null,
            devResponse = listOf(DeveloperComment.demo(commentId))
        )
    }
}

data class DeveloperComment(
    @SerializedName("id")
    val id: Long,
    @SerializedName("text")
    val text: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("date")
    val date: String
) {
    companion object {
        fun demo(id: Long = 5) = DeveloperComment(
            id = id,
            status = "firstName",
            date = "2023-07-20 19:09:45.045",
            text = "Dev comment Dev comment Dev comment",
        )
    }
}