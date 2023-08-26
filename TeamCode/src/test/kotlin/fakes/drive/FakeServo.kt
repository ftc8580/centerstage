package fakes.drive

import com.qualcomm.robotcore.hardware.HardwareDevice.Manufacturer
import com.qualcomm.robotcore.hardware.ServoController
import com.qualcomm.robotcore.hardware.ServoController.PwmStatus
import com.qualcomm.robotcore.hardware.ServoImpl


class FakeServo(
    private val port: Int
) : ServoImpl(
    FakeServoController(),
    0
) {
    override fun getController(): ServoController? {
        throw IllegalArgumentException("Not implemented")
    }

    override fun getPortNumber(): Int = port

    override fun getManufacturer(): Manufacturer? {
        throw IllegalArgumentException("Not implemented")
    }

    override fun getDeviceName(): String? {
        throw IllegalArgumentException("Not implemented")
    }

    override fun getConnectionInfo(): String? {
        throw IllegalArgumentException("Not implemented")
    }

    internal class FakeServoController : ServoController {
        private var servoPosition = 0.0

        override fun pwmEnable() {}

        override fun pwmDisable() {}

        override fun getPwmStatus(): PwmStatus? = null

        override fun setServoPosition(servo: Int, position: Double) {
            servoPosition = position
        }

        override fun getServoPosition(servo: Int): Double = servoPosition

        override fun getManufacturer(): Manufacturer {
            throw IllegalArgumentException("Not implemented")
        }

        override fun getDeviceName(): String {
            throw IllegalArgumentException("Not implemented")
        }

        override fun getConnectionInfo(): String {
            throw IllegalArgumentException("Not implemented")
        }

        override fun getVersion(): Int = 1

        override fun resetDeviceConfigurationForOpMode() {}

        override fun close() {}
    }
}