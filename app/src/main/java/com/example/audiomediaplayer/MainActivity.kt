package com.example.audiomediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

                    Card(
                        modifier = Modifier
                            .padding(50.dp)
                            .height(250.dp),
                    ) {
                        TimeBar(mediaPlayer = mediaPlayer)
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
                        Button(
                            onClick = ({
                               mediaPlayer.isLooping = true
                            })
                        ){
                            Text("Loop")
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
                .padding(horizontal = 50.dp)
                .padding(top = 10.dp),
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
                .fillMaxSize()
                .padding(top = 50.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Text(
                    modifier = Modifier.padding(end = 50.dp),
                    text="${(currentTime / 1000) / 60}:${addZero((currentTime / 1000) % 60)}",
                    fontSize = 24.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 50.dp),
                    text="${(mediaPlayer.duration / 1000) / 60}:${addZero((mediaPlayer.duration / 1000) % 60 )}",
                    fontSize = 24.sp
                )
            }
            var iconType by remember { mutableStateOf(R.drawable.play) }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Image(
                    painter = painterResource(iconType),
                    contentDescription = "icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .scale(0.75f)
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