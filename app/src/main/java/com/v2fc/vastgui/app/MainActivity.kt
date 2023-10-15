package com.v2fc.vastgui.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2fc.vastgui.app.ui.theme.V2FCTheme
import com.v2fc.vastgui.progress.ArcProgress
import com.v2fc.vastgui.progress.ArcProgressDefaults

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            V2FCTheme {
                val colorStops = arrayOf(
                    0.0f to Color.Yellow,
                    0.2f to Color.Red,
                    1f to Color.Blue
                )
                // A surface container using the 'background' color from the theme
                var sliderPosition by remember { mutableFloatStateOf(0f) }
                var progressPosition by remember { mutableFloatStateOf(0f) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "$progressPosition")
                        ArcProgress(
                            radius = 100f.dp,
                            width = 10f.dp,
                            endPointRadius = 20f.dp,
                            modifier = Modifier
                                .padding(20f.dp)
                                .size(220.dp),
                            colors = ArcProgressDefaults.brush(
                                Brush.horizontalGradient(colorStops = colorStops)
                            ),
                            currentProgress = sliderPosition
                        ) { progressPosition = it }
                        Slider(
                            value = sliderPosition,
                            onValueChange = {
                                sliderPosition = it
                            },
                            modifier = Modifier.padding(horizontal = 20f.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    V2FCTheme {
        val colorStops = arrayOf(
            0.0f to Color.Yellow,
            0.2f to Color.Red,
            1f to Color.Blue
        )
        ArcProgress(
            100f.dp,
            20f.dp,
            endPointRadius = 25f.dp,
            modifier = Modifier
                .padding(20f.dp)
                .size(300f.dp),
            colors = ArcProgressDefaults.brush(
                Brush.horizontalGradient(colorStops = colorStops)
            ),
            currentProgress = 1f
        ) {

        }
    }
}