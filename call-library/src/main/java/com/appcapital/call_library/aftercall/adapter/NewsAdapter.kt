package com.appcapital.call_library.aftercall.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appcapital.call_library.R
import com.appcapital.call_library.model.CurrentConditions
import com.appcapital.call_library.model.NewsArticle
import com.appcapital.call_library.utils.Utils
import com.bumptech.glide.Glide

class NewsAdapter(
    private val context: Context,
    private val newsList: List<NewsArticle>,
    private val weatherData: List<CurrentConditions>?,
    private val customView: View?,
    private val userLocation: Pair<String?, String?>?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Define view types
    private val TYPE_CUSTOM_VIEW = 0
    private val TYPE_WEATHER_ITEM = 1
    private val AD_ITEM = 2
    private val TYPE_NEWS_ITEM = 3
    private val TAG: String = NewsAdapter::class.simpleName.toString()

    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "position is : $position ---- $itemCount")
        return if (position == 0) {
            TYPE_CUSTOM_VIEW
        } else if(position == 1){
            TYPE_WEATHER_ITEM
        } else if(position == 2) {
            AD_ITEM
        }else{
            TYPE_NEWS_ITEM
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CUSTOM_VIEW -> {
                if(customView != null){
                    CustomViewHolder(customView)
                }else{
                    throw IllegalArgumentException("Invalid view type")
                }
            }
            AD_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.ad_layout, parent, false)
                AdViewHolder(view)
            }
            TYPE_WEATHER_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.weather_card, parent, false)
                WeatherViewHolder(view)
            }
            TYPE_NEWS_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_card, parent, false)
                NewsViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_CUSTOM_VIEW -> {
                // Custom view doesn't require further binding
            }
            TYPE_WEATHER_ITEM -> {
                if (holder is WeatherViewHolder && weatherData != null) {
                    val weatherItem = weatherData.firstOrNull()
                    weatherItem?.let {
                        // Set weather data for the view
                        holder.condition.text = it.conditions
                        holder.temperature.text = "${Utils.fahrenheitToCelsius(it.temp)} °C"
                        holder.feel.text = "Feels like: ${Utils.fahrenheitToCelsius(it.feelslike)} °C"
                        holder.wind.text = "Wind: ${it.windspeed} m/s"
                        holder.humidity.text = "\uD83D\uDCA7 ${Utils.fahrenheitToCelsius(it.humidity)}%"
                        holder.location.text = "${userLocation?.first ?: "Unknown"}, ${userLocation?.second ?: ""}"
                    }
                }
            }
            TYPE_NEWS_ITEM -> {
                if (holder is NewsViewHolder) {
                    val adjustedPosition = when {
                        customView != null && weatherData != null -> position - 2
                        customView != null || weatherData != null -> position - 1
                        else -> position
                    }

                    if (adjustedPosition >= 0 && adjustedPosition < newsList.size) {
                        val newsArticle = newsList[adjustedPosition]
                        holder.title.text = newsArticle.source?.name
                        holder.description.text = newsArticle.title
                        Glide.with(holder.itemView.context)
                            .load(newsArticle.urlToImage)
                            .centerCrop()
                            .into(holder.image)
                       // holder.image
                        // Bind other news item views as needed
                    }
                }
            }
        }
    }
    override fun getItemCount(): Int {
        var itemCount = newsList.size
        if (customView != null) itemCount += 1
        if (weatherData != null) itemCount += 1
        return itemCount
    }

    // ViewHolder for the custom view
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // ViewHolder for news items
    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val image: ImageView = itemView.findViewById(R.id.news_img)
        // Bind other views (e.g., description, image) if needed
    }
    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val weatherCard: ConstraintLayout = itemView.findViewById(R.id.weather_card)
            weatherCard.setVisibility(View.VISIBLE)
        }


        val temperature: TextView = itemView.findViewById(R.id.weather_temperature)
        val condition: TextView = itemView.findViewById(R.id.weather_condition)
        val location: TextView = itemView.findViewById(R.id.weather_location)
        val feel: TextView = itemView.findViewById(R.id.weather_feel)
        val wind: TextView = itemView.findViewById(R.id.weather_wind)
        val humidity: TextView = itemView.findViewById(R.id.weather_humidity)

        // Bind other views (e.g., description, image) if needed
    }
}


//class NewsAdapter(private val newsList: List<NewsArticle>,  private val customView: View) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    private val TYPE_CUSTOM_VIEW = 0
//    private val TYPE_NEWS_ITEM = 1
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            TYPE_CUSTOM_VIEW -> {
//                // Wrap the custom view in a ViewHolder
//                CustomViewHolder(customView)
//            }
//            TYPE_NEWS_ITEM -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.news_card, parent, false)
//                NewsViewHolder(view)
//            }
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }
//    override fun getItemViewType(position: Int): Int {
//        return if (position == 0) TYPE_CUSTOM_VIEW else TYPE_NEWS_ITEM
//    }
//    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
//        if (holder is NewsViewHolder) {
//            val newsArticle = newsList[position - 1] // Adjust position for the custom view
//            holder.title.text = newsArticle.title
//            // Bind other views (e.g., description, image) if needed
//        }
////        holder.description.text = newsArticle.description
////
////        // Using Glide to load images
////        Glide.with(holder.itemView.context)
////            .load(newsArticle.imageUrl)
////            .into(holder.image)
//    }
//
//    override fun getItemCount(): Int {
//        return newsList.size
//    }
//    // ViewHolder for the custom view
//    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//
//    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val title: TextView = itemView.findViewById(R.id.title)
////        val description: TextView = itemView.findViewById(R.id.news_description)
////        val image: ImageView = itemView.findViewById(R.id.news_image)
//    }
//}