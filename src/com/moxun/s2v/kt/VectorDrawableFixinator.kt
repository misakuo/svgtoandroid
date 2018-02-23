package com.moxun.s2v.kt

/**
 * @author eymar
 * @link https://github.com/eymar/DrVectorAndroid
 */

object VectorDrawableFixinator {

    private val regexMap = mapOf(
            "\n" to " ",
            "(-)(\\.\\d)" to " -0$2",
            " (\\.\\d)" to " 0$1",
            "([a-z]|[A-Z])(\\.\\d)" to "$1 0$2"
    )

    @JvmStatic
    fun getContentWithFixedFloatingPoints(value: String): String {
        var result = value as CharSequence

        // some paths may contain commas. Replace commas with spaces
        result = result.replace(",".toRegex(), " ")

        val prepareRegex = "(\\.\\d+)(\\.\\d)".toRegex()

        while (result.contains(prepareRegex)) { // adds spaces
            result = result.replace(prepareRegex, "$1 $2")
        }

        regexMap.forEach {
            result = result.replace(it.key.toRegex(), it.value)
        }


        val finalRegex = "\\s{2,}".toRegex()
        result = result.replace(finalRegex, " ")

        return result.toString()
    }
}