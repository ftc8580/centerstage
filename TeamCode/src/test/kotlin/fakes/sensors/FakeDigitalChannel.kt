package fakes.sensors

import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.DigitalChannelController
import com.qualcomm.robotcore.hardware.HardwareDevice

open class FakeDigitalChannel : DigitalChannel {
    private var mode = DigitalChannel.Mode.OUTPUT
    private var state = true

    override fun getManufacturer(): HardwareDevice.Manufacturer = HardwareDevice.Manufacturer.Other

    override fun getDeviceName(): String = "Fake Digital Channel"

    override fun getConnectionInfo(): String = ""

    override fun getVersion(): Int = 0

    override fun resetDeviceConfigurationForOpMode() {}

    override fun close() {}

    override fun getMode(): DigitalChannel.Mode = mode

    override fun setMode(mode: DigitalChannel.Mode) = run { this.mode = mode }

    override fun setMode(mode: DigitalChannelController.Mode?) {}

    override fun getState(): Boolean = state

    override fun setState(state: Boolean) = run { this.state = state }
}