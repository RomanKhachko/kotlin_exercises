package machine

import java.util.*

class CoffeeMachine {
    private var water = 400
    private var milk = 540
    private var coffeeBeans = 120
    private var cups = 9
    private var money = 550

    var isActive = true
    var currentState = State.CHOOSING_ACTION


    enum class State(val stateDirections: String) {
        CHOOSING_ACTION("Write action (buy, fill, take, remaining, exit):") {
            override fun processInput(input: String, coffeeMachine: CoffeeMachine) =
                    coffeeMachine.handleAction(Action.getAction(input))

        },
        BUYING_COFFEE("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:") {
            override fun processInput(input: String, coffeeMachine: CoffeeMachine) {
                coffeeMachine.buy(input)
                coffeeMachine.currentState = CHOOSING_ACTION
            }

        },
        FILLING_WATER("Write how many ml of water do you want to add:") {
            override fun processInput(input: String, coffeeMachine: CoffeeMachine) {
                coffeeMachine.addWater(Integer.valueOf(input))
                coffeeMachine.currentState = FILLING_MILK
            }
        },
        FILLING_MILK("Write how many ml of milk do you want to add:") {
            override fun processInput(input: String, coffeeMachine: CoffeeMachine) {
                coffeeMachine.addMilk(Integer.valueOf(input))
                coffeeMachine.currentState = FILLING_COFFEE_BEANS
            }
        },
        FILLING_COFFEE_BEANS("Write how many grams of coffee beans do you want to add:") {
            override fun processInput(input: String, coffeeMachine: CoffeeMachine) {
                coffeeMachine.addCoffeeBeans(Integer.valueOf(input))
                coffeeMachine.currentState = ADDING_CUPS
            }
        },
        ADDING_CUPS("Write how many disposable cups of coffee do you want to add") {
            override fun processInput(input: String, coffeeMachine: CoffeeMachine) {
                coffeeMachine.addPlasticCups(Integer.valueOf(input))
                coffeeMachine.currentState = CHOOSING_ACTION
            }
        };

        abstract fun processInput(input: String, coffeeMachine: CoffeeMachine)
        fun printStateDirections() {
            println(stateDirections)
        }
    }

    enum class Action {
        BUY,
        FILL,
        TAKE,
        REMAINING,
        EXIT,
        UNSUPPORTED_ACTION;

        companion object {
            fun getAction(input: String): Action {
                val action = input.toUpperCase()
                return if (values().map { action -> action.name }.contains(action)) valueOf(action) else UNSUPPORTED_ACTION
            }
        }

    }

    enum class CoffeeType(val waterRequired: Int = 0, val coffeeBeansRequired: Int = 0, val milkRequired: Int = 0, val price: Int = 0) {
        ESPRESSO(250, 16, 0, 4),
        LATTE(350, 20, 75, 7),
        CAPPUCCINO(200, 12, 100, 6),
        UNSUPPORTED_TYPE;

        companion object {
            fun getCoffeeType(typeNumber: Int): CoffeeType {
                return if (typeNumber <= values().size - 1) values()[typeNumber - 1] else UNSUPPORTED_TYPE
            }
        }
    }

    fun action(input: String) {
        currentState.processInput(input, this)
    }

    fun printCurrentStateDirections() {
        currentState.printStateDirections()
    }

    private fun take() {
        println("I gave you \$$money")
        money = 0
    }

    private fun addWater(water: Int) {
        this.water += water
    }

    private fun addMilk(milk: Int) {
        this.milk += milk
    }

    private fun addCoffeeBeans(coffeeBeans: Int) {
        this.coffeeBeans += coffeeBeans
    }

    private fun addPlasticCups(cups: Int) {
        this.cups += cups
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.BUY -> currentState = State.BUYING_COFFEE
            Action.FILL -> currentState = State.FILLING_WATER
            Action.TAKE -> take()
            Action.REMAINING -> printMachineState()
            Action.EXIT -> isActive = false
            Action.UNSUPPORTED_ACTION -> println("Unsupported operation")
        }
    }

    private fun printMachineState() {
        println("The coffee machine has:")
        println("$water of water")
        println("$milk of milk")
        println("$coffeeBeans of coffee beans")
        println("$cups of disposable cups")
        println("$money of money")
    }

    fun buy(input: String) {
        if (input.equals("back")) {
            return
        }
        val coffeeType = CoffeeType.getCoffeeType(Integer.valueOf(input))
        if (coffeeType == CoffeeType.UNSUPPORTED_TYPE) {
            println("Unsupported option")
        } else {
            serveCupOfCoffee(coffeeType)
        }

    }

    private fun serveCupOfCoffee(coffeeType: CoffeeType) {
        val waterDiff = water - coffeeType.waterRequired
        val milkDiff = milk - coffeeType.milkRequired
        val coffeeBeansDiff = coffeeBeans - coffeeType.coffeeBeansRequired
        if (verifyPossibility(waterDiff, milkDiff, coffeeBeansDiff)) {
            cups--
            money += coffeeType.price
            water = waterDiff
            milk = milkDiff
            coffeeBeans = coffeeBeansDiff
        }
    }

    private fun verifyPossibility(waterDiff: Int, milkDiff: Int, coffeeBeansDiff: Int): Boolean {
        val ingredients = mapOf<String, Int>("water" to waterDiff, "milk" to milkDiff, "coffee beans" to coffeeBeansDiff)
        var isEnough = true
        var neededComponent = ""
        for ((k, v) in ingredients) {
            if (v < 0) {
                isEnough = false
                neededComponent = k
                break
            }
        }
        println(if (isEnough) "I have enough resources, making you a coffee!" else "Sorry, not enough $neededComponent!")
        return isEnough
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    val coffeeMachine = CoffeeMachine()
    while (coffeeMachine.isActive) {
        coffeeMachine.printCurrentStateDirections()
        val input = scanner.next()
        coffeeMachine.action(input)
    }
}

