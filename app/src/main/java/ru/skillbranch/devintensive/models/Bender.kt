package ru.skillbranch.devintensive.models

class Bender(var status:Status = Status.NORMAL,
             var question:Question = Question.NAME) {

    private var isLastQuestion = false
    private var isLimitError = false

    init {
        if (question == Question.IDLE) {
            isLastQuestion = true
        }
        if (status == Status.CRITICAL) {
            isLimitError = true
        }
    }

    fun askQuestion(): String = when(question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun validation(answer: String): String {

        if(answer.isNullOrBlank()) {
            return ""
        }

        val trimmed = answer.trim()

        return when (question) {
            Question.NAME -> if (trimmed.first().isLowerCase()) "Имя должно начинаться с заглавной буквы\n${Question.NAME.question}" else ""
            Question.PROFESSION -> if (trimmed.first().isUpperCase()) "Профессия должна начинаться со строчной буквы\n${Question.PROFESSION.question}" else ""
            Question.MATERIAL -> if (Regex("""\d+""").containsMatchIn(trimmed)) "Материал не должен содержать цифр\n${Question.MATERIAL.question}" else ""
            Question.BDAY -> if (!Regex("""\d+""").matches(trimmed)) "Год моего рождения должен содержать только цифры\n${Question.BDAY.question}" else ""
            Question.SERIAL -> if (!Regex("""\d{7}+""").matches(trimmed)) "Серийный номер содержит только цифры, и их 7\n${Question.SERIAL.question}" else ""
            else -> ""
        }
    }

    fun listenAnswer(answer:String?) :Pair<String, Triple<Int, Int, Int>> {

        when {
            isLastQuestion -> {return "На этом все, вопросов больше нет" to status.color}
            question.answer.contains(answer) -> {
                question = question.nextQuestion()
                if (question == Question.IDLE) {
                    isLastQuestion = true
                }
                return "Отлично - ты справился\n${question.question}" to status.color
            }
            isLimitError -> {
                isLimitError = false
                status = Status.NORMAL
                question = Question.NAME
                return "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
            }
            else -> {
                status = status.nextStatus()
                if (status == Status.CRITICAL) {
                    isLimitError = true
                }
                return "Это неправильный ответ\n${question.question}" to status.color
                }
            }
    }


    enum class Status(val color:Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String,
                        val answer: List<String>) {
        NAME("Как меня зовут?", listOf("бендер","bender")) {
            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик","bender")) {
            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл","дерево","metal","iron","wood")) {
            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion() :Question
    }
}