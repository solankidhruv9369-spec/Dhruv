package com.example.data.service

import com.example.data.model.CommandResult
import java.net.URLEncoder

object CommandProcessor {

    fun processCommand(input: String): CommandResult {
        val query = input.trim().lowercase()

        // 1. YouTube
        if (query.startsWith("open youtube") || query == "youtube") {
            return CommandResult(
                isBrowserAction = true,
                action = "Opening YouTube for you right now.",
                url = "https://www.youtube.com"
            )
        }
        if (query.startsWith("search youtube for ") || query.startsWith("play ") && query.contains(" on youtube")) {
            val searchTerm = query.removePrefix("search youtube for ")
                .removePrefix("play ")
                .removeSuffix(" on youtube")
                .trim()
            val encoded = URLEncoder.encode(searchTerm, "UTF-8")
            return CommandResult(
                isBrowserAction = true,
                action = "Searching YouTube for $searchTerm.",
                url = "https://www.youtube.com/results?search_query=$encoded"
            )
        }

        // 2. Google Search
        if (query.startsWith("search google for ") || query.startsWith("google ")) {
            val searchTerm = query.removePrefix("search google for ").removePrefix("google ").trim()
            val encoded = URLEncoder.encode(searchTerm, "UTF-8")
            return CommandResult(
                isBrowserAction = true,
                action = "Searching Google for $searchTerm.",
                url = "https://www.google.com/search?q=$encoded"
            )
        }
        if (query == "open google") {
            return CommandResult(
                isBrowserAction = true,
                action = "Opening Google.",
                url = "https://www.google.com"
            )
        }

        // 3. Spotify / Music
        if (query.startsWith("open spotify") || query == "spotify" || query == "play music") {
            return CommandResult(
                isBrowserAction = true,
                action = "Opening Spotify for your favorite tunes.",
                url = "https://open.spotify.com"
            )
        }

        // 4. GitHub
        if (query.startsWith("open github") || query == "github") {
            return CommandResult(
                isBrowserAction = true,
                action = "Opening GitHub repositories.",
                url = "https://github.com"
            )
        }

        // 5. Maps / Navigation
        if (query.startsWith("open maps") || query.startsWith("navigate to ") || query.startsWith("directions to ")) {
            val destination = query.removePrefix("open maps to ")
                .removePrefix("navigate to ")
                .removePrefix("directions to ")
                .removePrefix("open maps")
                .trim()
            val encoded = URLEncoder.encode(destination, "UTF-8")
            val url = if (destination.isNotEmpty()) "https://www.google.com/maps/search/?api=1&query=$encoded" else "https://www.google.com/maps"
            return CommandResult(
                isBrowserAction = true,
                action = if (destination.isNotEmpty()) "Opening directions to $destination." else "Opening Google Maps.",
                url = url
            )
        }

        // 6. Wikipedia
        if (query.startsWith("wikipedia ") || query.startsWith("search wikipedia for ")) {
            val topic = query.removePrefix("search wikipedia for ").removePrefix("wikipedia ").trim()
            val encoded = URLEncoder.encode(topic, "UTF-8")
            return CommandResult(
                isBrowserAction = true,
                action = "Looking up $topic on Wikipedia.",
                url = "https://en.wikipedia.org/wiki/Special:Search?search=$encoded"
            )
        }

        // 7. Weather
        if (query.contains("weather") && (query.startsWith("check") || query.startsWith("open") || query.startsWith("what is the"))) {
            return CommandResult(
                isBrowserAction = true,
                action = "Checking the latest weather forecast.",
                url = "https://www.google.com/search?q=weather"
            )
        }

        // 8. News
        if (query.startsWith("open news") || query == "latest news" || query == "news") {
            return CommandResult(
                isBrowserAction = true,
                action = "Loading the latest top news headlines.",
                url = "https://news.google.com"
            )
        }

        // Not a direct browser command -> chit-chat / companion reasoning
        return CommandResult(isBrowserAction = false, action = "")
    }
}
