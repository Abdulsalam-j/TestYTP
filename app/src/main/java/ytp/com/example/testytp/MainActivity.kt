package ytp.com.example.testytp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import ytp.com.example.testytp.ui.theme.TestYTPTheme
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentXKt
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.regex.Pattern
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer as YouTubePlayer2

const val YOUTUBE_API_KEY = "My-Key"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestYTPTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting("Yacht Video")
                        Spacer(modifier = Modifier.padding(60.dp))
                        YouTubeScreen(youtubeLink = "https://www.youtube.com/watch?v=cBoP-aKNhCg")
                        Spacer(modifier = Modifier.padding(60.dp))
                        YouTubeScreen2(videoLink = "https://www.youtube.com/watch?v=qWzKTIs8pXo")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(modifier = Modifier.padding(20.dp), text = "Hello $name!")
}

@Composable
fun YouTubeScreen(youtubeLink: String) {
    val ctx = LocalContext.current
    AndroidView(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .size(335.dp, 189.dp),
        factory = {
            val fm = (ctx as AppCompatActivity).supportFragmentManager
            val view = FragmentContainerView(it).apply {
                id = androidx.fragment.R.id.fragment_container_view_tag
            }
            val fragment = YouTubePlayerSupportFragmentXKt().apply {
                initialize(
                    YOUTUBE_API_KEY,
                    object : YouTubePlayer.OnInitializedListener {
                        override fun onInitializationFailure(
                            provider: YouTubePlayer.Provider,
                            result: YouTubeInitializationResult
                        ) {
                            Toast.makeText(
                                context,
                                "Error initializing video",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onInitializationSuccess(
                            provider: YouTubePlayer.Provider,
                            player: YouTubePlayer,
                            wasRestored: Boolean
                        ) {
                            // TODO closing this screen when the player is in fullscreen
                            //  is making the app keep in landscape. Disabling for now.
                            player.setShowFullscreenButton(true)
                            if (!wasRestored) {
                                player.cueVideo(getYouTubeVideoId(youtubeLink))
                            }
                        }
                    },
                )
            }
            fm.commit {
                setReorderingAllowed(true)
                add(androidx.fragment.R.id.fragment_container_view_tag, fragment)
            }
            view
        },
    )
}

@Composable
fun YouTubeScreen2(
    videoLink: String,
) {
    val ctx = LocalContext.current
    AndroidView(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .size(335.dp, 189.dp),
        factory = {
        val view = YouTubePlayerView(it)
        val fragment = view.addYouTubePlayerListener(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer2) {
                    super.onReady(youTubePlayer)
                    youTubePlayer.cueVideo(getYouTubeVideoId(videoLink), 0f)
                }
            }
        )
        view
    })
}

// Utility function to extract the YouTube video ID from the URL
private fun getYouTubeVideoId(youtubeLink: String): String {
    val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
    val compiledPattern = Pattern.compile(pattern)
    val matcher = compiledPattern.matcher(youtubeLink)
    return if (matcher.find()) {
        matcher.group()
    } else {
        throw IllegalArgumentException("Invalid YouTube video URL: $youtubeLink")
    }
}
