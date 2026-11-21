package pl.uczelnia.projekt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Log.d("CYKL", "MainActivity: onCreate, stan zapisany: " + (savedInstanceState != null))

        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("CYKL", "MainActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CYKL", "MainActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CYKL", "MainActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("CYKL", "MainActivity: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CYKL", "MainActivity: onDestroy (isFinishing=" + isFinishing + ", isChangingConfigurations=" + isChangingConfigurations + ")")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("CYKL", "MainActivity: onRestart")
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
