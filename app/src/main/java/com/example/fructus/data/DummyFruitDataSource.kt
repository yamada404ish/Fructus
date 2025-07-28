package com.example.fructus.data

import com.example.fructus.R

object DummyFruitDataSource {
    val fruitList = listOf(
        Fruit(
            id = 0,
            name = "Lakatan",
            shelfLife = 2,
            ripeningProcess = true,
            image = R.drawable.cavendish,
            ripeningStage = "overripe",
        ),
        Fruit(
            id = 1,
            name = "Cavendish",
            shelfLife = 4,
            ripeningProcess = false,
            image = R.drawable.lakatan,
            ripeningStage = "unripe",
        ),
        Fruit(
            id = 2,
            name = "Saba",
            shelfLife = 4,
            ripeningProcess = true,
            image = R.drawable.cavendish,
            ripeningStage = "ripe",
        )
    )
}