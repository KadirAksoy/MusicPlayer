package com.kadiraksoy.kadir

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MusicPlayerScreen(navController: NavController) {
    val context = LocalContext.current
    var currentPlayingIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var musicList by remember { mutableStateOf(getMusicListWithFavorites(context)) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    //Path alma işlemi
    LaunchedEffect(currentPlayingIndex) {
        isPlaying = true
        val mediaItem = MediaItem.fromUri(musicList[currentPlayingIndex].path)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }

    //track slider işlemleri
    //sürekli olarak mevcut konumu güncellemek için kullanılır.
    LaunchedEffect(key1 = exoPlayer.currentPosition, key2 = exoPlayer.isPlaying) {
        delay(1000)
        currentPosition.longValue = exoPlayer.currentPosition
    }
    //slider'ın mevcut konumunu güncellemek için kullanılır.
    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }
    // toplam ses dosyasının süresini güncellemek için kullanılır.
    LaunchedEffect(exoPlayer.duration) {
        if (exoPlayer.duration > 0) {
            totalDuration.longValue = exoPlayer.duration
        }
    }

    // Path'i alıp Çalma durumunu başlat
    fun startPlaying() {
        isPlaying = true
        val mediaItem = MediaItem.fromUri(musicList[currentPlayingIndex].path)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    // Çalma durumunu başlat/durdur
    fun togglePlayPause() {
        isPlaying = !isPlaying
        if (isPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    // Previous butonuna tıklandığında yapılacak işlemler--baştan en sona geçiyor.
    fun onPreviousClick() {
        if (currentPlayingIndex > 0) {
            currentPlayingIndex--
        } else {
            // eğer baştaysa en sona gidiyor
            currentPlayingIndex = musicList.size - 1
        }
        startPlaying()
    }

    // Next butonuna tıklandığında yapılacak işlemler --tekrar başa dönüyo
    fun onNextClick() {
        if (currentPlayingIndex < musicList.size - 1) {
            currentPlayingIndex++
        } else {
            // bitince en baştaki index'e gider
            currentPlayingIndex = 0
        }
        startPlaying()
    }

    //Favori durumunu günceller
    fun onFavoriteClick(musicFile: MusicFile) {
        musicFile.isFavorite = !musicFile.isFavorite
        updateFavoriteStatus(context, musicFile)
    }


    // bir şarkı bittikten sonra diğer şarkı oto çalması için
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    // Şarkı tamamlandığında yapılacak işlemler
                    onNextClick()
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }
    // Play/Pause butonuna tıklandığında yapılacak işlemler
    fun onPlayPauseClick() {
        togglePlayPause()
    }

    Scaffold(modifier = Modifier,
        topBar = {
            TopBar(
                title = "Music Player",
                actions = {
                        IconButton(onClick = {
                        musicList = getMusicListWithFavorites(context)
                        navController.navigate("favoriteList")
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "List",
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(musicList) { index, musicFile ->
                    MusicCardItem(
                        musicFile = musicFile,
                        isPlaying = index == currentPlayingIndex && isPlaying,
                        onPlayPauseClick = {
                            if (index == currentPlayingIndex) {
                                togglePlayPause()
                            } else {
                                currentPlayingIndex = index
                                startPlaying()
                            }
                        },
                        onFavoriteClick = { onFavoriteClick(musicFile)}
                    )
                }
            }
            TrackSliderWithTime(
                sliderPosition = sliderPosition,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                exoPlayer = exoPlayer
            )
            MusicBottomAppBar(
                onPreviousClick = { onPreviousClick() },
                onPlayPauseClick = { onPlayPauseClick() },
                onNextClick = { onNextClick() },
                isPlaying = isPlaying,
            )
        }
    }
}


