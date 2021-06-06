package com.github.theapache64.corvetee.util

import java.util.*

/**
 * Operations related to reading text input from console
 */
object InputUtils {

    private val scanner by lazy { Scanner(System.`in`) }

    /**
     * Get a String with given prompt as prompt
     */
    fun promptString(prompt: String, isRequired: Boolean): String {
        print(Color.GREEN, "$prompt: ")
        val value = scanner.nextLine()
        while (value.trim().isEmpty() && isRequired) {
            println(Color.RED, "Invalid ${prompt.lowercase(Locale.getDefault())} `$value`")
            return promptString(prompt, isRequired)
        }
        return value
    }

    fun getInt(prompt: String, lowerBound: Int, upperBound: Int, but: Array<Int> = arrayOf()): Int {
        print(Color.GREEN, "$prompt: ")

        val sVal = scanner.nextLine()
        try {
            val value = sVal.toInt()
            if (!but.contains(value) && (value < lowerBound || value > upperBound)) {
                // error
                println(Color.RED, "Input must be between $lowerBound and $upperBound")
                return getInt(prompt, lowerBound, upperBound)
            }
            return value
        } catch (e: NumberFormatException) {
            println("Invalid input `$sVal`")
            return getInt(prompt, lowerBound, upperBound)
        }
    }

}
