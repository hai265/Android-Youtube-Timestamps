package com.example.uma

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.uma.ui.ComposeActivity
import com.example.uma.ui.noncompose.NonComposeActivity
import com.example.uma.ui.theme.UmaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UmaTheme() {
                Column {
                    ActivityOptions()
                }
            }
        }
    }
}

@Composable
fun ActivityOptions() {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp, 0.dp, 10.dp, 0.dp),
            onClick = {
                context.startActivity(
                    Intent(context, ComposeActivity::class.java)
                )
            }) {
            Text(text = "Compose")
        }

        Button(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(10.dp, 0.dp, 20.dp, 0.dp),
            onClick = {
                context.startActivity(
                    Intent(context, NonComposeActivity::class.java)
                )
            }) {
            Text(text = "Non Compose")
        }
    }
}
