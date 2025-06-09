package com.example.audiomediaplayer

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun MainScreen(mediaPlayer: MediaPlayer, currentSong: MutableState<String?>, navController: NavController) {
    val context = LocalContext.current
    val files = context.assets.list("") ?: emptyArray()

    val artist = remember { mutableStateOf<String?>("Unknown Artist") }
    val album = remember { mutableStateOf<String?>("Unknown Album") }
    val title = remember { mutableStateOf<String?>("Unknown Title") }

    if (currentSong.value != null && currentSong.value != "placeholder") {
        val metaDataRetriever = MediaMetadataRetriever().apply{
            context.assets.openFd(currentSong.value!!).use { descriptor ->
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            }
        }

        artist.value = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        album.value = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        title.value = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
    }

    val currentIndex = files.indexOf(currentSong.value)
    val nextSong = remember { mutableStateOf<String?>(null) }

    if (currentIndex == -1 || currentIndex > files.size - 1) {
        nextSong.value = files[0] // Loop to the first song if current is invalid
    } else {
        nextSong.value = files[currentIndex + 1]
    }

    val nextArtist = remember { mutableStateOf<String?>("Unknown Artist") }
    val nextAlbum = remember { mutableStateOf<String?>("Unknown Album") }
    val nextTitle = remember { mutableStateOf<String?>("Unknown Title") }

    if (nextSong.value != null) {
        val metaDataRetriever = MediaMetadataRetriever().apply{
            context.assets.openFd(nextSong.value!!).use { descriptor ->
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            }
        }

        nextArtist.value = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        nextAlbum.value = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        nextTitle.value = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
    }


    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(vertical = 25.dp, horizontal = 16.dp)
                .fillMaxWidth()
                .height(110.dp),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ){
            Column(
                modifier = Modifier.padding(top = 15.dp)
            ){
                Text(
                    text = "Next Song",
                    modifier = Modifier
                        .padding(horizontal = 25.dp),
                    fontSize = 20.sp
                )
                Text(
                    text = nextArtist.value ?: "Unknown Artist",
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 14.sp
                )
                Text(
                    text = nextTitle.value ?: "Unknown Title",
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 16.sp
                )
                Text(
                    text = nextAlbum.value ?: "Unknown Album",
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 12.sp
                )
            }
        }


        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = innerPadding.calculateTopPadding() + 150.dp)
                .height(350.dp)
                .fillMaxWidth(),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(top = 15.dp)
            ) {
                Text(
                    text = "Now Playing",
                    modifier = Modifier
                        .padding(horizontal = 25.dp),
                    fontSize = 32.sp
                )
                Text(
                    text = artist.value ?: "Unknown Artist",
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 20.sp
                )
                Text(
                    text = title.value ?: "Unknown Title",
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 22.sp
                )
                Text(
                    text = album.value ?: "Unknown Album",
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 16.sp
                )
            }
            PlayCard(mediaPlayer = mediaPlayer, currentSong = currentSong)

        }
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (mediaPlayer.isPlaying) mediaPlayer.stop()
                    navController.navigate("select_song/${currentSong.value}") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .padding(top = 550.dp)
                    .width(200.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "Select a Song",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}



@Composable
fun PlayCard(mediaPlayer: MediaPlayer, currentSong: MutableState<String?>) {
    var currentTime by remember { mutableStateOf(mediaPlayer.currentPosition) }
    val files = LocalContext.current.assets.list("") ?: emptyArray()
    val songEnded = remember { mutableStateOf(false) }

    // TODO fix auto-playing
    //  Case 1: Brain Damage went into Can-Utility fine, but Cogs and Cogs crashed. The media player, oddly enough, kept going for a bit
    //  Case 2: Started from Can-Utility, it also crashed when going to Cogs and Cogs
    //  Case 3: Tried starting from Cogs and Cogs, crashed the program
    if (songEnded.value) {
        if (!mediaPlayer.isLooping) {
            mediaPlayer.pause()
        }

        mediaPlayer.stop()
        mediaPlayer.reset()

        var currentIndex = files.indexOf(currentSong.value)

        // Sets the current index to one before the first to loop to the beginning
        if (currentIndex == -1 || currentIndex > files.size - 1) {
            currentIndex = -1
        }

        val nextSong = files[currentIndex + 1]
        currentSong.value = nextSong

        LocalContext.current.assets.openFd(nextSong).use { descriptor ->
            mediaPlayer.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length
            )
            mediaPlayer.prepare()
            mediaPlayer.seekTo(0)
            currentTime = 0
            mediaPlayer.start()
        }

        songEnded.value = false
    }

    LaunchedEffect(Unit) {
        // TODO the time gets locked if it reaches the end
        while (mediaPlayer.duration > 0) {
            currentTime = if (mediaPlayer.currentPosition >= mediaPlayer.duration) mediaPlayer.duration
            else mediaPlayer.currentPosition
            delay(100)
            if (mediaPlayer.duration == currentTime) {
                songEnded.value = true
            }
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
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = "${(currentTime / 1000) / 60}:${addZero((currentTime / 1000) % 60)}",
                    fontSize = 20.sp,
                    color = Color(0xFF1E88E5)
                )

                Image(
                    painter = painterResource(iconType),
                    contentDescription = "icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
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
                    fontSize = 20.sp,
                    color = Color(0xFF1E88E5)
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
                var volume by remember { mutableStateOf(1f) }

                Slider(
                    modifier = Modifier
                        .padding(top = 10.dp, end = 25.dp)
                        .width(100.dp)
                        .height(1.dp),
                    value = volume,
                    onValueChange = {
                        mediaPlayer.setVolume(it,it)
                        volume = it
                    },
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
                    ),
                    valueRange = 0f..1f
                )

                Image(
                    painter = painterResource(loopType),
                    contentDescription = "icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(bottom = 10.dp, end = 20.dp)
                        .scale(1.75f)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                if (mediaPlayer.isLooping) {
                                    mediaPlayer.isLooping = false
                                    loopType = R.drawable.looptransparent
                                } else {
                                    mediaPlayer.isLooping = true
                                    loopType = R.drawable.loop
                                }
                            }
                        }
                )
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

