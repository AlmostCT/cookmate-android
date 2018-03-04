@file:Suppress("MemberVisibilityCanBePrivate")

package almostct.top.foodhack.model

object DummyData {
    val milk = Product("Молоко", "500 мл")
    val eggs = Product("Яйца", "3 шт.")
    val flour = Product("Мука", "200 г")
    val butter = Product("Масло сливочное", "30 г")
    val sugar = Product("Сахар", "2 ст. ложки")
    val salt = Product("Соль", "1/2 ч. ложки")

    val step1 = RecipeStep(
        1,
        "Взбейте яйца",
        "Взбейте яйца с сахаром, постепенно введите муку и соль.",
        30,
        listOf(eggs, sugar, flour, salt)
    )

    val step2 = RecipeStep(
        stepId = 2,
        shortDescription = "Влейте молоко",
        longDescription = "Влейте молоко и аккуратно размешайте до однородной массы.",
        time = 0,
        products = listOf(milk)
    )

    val step3 = RecipeStep(
        stepId = 3,
        shortDescription = "Оставьте на 20 минут",
        longDescription = "Оставьте на 20 минут.",
        time = 20 * 60,
        products = listOf()
    )

    val step4 = RecipeStep(
        stepId = 4,
        shortDescription = "Добавьте растительное масло",
        longDescription = "Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.",
        time = 0,
        products = listOf(butter)
    )

    val pancake = Recipe(
        timeCreation = 1520121600000,
        name = "Pancake",
        rating = 5,
        tools = "Миска, вилка, венчик и сковорода.",
        weight = "95.3 г",
        proteins = "24.8 г",
        fats = "25.7 г",
        carbohydrates = "95.3 г",
        calories = "709 ккал",
        totalTime = "40 минут",
        picture = null,
        ingredients = listOf(),
        steps = listOf(step1, step2, step3, step4)
    )


    val pancakeAchievement = Achievement(
            name = "pancakeAchievement",
            description = "За лучшие блинчики в твоей жизни!",
        image = "pancake_achievement.png"
    )

    val firstRunAchievement = Achievement(
        name = "firstRun",
        description = "За первый выполненный рецепт!",
        image = "plus_one_achievement.png"
    )

    val acc1 = Account("nickname1", 100, null, listOf())
    val acc2 = Account("nickname2", 10, null, listOf(pancakeAchievement))

    val comments: List<Comment> = listOf(
        Comment(acc1, "a1", "wow", 1000L, 10, 0),
        Comment(acc2, "a2", "Adorable", 1020L, 10, 0),
        Comment(
            acc2,
            "a1",
            "Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде",
            1001L,
            10,
            100
        )
    )
}
