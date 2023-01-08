package data.generation

object GenerateNames {

    private val maleNames = listOf<String>(
        "Максим",
        "Александр",
        "Артем",
        "Михаил",
        "Данил",
        "Иван",
        "Алексей",
        "Кирилл",
        "Степан",
        "Вадим"
    )

    private val femaleNames = listOf<String>(
        "Анна",
        "Юлия",
        "София",
        "Виктория",
        "Александра",
        "Алина",
        "Евгения",
        "Елизавета",
        "Ева",
        "Вера"
    )

    private val maleSurnames = listOf<String>(
        "Иванов",
        "Васильев",
        "Петров",
        "Смирнов",
        "Михайлов",
        "Федоров",
        "Соколов",
        "Яковлев",
        "Попов",
        "Андреев",
    )

    private val femaleSurnames = listOf<String>(
        "Иванова",
        "Васильева",
        "Петрова",
        "Смирнова",
        "Михайлова",
        "Федорова",
        "Соколова",
        "Яковлева",
        "Попова",
        "Андреева",
    )

    fun generateName(): String {
        if ((0..1).random() == 0) {
            return "${maleNames[(maleNames.indices).random()]} ${maleSurnames[(maleSurnames.indices).random()]}"
        }
        return "${femaleNames[(femaleNames.indices).random()]} ${femaleSurnames[(femaleSurnames.indices).random()]}"
    }
}