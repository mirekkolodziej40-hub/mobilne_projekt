package pl.uczelnia.projekt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

// Obserwator cyklu życia (Laboratorium 3)
@Composable
fun ObserwatorCyklu(nazwa: String) {
    DisposableEffect(Unit) {
        println("CYKL [$nazwa]: kompozycja dodana")
        onDispose { println("CYKL [$nazwa]: kompozycja usunięta") }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        println("CYKL [$nazwa]: ON_START")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        println("CYKL [$nazwa]: ON_RESUME")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        println("CYKL [$nazwa]: ON_PAUSE")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        println("CYKL [$nazwa]: ON_STOP")
    }
}
