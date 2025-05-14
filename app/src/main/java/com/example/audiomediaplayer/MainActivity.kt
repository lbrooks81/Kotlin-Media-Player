package com.example.audiomediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audiomediaplayer.ui.theme.AudioMediaPlayerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mediaPlayer = MediaPlayer().apply {
            assets.openFd("infernal_sonata.mp3").use { descriptor ->
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                prepare()
            }
        }

        enableEdgeToEdge()
        setContent {
            AudioMediaPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    TimeBar(mediaPlayer = mediaPlayer)

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
                        Button(
                            onClick = ({
                                mediaPlayer.isLooping = true
                            })
                        ){
                            Text("Loop")
                        }

                        var toggleText by remember { mutableStateOf("Play") }
                        Button(
                            onClick = ({
                                if (mediaPlayer.isPlaying) {
                                    mediaPlayer.pause()
                                    toggleText = "Play"
                                }
                                else {
                                    mediaPlayer.start()
                                    toggleText = "Pause"
                                }
                                Log.d("tag", "isPlaying: ${mediaPlayer.isPlaying}")
                            })
                        ) {
    // TODO Replace text with an icon that dynamically updates using the MediaPlayer.isPlaying() boolean
                            Text("$toggleText")
                        }
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
    }
}

@Composable
fun TimeBar(mediaPlayer: MediaPlayer) {
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
            .padding(top = 50.dp),
        value = currentTime.toFloat(),
        onValueChange = {
            mediaPlayer.seekTo(it.toInt())
        },
        valueRange = 0f..mediaPlayer.duration.toFloat()
    )
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        Text(
            modifier = Modifier.padding(end = 50.dp, top = 25.dp),
            text="${(currentTime / 1000) / 60}:${addZero((currentTime / 1000) % 60)}",
            fontSize = 24.sp,
        )
        Text(
            modifier = Modifier.padding(start = 50.dp, top = 25.dp),
            text="${(mediaPlayer.duration / 1000) / 60}:${addZero((mediaPlayer.duration / 1000) % 60 )}",
            fontSize = 24.sp
        )
    }
}
fun addZero(number: Int): String {
    return if (number < 10) {
        "0$number"
    } else {
        number.toString()
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AudioMediaPlayerTheme {
        Greeting("Android")
    }
}