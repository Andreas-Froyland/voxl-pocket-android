package com.tembo.voxl

import org.json.JSONObject

/** A display the host can stream. */
data class Display(val id: Int, val name: String)

/**
 * The host's displays, from `hello`. voxl sends them as
 * `"displays": {"selected": id, "available": [{"id": …, "name": …}]}` and switches on
 * `ds,<id>` (macOS and Windows hosts). An older form, a count in `displays` and an index
 * in `display`, switched on `dp,<index>`; it is still read.
 */
data class Displays(val selected: Int, val available: List<Display>, private val legacy: Boolean = false) {
    val canSwitch get() = available.size > 1

    /** The message that asks the host to stream display `id`. */
    fun command(id: Int) = if (legacy) "dp,$id" else "ds,$id"

    companion object {
        val NONE = Displays(0, emptyList())

        fun parse(hello: JSONObject): Displays {
            hello.optJSONObject("displays")?.let { d ->
                val list = d.optJSONArray("available")
                val available = if (list == null) emptyList() else (0 until list.length()).mapNotNull { i ->
                    list.optJSONObject(i)?.takeIf { it.has("id") }?.let { o ->
                        Display(o.optInt("id"), o.optString("name").ifEmpty { "Display ${i + 1}" })
                    }
                }
                return Displays(d.optInt("selected", available.firstOrNull()?.id ?: 0), available)
            }
            val n = hello.optInt("displays", 1)
            if (n < 2) return NONE
            val names = List(n) { Display(it, if (it == 0) "Display 1 (main)" else "Display ${it + 1}") }
            return Displays(hello.optInt("display", 0), names, legacy = true)
        }
    }
}
