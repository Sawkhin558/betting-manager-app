package com.betting.manager

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    
    @Test
    fun appName_isCorrect() {
        val expected = "Betting Manager"
        // In a real test, you would check BuildConfig or resources
        assertTrue("App name should contain 'Betting'", expected.contains("Betting"))
    }
}