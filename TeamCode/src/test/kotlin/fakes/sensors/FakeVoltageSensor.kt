package fakes.sensors

import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.VoltageSensor

class FakeVoltageSensor : VoltageSensor {
    private var voltage: Double = 12.0

    override fun getManufacturer(): HardwareDevice.Manufacturer = HardwareDevice.Manufacturer.Other

    override fun getDeviceName(): String = "Fake Voltage Sensor"

    override fun getConnectionInfo(): String = ""

    override fun getVersion(): Int = 0

    override fun resetDeviceConfigurationForOpMode() {}

    override fun close() {}

    override fun getVoltage(): Double = voltage

    fun setVoltage(newVoltage: Double) = run { voltage = newVoltage }
}