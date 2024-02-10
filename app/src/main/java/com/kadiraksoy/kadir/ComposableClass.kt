package com.kadiraksoy.kadir

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            if (navigationIcon != null) {
                navigationIcon()
            }
        },
        actions = {
            if (actions != null) {
                actions()
            }
        }
    )
}

@Composable
fun MusicBottomAppBar(
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    isPlaying: Boolean,

) {
    BottomAppBar(
        modifier = Modifier.background(Color.Green)
    ) {
        Spacer(modifier = Modifier.weight(2f))
        IconButton(onClick = { onPreviousClick() }) {
            Image(
                painter = painterResource(id = R.drawable.ic_previous),
                contentDescription = "Previous",
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.weight(3f))
        IconButton(onClick = { onPlayPauseClick() }) {
            Image(
                painter = if (isPlaying) painterResource(id = R.drawable.ic_pause) else painterResource(id = R.drawable.ic_play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.weight(3f))
        IconButton(onClick = { onNextClick() }) {
            Image(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "Next",
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
fun MusicCardItem(
    musicFile: MusicFile,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onFavoriteClick: () -> Unit, // Yeni eklenen favori butonu tıklama işlemi
) {
    //musicFile nesnesinin favori durumunu Compose UI'ına bağlar
    // ve bu durum değiştiğinde UI'ın otomatik olarak güncellenmesini sağlar.
    val isFavoriteState = remember { mutableStateOf(musicFile.isFavorite) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(IntrinsicSize.Max),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween // İçeriği yatayda ortala
        ) {
            MusicPlayerControls(
                isPlaying = isPlaying,
                onPlayPauseClick = onPlayPauseClick,
            )
            Spacer(modifier = Modifier.width(16.dp)) // İki öğe arasında boşluk ekleyebilirsiniz
            Text(
                text = musicFile.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .align(Alignment.CenterVertically) // Dikeyde ortala
            )
            // Favori eklemek için Icon
            IconButton(
                onClick = { onFavoriteClick()
                    // güncel olarak rengi değiştirmek için
                    isFavoriteState.value = !isFavoriteState.value},
                modifier = Modifier
                    .size(30.dp) // Favori simge boyutu
            ) {
                val favoriteIcon = if (isFavoriteState.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                val favoriteColor = if (isFavoriteState.value) Color.Red else Color.Gray
                Icon(
                    imageVector = if (musicFile.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (musicFile.isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

@Composable
fun FavoriteMusicCardItem(
    musicFile: MusicFile,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(IntrinsicSize.Max)// İçerik boyutuna uygun yükseklik
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween // İçeriği yatayda ortala
        ) {
            MusicPlayerControls(
                isPlaying = isPlaying,
                onPlayPauseClick = onPlayPauseClick,
            )
            Spacer(modifier = Modifier.width(16.dp)) // İki öğe arasında boşluk ekleyebilirsiniz
            Text(
                text = musicFile.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .align(Alignment.CenterVertically) // Dikeyde ortala
            )
        }
    }
}

@Composable
fun MusicPlayerControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
) {
    val image: Painter = if (isPlaying) {
        painterResource(id = R.drawable.ic_pause)
    } else {
        painterResource(id = R.drawable.ic_play)
    }

    Image(
        painter = image,
        contentDescription = null,
        modifier = Modifier
            .size(30.dp) // Adjust the size as needed
            .clickable(onClick = onPlayPauseClick)
    )
}

@Composable
fun TrackSlider(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float
) {
    Slider(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        onValueChangeFinished = {
            onValueChangeFinished()
        },
        valueRange = 0f..songDuration,
        colors = SliderDefaults.colors(
            thumbColor = Color.Black,
            activeTrackColor = Color.DarkGray,
            inactiveTrackColor = Color.Gray,
        )
    )

}

@Composable
fun TrackSliderWithTime(
    sliderPosition: MutableState<Long>,
    currentPosition: MutableState<Long>,
    totalDuration: MutableState<Long>,
    exoPlayer: ExoPlayer
) {
    //it = kullanıcının kaydırma çubuğunu hareket ettirdiği değeri ifade eder.
    TrackSlider(
        value = sliderPosition.value.toFloat(),
        onValueChange = {
            sliderPosition.value = it.toLong()
        },
        onValueChangeFinished = {
            currentPosition.value = sliderPosition.value
            exoPlayer.seekTo(sliderPosition.value)
        },
        songDuration = totalDuration.value.toFloat()
    )
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = (currentPosition.value).convertToText(),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            color = Color.Black,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
        // kalan zaman
        val remainTime = totalDuration.value - currentPosition.value
        Text(
            text = if (remainTime >= 0) remainTime.convertToText() else "",
            modifier = Modifier
                .padding(8.dp),
            color = Color.Black,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}


private fun Long.convertToText(): String {
    val sec = this / 1000
    val minutes = sec / 60
    val seconds = sec % 60

    val minutesString = if (minutes < 10) {
        "0$minutes"
    } else {
        minutes.toString()
    }
    val secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$minutesString:$secondsString"
}
