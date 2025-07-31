package com.example.fructus.data

import com.example.fructus.R

object DummyFruitDataSource {
    val fruitList = listOf(
        Fruit(
            id = 0,
            name = "Lakatan",
            shelfLife = 24,
            ripeningProcess = true,
            image = R.drawable.img_placeholder,
            ripeningStage = "unripe",
        ),
        Fruit(
            id = 1,
            name = "Cavendish",
            shelfLife = 25,
            ripeningProcess = false,
            image = R.drawable.img_placeholder,
            ripeningStage = "unripe",
        ),
        Fruit(
            id = 2,
            name = "Saba",
            shelfLife = 41,
            ripeningProcess = true,
            image = R.drawable.img_placeholder,
            ripeningStage = "ripe",
        ),
        Fruit(
            id = 22,
            name = "Tomato",
            shelfLife = 4,
            ripeningProcess = false,
            image = R.drawable.img_placeholder,
            ripeningStage = "spoiled",
        ),
        Fruit(
            id = 14,
            name = "Carabao",
            shelfLife = 4,
            ripeningProcess = true,
            image = R.drawable.img_placeholder,
            ripeningStage = "overripe",
        )
    )
}