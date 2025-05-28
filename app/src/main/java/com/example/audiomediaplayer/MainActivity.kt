package com.example.audiomediaplayer

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audiomediaplayer.ui.theme.AudioMediaPlayerTheme
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data class Main(val mediaPlayer: MediaPlayer,
                val artist: String,
                val album: String,
                val title: String)
@Serializable
data class SelectSong(val mediaPlayer: MediaPlayer)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioMediaPlayerTheme {
                AppStart()
            }
        }
    }
}

@Composable
fun AppStart()
{
    val context = LocalContext.current
    // Initializes the mediaPlayer by opening the audio file and setting the data source
    val mediaPlayer = MediaPlayer().apply {
        context.assets.openFd("infernal_sonata.mp3").use { descriptor ->
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            prepare()
        }
    }

    val metaDataRetriever = MediaMetadataRetriever().apply{
        context.assets.openFd("infernal_sonata.mp3").use { descriptor ->
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        }
    }

    val artist = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    val album = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    val title = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                mediaPlayer = mediaPlayer,
                artist = artist ?: "Unknown Artist",
                album = album ?: "Unknown Album",
                title = title ?: "Unknown Title"
            )
        }
        composable("select_song") {
            SelectSongScreen()
        }
    }
}

@Composable
fun MainScreen(mediaPlayer: MediaPlayer, artist: String, album: String, title: String) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(top = 150.dp)
                .height(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 15.dp)
            ) {
                Text(
                    text = artist,
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 20.sp
                )
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 22.sp
                )
                Text(
                    text = album,
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 16.sp
                )
            }
            PlayCard(mediaPlayer = mediaPlayer)
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Slider(
                modifier = Modifier
                    .padding(start = 50.dp)
                    .graphicsLayer {
                        rotationZ = 270f
                    }
                    .width(100.dp)
                    .height(1.dp),
                value = 0f,
                onValueChange = {
                    mediaPlayer.setVolume(it,it)
                },
                valueRange = 0f..1f
            )
        }
    }
}

@Composable
fun SelectSongScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Select a song",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun PlayCard(mediaPlayer: MediaPlayer) {
    var currentTime by remember { mutableStateOf(mediaPlayer.currentPosition)}

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = if (mediaPlayer.currentPosition >= mediaPlayer.duration) mediaPlayer.duration
            else mediaPlayer.currentPosition
            delay(100)
        }
    }

        Slider(
            modifier = Modifier
                .padding(horizontal = 50.dp),
            value = currentTime.toFloat(),
            onValueChange = {
                mediaPlayer.seekTo(it.toInt())
            },
            valueRange = 0f..mediaPlayer.duration.toFloat(),
            colors = SliderColors(
                thumbColor = Color(red=0, green=167, blue=100),
                activeTrackColor = Color.Green,
                inactiveTrackColor = Color.Gray,
                activeTickColor = Color.Black,
                inactiveTickColor = Color.Black,
                disabledActiveTrackColor = Color.Gray,
                disabledInactiveTrackColor = Color.Gray,
                disabledThumbColor = Color(red=0, green= 255, blue=100),
                disabledActiveTickColor = Color.Gray,
                disabledInactiveTickColor = Color.Gray,
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var iconType by remember { mutableStateOf(R.drawable.play) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .height(300.dp)
                   ,
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .width(450.dp)
                        .height(50.dp)
                        ,
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text="${(currentTime / 1000) / 60}:${addZero((currentTime / 1000) % 60)}",
                        fontSize = 24.sp,
                    )

                    Image(
                        painter = painterResource(iconType),
                        contentDescription = "icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    if (mediaPlayer.isPlaying) {
                                        mediaPlayer.pause()
                                        iconType = R.drawable.play
                                    }
                                    else {
                                        mediaPlayer.start()
                                        iconType = R.drawable.pause
                                    }
                                }
                            }
                    )
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text="${(mediaPlayer.duration / 1000) / 60}:${addZero((mediaPlayer.duration / 1000) % 60 )}",
                        fontSize = 24.sp
                    )
                }

                var loopType by remember { mutableStateOf(R.drawable.looptransparent) }

                Row(
                    modifier = Modifier
                        .width(450.dp)
                        .padding(bottom = 50.dp)
                        ,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Image(
                        painter = painterResource(loopType),
                        contentDescription = "icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    if (mediaPlayer.isLooping) {
                                        mediaPlayer.isLooping = false
                                        loopType = R.drawable.loop
                                    } else {
                                        mediaPlayer.isLooping = true
                                        loopType = R.drawable.looptransparent
                                    }
                                }
                            }
                    )
                    Button(
                        onClick = ({
                            mediaPlayer.stop()
                            mediaPlayer.prepare()
                        })
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
}
fun addZero(number: Int): String {
    return if (number < 10) {
        "0$number"
    } else {
        number.toString()
    }
}
