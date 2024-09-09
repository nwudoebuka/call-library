package com.appcapital.call_library.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class NewsResponse (
    val status: String,
    val totalResults: Long,
    val articles: List<NewsArticle>
)

@Serializable
data class NewsArticle (
    val source: NewsSource? = null,
    val author: String,
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null
)

@Serializable
data class NewsSource (
    val id: String? = null,
    val name: String? = null
)

@Serializable
enum class NewsID(val value: String) {
    @SerialName("google-news") GoogleNews("google-news");
}

@Serializable
enum class Name(val value: String) {
    @SerialName("Google News") GoogleNews("Google News");
}
