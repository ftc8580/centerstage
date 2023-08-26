package fakes.sensors

import com.qualcomm.robotcore.hardware.TouchSensor

class FakeTouchSensor : FakeDigitalChannel(), TouchSensor {
    override fun getValue(): Double = if (isPressed()) 1.0 else 0.0

    override fun isPressed(): Boolean = !state
}