package util

import org.firstinspires.ftc.teamcode.util.ServoUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ServoUtilTest {
    @Test
    fun `test getSweepTimeMs with default range`() {
        val time = ServoUtil.getSweepTimeMs(0.0, 1.0)
        val expectedTime = ServoUtil.MS_PER_DEGREE * ServoUtil.DEFAULT_DEGREE_RANGE * ServoUtil.THRESHOLD_MULTIPLIER
        assertEquals(expectedTime, time)
    }

    @Test
    fun `test getSweepTimeMs with custom range and default degree`() {
        val time = ServoUtil.getSweepTimeMs(0.0, 1.0, 0.5, 1.5)
        val expectedTime = 1.0 * ServoUtil.DEFAULT_DEGREE_RANGE * ServoUtil.MS_PER_DEGREE * ServoUtil.THRESHOLD_MULTIPLIER
        assertEquals(expectedTime, time)
    }

    @Test
    fun `test getSweepTimeMs with negative positions`() {
        val time = ServoUtil.getSweepTimeMs(-1.0, -0.5)
        val expectedTime = 0.5 * ServoUtil.DEFAULT_DEGREE_RANGE * ServoUtil.MS_PER_DEGREE * ServoUtil.THRESHOLD_MULTIPLIER
        assertEquals(expectedTime, time)
    }

    @Test
    fun `test getSweepTimeMs with currentPosition greater than targetPosition`() {
        val time = ServoUtil.getSweepTimeMs(1.0, 0.5)
        val expectedTime = 0.5 * ServoUtil.DEFAULT_DEGREE_RANGE * ServoUtil.MS_PER_DEGREE * ServoUtil.THRESHOLD_MULTIPLIER
        assertEquals(expectedTime, time)
    }

    @Test
    fun `test getSweepTimeMs with same current and target positions`() {
        val time = ServoUtil.getSweepTimeMs(0.5, 0.5)
        val expectedTime = 0.0
        assertEquals(expectedTime, time)
    }

    // Additional tests can include testing the boundary values, invalid inputs, etc.
}