package fakes.drive

import com.qualcomm.hardware.rev.RevBlinkinLedDriver
import com.qualcomm.robotcore.hardware.HardwareDevice.Manufacturer

class FakeRevBlinkinLedDriver(
    port: Int
) : RevBlinkinLedDriver(
    null,
    port
) {
    private var currentPattern: BlinkinPattern = BlinkinPattern.BLUE

    override fun setPattern(pattern: BlinkinPattern) = run { currentPattern = pattern }

    fun getCurrentPattern(): BlinkinPattern = currentPattern

    override fun getManufacturer(): Manufacturer = Manufacturer.Lynx

    override fun getDeviceName(): String = "Fake RevBlinkinledDriver"

    override fun getConnectionInfo(): String = ""

    override fun getVersion(): Int = 1

    override fun resetDeviceConfigurationForOpMode() {}

    override fun close() {}
}