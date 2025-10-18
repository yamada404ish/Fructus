package com.example.fructus.ui.guide.model

data class FruitGuide(
    val type: String,
    val fruitName: String,
    val stages: List<FruitStage>
)

data class FruitStage(
    val image: String,
    val stage: String,
    val shelfLifeNatural: String,
    val shelfLifeArtificial: String
)