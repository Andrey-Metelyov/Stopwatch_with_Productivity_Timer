package org.hyperskill.stopwatch

import android.widget.Button
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

//Version 1.1
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    @Test
    fun testShouldCheckStartButtonExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"startButton\" placed in activity?"
        assertNotNull(message, activity.findViewById<Button>(R.id.startButton))
    }

    @Test
    fun testShouldCheckResetButtonExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"resetButton\" placed in activity?"
        assertNotNull(message, activity.findViewById<Button>(R.id.resetButton))
    }

    @Test
    fun testShouldCheckTextViewExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"textView\" placed in activity?"
        assertNotNull(message, activity.findViewById<TextView>(R.id.textView))
    }

    @Test
    fun testShouldCheckStartButtonText() {
        val activity = activityController.setup().get()

        val message = "in button property \"text\""
        assertEquals(message, "Start", activity.findViewById<Button>(R.id.startButton).text)
    }

    @Test
    fun testShouldCheckResetButtonText() {
        val activity = activityController.setup().get()

        val message = "in button property \"text\""
        assertEquals(message, "Reset", activity.findViewById<Button>(R.id.resetButton).text)
    }

}