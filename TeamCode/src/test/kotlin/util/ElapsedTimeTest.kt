package util

import com.qualcomm.robotcore.util.ElapsedTime
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.firstinspires.ftc.teamcode.util.isTimedOut
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ElapsedTimeTest {
    private val elapsedTime: ElapsedTime = mockk()

    @BeforeEach
    fun setup() {
        clearMocks(elapsedTime)
    }

    @ParameterizedTest
    @MethodSource("provideElapsedTimeLessThanTarget")
    fun `isTimedOut returns false if elapsed time is less than input`(currentTime: Double, targetTime: Double) {
        every { elapsedTime.milliseconds() } returns currentTime

        assertFalse(elapsedTime.isTimedOut(targetTime))
    }

    @ParameterizedTest
    @MethodSource("provideElapsedTimeGreaterThanOrEqualToTarget")
    fun `isTimedOut returns false if elapsed time is greater than or equal to input`(currentTime: Double, targetTime: Double) {
        every { elapsedTime.milliseconds() } returns currentTime

        assertTrue(elapsedTime.isTimedOut(targetTime))
    }

    companion object {
        @JvmStatic
        fun provideElapsedTimeLessThanTarget(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(0.0, 10.0),
                Arguments.of(Double.MIN_VALUE, 10.0),
                Arguments.of(Double.MAX_VALUE - 1.0, Double.MAX_VALUE),
                Arguments.of(970.999999999, 971.0),
                Arguments.of(10.01, 10.02),
                Arguments.of(10.00001, 10.00002),
                Arguments.of(99.01, 99.1)
            );
        }

        @JvmStatic
        fun provideElapsedTimeGreaterThanOrEqualToTarget(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(11.0, 10.0),
                Arguments.of(10.0, Double.MIN_VALUE),
                Arguments.of(Double.MAX_VALUE, 10.0),
                Arguments.of(99.9999, 99.9998),
                Arguments.of(10.00000001, 10.0)
            );
        }
    }
}