package pl.uczelnia.projekt

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform