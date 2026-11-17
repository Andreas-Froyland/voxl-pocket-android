package com.tembo.voxl

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplaysTest {
    @Test fun current() {
        val d = Displays.parse(JSONObject("""{"t":"hello","w":1,"h":1,"displays":{"selected":2,
            "available":[{"id":0,"name":"Display 1 (3440x1440) · Main"},{"id":2,"name":"Display 3"}]}}"""))
        assertEquals(2, d.selected)
        assertEquals(listOf(Display(0, "Display 1 (3440x1440) · Main"), Display(2, "Display 3")), d.available)
        assertTrue(d.canSwitch)
        assertEquals("ds,0", d.command(0))
    }

    @Test fun macIds() {
        // macOS ids are CGDirectDisplayIDs, not indexes.
        val d = Displays.parse(JSONObject("""{"displays":{"selected":69733382,
            "available":[{"id":69733382,"name":"Display 1 · Main"},{"id":4,"name":""}]}}"""))
        assertEquals(69733382, d.selected)
        assertEquals("Display 2", d.available[1].name)
        assertEquals("ds,4", d.command(4))
    }

    @Test fun legacy() {
        val d = Displays.parse(JSONObject("""{"displays":3,"display":1}"""))
        assertEquals(1, d.selected)
        assertEquals(3, d.available.size)
        assertEquals("Display 1 (main)", d.available[0].name)
        assertEquals("dp,2", d.command(2))
    }

    @Test fun single() {
        assertFalse(Displays.parse(JSONObject("""{"t":"hello"}""")).canSwitch)
        assertFalse(Displays.parse(JSONObject("""{"displays":1}""")).canSwitch)
        assertFalse(Displays.parse(JSONObject("""{"displays":{"selected":0,"available":[{"id":0,"name":"A"}]}}""")).canSwitch)
    }
}
