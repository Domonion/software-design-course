// this is just simple rough converting, disregarding precision, only for consistency with task
enum class CURRENCY {
    USD {
        override fun convert(from: CURRENCY, amount: Double): Double {
            return when (from) {
                USD -> amount
                EUR -> amount * 11 / 10
                RUB -> amount * 80
            }
        }
    },
    EUR {
        override fun convert(from: CURRENCY, amount: Double): Double {
            return when (from) {
                USD -> amount * 10 / 11
                EUR -> amount
                RUB -> amount * 90
            }
        }
    },
    RUB {
        override fun convert(from: CURRENCY, amount: Double): Double {
            return when(from) {
                USD -> amount / 80
                EUR -> amount / 90
                RUB -> amount
            }
        }
    };

    abstract fun convert(from: CURRENCY, amount: Double): Double
}