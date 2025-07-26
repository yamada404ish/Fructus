package com.example.fructus.data

import com.example.fructus.R

object DummyFruitDataSource {
    val fruitList = listOf(
        Fruit(
            id = 0,
            name = "Lakatan",
            shelfLife = 2,
            ripeningProcess = true,
            image = R.drawable.cavendish, //di pa to
            ripeningStage = "overripe",
        )
    )
}