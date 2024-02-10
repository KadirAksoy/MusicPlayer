package com.kadiraksoy.kadir

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavoriteList(navController:NavController){
    val context = LocalContext.current

    var currentPlayingIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    // isFavorite durumu True olanları alıyoruz.
    fun getFavoriteMusicList(context: Context): List<MusicFile> {
        val allMusicList = getMusicListWithFavorites(context)
        return allMusicList.filter { it.isFavorite }
    }

    val favoriteMusicList = remember { mutableStateListOf<MusicFile>() }


    // Favori müzik listesini güncelle
    LaunchedEffect(context) {
        favoriteMusicList.clear()
        favoriteMusicList.addAll(getFavoriteMusicList(context))
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
    }
    //ExoPlayer nesnesini serbest bırakır.
    // ExoPlayer, medya çalma işlemleri için kullanılan bir kütüphanedir.
    // Bunu kullanarak, ExoPlayer'ın kaynaklarını düzgün bir şekilde serbest bırakarak
    // bellek sızıntılarını ve performans sorunlarını önlemiş olursun.
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    //Path alma işlemi
    LaunchedEffect(currentPlayingIndex) {
        if(favoriteMusicList.isNotEmpty()){
            isPlaying = true
            val mediaItem = MediaItem.fromUri(favoriteMusicList[currentPlayingIndex].path)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()}

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

    // Çalma durumunu başlat
    fun startPlaying() {
        isPlaying = true
        val mediaItem = MediaItem.fromUri(favoriteMusicList[currentPlayingIndex].path)
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
            // If at the beginning, go to the last item
            currentPlayingIndex = favoriteMusicList.size - 1
        }
        startPlaying()
    }

    // Next butonuna tıklandığında yapılacak işlemler --tekrar başa dönüyo
    fun onNextClick() {
        if (currentPlayingIndex < favoriteMusicList.size - 1) {
            currentPlayingIndex++
        } else {
            // If at the end, go to the first item
            currentPlayingIndex = 0
        }
        startPlaying()
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

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopBar(
                title = "Favorite List",
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("musicPlayerScreen")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ekle",
                            tint = Color.White
                        )
                    }
                }
            )
        },
    ) {
        if (favoriteMusicList.isNotEmpty())
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(favoriteMusicList) {index, musicFile ->
                        FavoriteMusicCardItem(
                            musicFile = musicFile,
                            isPlaying = index == currentPlayingIndex && isPlaying,
                            onPlayPauseClick = {
                                if (index == currentPlayingIndex) {
                                    togglePlayPause()
                                } else {
                                    currentPlayingIndex = index
                                    startPlaying()
                                } },
                        )
                    }
                }
                TrackSliderWithTime(
                    sliderPosition = sliderPosition,
                    currentPosition = currentPosition,
                    totalDuration = totalDuration,
                    exoPlayer = exoPlayer)
                MusicBottomAppBar(
                    onPreviousClick = { onPreviousClick() },
                    onPlayPauseClick = { onPlayPauseClick() },
                    onNextClick = { onNextClick() },
                    isPlaying = isPlaying,)
            }

        }

    }
}