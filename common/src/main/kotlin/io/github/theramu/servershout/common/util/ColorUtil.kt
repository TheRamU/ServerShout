package io.github.theramu.servershout.common.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * @author TheRamU
 * @since 2024/8/25 5:09
 */
object ColorUtil {

    fun translateColor(str: String): String {
        if (str.isEmpty()) return str
        val ary = str.toCharArray()
        val colors = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr"
        for (i in 0 until ary.size - 1) {
            if (ary[i] == '&' && colors.indexOf(ary[i + 1]) > -1) {
                ary[i] = 167.toChar()
                ary[i + 1] = ary[i + 1].lowercaseChar()
            }
        }
        return String(ary)
    }

    fun translateColorBack(str: String): String {
        return str.replace(167.toChar(), '&')
    }

    fun stripColor(str: String): String {
        if (str.isEmpty()) return str
        val result = StringBuilder(str.length)
        val colors = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr"
        var i = 0
        while (i < str.length) {
            if (i < str.length - 1
                && (str[i] == '&' || str[i] == 167.toChar())
                && colors.indexOf(str[i + 1]) > -1
            ) {
                i += 2
            } else {
                result.append(str[i])
                i++
            }
        }
        return result.toString()
    }

    fun deserializeComponent(text: String): Component {
        return LegacyComponentSerializer.legacySection().deserialize(translateColor(text))
    }
}