package almostct.top.foodhack.model

object DummyData {
    val milk = Product("Молоко", "500 мл")
    val eggs = Product("Яйца", "3 шт.")
    val flour = Product("Мука", "200 г")
    val butter = Product("Масло сливочное", "30 г")
    val sugar = Product("Сахар", "2 ст. ложки")
    val salt = Product("Соль", "1/2 ч. ложки")

    val step1 = ReceiptStep(
        1,
        "Взбейте яйца",
        "Взбейте яйца с сахаром, постепенно введите муку и соль.",
        0,
        listOf(eggs, sugar, flour, salt)
    )

    val step2 = ReceiptStep(
        stepId = 2,
        shortDescription = "Влейте молоко",
        longDescription = "Влейте молоко и аккуратно размешайте до однородной массы.",
        time = 0,
        products = listOf(milk)
    )

    val step3 = ReceiptStep(
        stepId = 3,
        shortDescription = "Оставьте на 20 минут",
        longDescription = "Оставьте на 20 минут.",
        time = 20 * 60,
        products = listOf()
    )

    val step4 = ReceiptStep(
        stepId = 4,
        shortDescription = "Добавьте растительное масло",
        longDescription = "Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.Добавьте в тесто растительное масло и жарьте блины на сильно разогретой сковороде.",
        time = 0,
        products = listOf(butter)
    )

    val pancake = Receipt(
        name = "Pancake",
        rating = 5,
        tools = "Миска, вилка, венчик и сковорода.",
        weight = "95.3 г",
        proteins = "24.8 г",
        fats = "25.7 г",
        carbohydrates = "95.3 г",
        calories = "709 ккал",
        totalTime = 40,
        picture = null,
        steps = listOf(step1, step2, step3, step4)
    )
}
