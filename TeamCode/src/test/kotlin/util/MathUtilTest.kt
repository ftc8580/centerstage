package util

import org.firstinspires.ftc.teamcode.util.MathUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MathUtilTest {
    @Test
    @DisplayName("Test isWithinRange with double arguments")
    fun testIsWithinRangeDouble() {
        assertTrue(MathUtil.isWithinRange(0.0, 10.0, 5.0))
        assertFalse(MathUtil.isWithinRange(0.0, 10.0, -10.0))
        assertFalse(MathUtil.isWithinRange(0.0, 10.0, 15.0))
    }

    @Test
    @DisplayName("Test isWithinRange with int arguments")
    fun testIsWithinRangeInt() {
        assertTrue(MathUtil.isWithinRange(0, 10, 5))
        assertFalse(MathUtil.isWithinRange(0, 10, -10))
        assertFalse(MathUtil.isWithinRange(0, 10, 15))
    }

    @Test
    @DisplayName("Test clamp with double arguments")
    fun testClampDouble() {
        assertEquals(5.0, MathUtil.clamp(5.0, 10.0, 2.0))
        assertEquals(5.0, MathUtil.clamp(5.0, 10.0, 5.0))
        assertEquals(5.1, MathUtil.clamp(5.0, 10.0, 5.1))
        assertEquals(10.0, MathUtil.clamp(5.0, 10.0, 10.0))
        assertEquals(10.0, MathUtil.clamp(5.0, 10.0, 15.0))
    }

    @Test
    @DisplayName("Test clamp with int arguments")
    fun testClampInt() {
        assertEquals(5, MathUtil.clamp(5, 10, 2))
        assertEquals(5, MathUtil.clamp(5, 10, 5))
        assertEquals(6, MathUtil.clamp(5, 10, 6))
        assertEquals(10, MathUtil.clamp(5, 10, 10))
        assertEquals(10, MathUtil.clamp(5, 10, 15))
    }

    @ParameterizedTest
    @CsvSource("1.234567, 1.235", "2.789012, 2.789", "3.0, 3.0")
    @DisplayName("Test round function")
    fun testRound(input: Double, expected: Double) {
        val result = MathUtil.round(input)
        assertEquals(expected, result, 0.001) // Allow for a small error due to rounding
    }
}