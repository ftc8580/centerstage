package fakes.drive

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.ServoController

class FakeCRServo : CRServo {
    private var power = 0.0
    private var direction = DcMotorSimple.Direction.FORWARD

    override fun getManufacturer(): HardwareDevice.Manufacturer = HardwareDevice.Manufacturer.Other

    override fun getDeviceName(): String = "Fake CR Servo"

    override fun getConnectionInfo(): String = ""

    override fun getVersion(): Int = 1

    override fun resetDeviceConfigurationForOpMode() {}

    override fun close() {}

    override fun setDirection(direction: DcMotorSimple.Direction) {
        this.direction = direction
    }

    override fun getDirection(): DcMotorSimple.Direction = direction

    override fun setPower(power: Double) {
        this.power = power
    }

    override fun getPower(): Double = power

    override fun getController(): ServoController {
        throw IllegalArgumentException("Not implemented")
    }

    override fun getPortNumber(): Int = 0
}