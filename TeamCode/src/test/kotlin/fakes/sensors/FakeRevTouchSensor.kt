package fakes.sensors

import com.qualcomm.hardware.rev.RevTouchSensor

class FakeRevTouchSensor(
    physicalPort: Int
) : RevTouchSensor(
    null,
    physicalPort
) {
    private var pressed = false

    override fun isPressed(): Boolean = pressed

    fun setPressed(flag: Boolean) = run { pressed = flag }

    override fun getDeviceName(): String = "Fake RevTouchSensor"

    override fun getConnectionInfo(): String = ""

    override fun getVersion(): Int = 1

    override fun resetDeviceConfigurationForOpMode() {}

    override fun close() {}
}