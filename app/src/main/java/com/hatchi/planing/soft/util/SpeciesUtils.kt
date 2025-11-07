package com.hatchi.planing.soft.util

import com.hatchi.planing.soft.data.model.Species

object SpeciesUtils {
    fun getSpeciesDisplayName(species: Species): String = when (species) {
        Species.CHICKEN -> "Chicken"
        Species.DUCK -> "Duck"
        Species.TURKEY -> "Turkey"
        Species.QUAIL -> "Quail"
        Species.GOOSE -> "Goose"
        Species.CUSTOM -> "Custom"
    }

    fun getSpeciesEmoji(species: Species): String = when (species) {
        Species.CHICKEN -> "🐔"
        Species.DUCK -> "🦆"
        Species.TURKEY -> "🦃"
        Species.QUAIL -> "🐦"
        Species.GOOSE -> "🦢"
        Species.CUSTOM -> "🥚"
    }
}

