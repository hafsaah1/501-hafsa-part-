package com.example.beautyapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.beautyapp.viewmodel.WeatherViewModel
import com.example.beautyapp.data.weather.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
// Added imports:
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import com.example.beautyapp.R // Make sure this R import is here

// A simple class to hold all 4 pieces of video info
private data class VideoInfo(
    val topic: String,
    val duration: String,
    val videoId: String,
    val thumbnailResId: Int
)
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    context: ComponentActivity,
    viewModel: WeatherViewModel,
    userName:String
) {
    var cityName by remember { mutableStateOf("Boston") }
    val weatherState by viewModel.weatherState.collectAsState()
    val apiKey = "769e458d899c570bd71a25267411d1f9"
    var isFahrenheit by remember { mutableStateOf(true) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchInput by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var selectedVideoId by remember { mutableStateOf<String?>(null) }

    // Store the last successful weather state
    var lastSuccessfulState by remember {
        mutableStateOf<WeatherViewModel.WeatherState>(WeatherViewModel.WeatherState.Initial)
    }

    // Fetch Boston weather on initial load
    LaunchedEffect(Unit) {
        viewModel.fetchWeather("Boston", apiKey)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header Section
        HeaderSection(userName=userName)

        Spacer(modifier = Modifier.height(24.dp))

        // Show either Search Bar OR Weather Display Card
        if (showSearchBar) {
            // Search Bar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Change Location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    TextField(
                        value = searchInput,
                        onValueChange = {
                            searchInput = it
                            searchError = ""
                        },
                        label = { Text("Enter City Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        enabled = !isSearching
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Error Message
                    if (searchError.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = searchError,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                when {
                                    searchInput.isBlank() -> {
                                        searchError = "Please enter a city name"
                                    }
                                    isSearching -> {}
                                    else -> {
                                        isSearching = true
                                        searchError = ""
                                        viewModel.fetchWeather(searchInput, apiKey)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = !isSearching
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Get Weather", fontSize = 16.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                showSearchBar = false
                                searchInput = ""
                                searchError = ""
                                isSearching = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            enabled = !isSearching
                        ) {
                            Text("Cancel", fontSize = 16.sp)
                        }
                    }
                }
            }

            // Handle search result
            LaunchedEffect(weatherState) {
                if (isSearching) {
                    when (weatherState) {
                        is WeatherViewModel.WeatherState.Success -> {
                            cityName = searchInput
                            showSearchBar = false
                            searchInput = ""
                            searchError = ""
                            isSearching = false
                            lastSuccessfulState = weatherState
                        }
                        is WeatherViewModel.WeatherState.Error -> {
                            val errorMsg = (weatherState as WeatherViewModel.WeatherState.Error).errorMessage
                            searchError = if (errorMsg.contains("404")) {
                                "City not found. Please try another location."
                            } else {
                                "Failed to fetch weather. Please try again."
                            }
                            isSearching = false
                        }
                        else -> {}
                    }
                }
            }
        } else {
            // Weather Display Card
            val displayState = if (lastSuccessfulState is WeatherViewModel.WeatherState.Success) {
                lastSuccessfulState
            } else {
                weatherState
            }

            when (displayState) {
                WeatherViewModel.WeatherState.Initial -> {}
                WeatherViewModel.WeatherState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is WeatherViewModel.WeatherState.Success -> {
                    val weatherResponse = (displayState as WeatherViewModel.WeatherState.Success).weatherResponse
                    WeatherDisplayCard(
                        cityName = weatherResponse.name,
                        temperature = weatherResponse.main.temp,
                        description = weatherResponse.weather[0].description,
                        humidity = weatherResponse.main.humidity,
                        feelsLike = weatherResponse.main.feelsLike,
                        isFahrenheit = isFahrenheit,
                        onUnitToggle = { isFahrenheit = it },
                        onChangeLocation = { showSearchBar = true }
                    )
                    LaunchedEffect(displayState) {
                        lastSuccessfulState = displayState
                    }
                }
                is WeatherViewModel.WeatherState.Error -> {
                    val errorMessage = (displayState as WeatherViewModel.WeatherState.Error).errorMessage
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: $errorMessage",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Video Section with Embedded Player
        if (selectedVideoId != null) {
            EmbeddedYouTubePlayer(
                videoId = selectedVideoId!!,
                lifecycleOwner = context,
                onClose = { selectedVideoId = null }
            )
        } else {
            VideoCardPlaceHolder(
                context = context,
                onVideoClick = { videoId -> selectedVideoId = videoId }
            )
        }
    }
}

@Composable
fun HeaderSection(userName:String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hi 👋",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Discover your unique beauty!",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun WeatherDisplayCard(
    cityName: String,
    temperature: Double,
    description: String,
    humidity: Int,
    feelsLike: Double,
    isFahrenheit: Boolean,
    onUnitToggle: (Boolean) -> Unit,
    onChangeLocation: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cityName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                TextButton(
                    onClick = onChangeLocation,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Change",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = getWeatherEmoji(description), fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                val displayTemp = if (isFahrenheit) {
                    celsiusToFahrenheit(temperature).toInt()
                } else {
                    temperature.toInt()
                }

                Text(
                    text = "$displayTemp",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Column {
                    Text(
                        text = "°F",
                        fontSize = 16.sp,
                        fontWeight = if (isFahrenheit) FontWeight.Bold else FontWeight.Medium,
                        color = if (isFahrenheit) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable(
                            indication = rememberRipple(bounded = false),
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onUnitToggle(true) }
                    )
                    Text(
                        text = "|",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "°C",
                        fontSize = 16.sp,
                        fontWeight = if (!isFahrenheit) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isFahrenheit) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable(
                            indication = rememberRipple(bounded = false),
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onUnitToggle(false) }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Humidity: $humidity%",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    val feelsLikeDisplay = if (isFahrenheit) {
                        "${celsiusToFahrenheit(feelsLike).toInt()}°F"
                    } else {
                        "${feelsLike.toInt()}°C"
                    }
                    Text(
                        text = "Feels: $feelsLikeDisplay",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Text(
                text = description.replaceFirstChar { it.uppercase() },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
fun EmbeddedYouTubePlayer(
    videoId: String,
    lifecycleOwner: ComponentActivity,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // YouTube Player
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp)),
            factory = { context ->
                YouTubePlayerView(context).apply {
                    lifecycleOwner.lifecycle.addObserver(this)

                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                    })
                }
            }
        )
    }
}

//  ************************************************
//  THIS IS THE UPDATED FUNCTION
//  ************************************************
@Composable
fun VideoCardPlaceHolder(
    context: ComponentActivity,
    onVideoClick: (String) -> Unit
) {
    // UPDATED: Now uses the VideoInfo data class
    val beautyVideos = listOf(
        VideoInfo("Foundation Tips", "2 min", "hkQhA6lmDME", R.drawable.foundation),
        VideoInfo("Blush Tutorial", "3 min", "5wx1U64ez14", R.drawable.blush),
        VideoInfo("Contour Guide", "4 min", "QKz_2ScpV9s", R.drawable.foundation), // Placeholder
        VideoInfo("Eye Makeup", "5 min", "tTV6vZCKEWM", R.drawable.foundation), // Placeholder
        VideoInfo("Lipstick", "2 min", "ay1EH7uQjG0", R.drawable.lipstick)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(beautyVideos.size) { index ->
            // Destructure the data class - this is now correct
            val (topic, duration, videoId, thumbnailResId) = beautyVideos[index]

            Card(
                modifier = Modifier
                    .width(220.dp)
                    .height(300.dp)
                    .clickable(
                        indication = rememberRipple(),
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onVideoClick(videoId)
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. The Thumbnail Image
                    Image(
                        painter = painterResource(id = thumbnailResId),
                        contentDescription = topic,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // 2. Dark gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.8f)
                                    ),
                                    startY = 300f
                                )
                            )
                    )

                    // 3. Your existing content
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        // Duration Badge
                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⏱️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = duration,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Play Button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Topic Title
                        Text(
                            text = topic,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}