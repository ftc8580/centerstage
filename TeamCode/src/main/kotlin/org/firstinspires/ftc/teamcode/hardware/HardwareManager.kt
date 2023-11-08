package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.Servo.Direction
import com.qualcomm.robotcore.hardware.TouchSensor
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.util.Encoder
import org.firstinspires.ftc.teamcode.util.LynxModuleUtil
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import java.lang.Exception

class HardwareManager(private val config: CDConfig, val hardwareMap: HardwareMap) {
    lateinit var batteryVoltageSensor: VoltageSensor
    private lateinit var leftFrontMotor: DcMotorEx
    private lateinit var leftRearMotor: DcMotorEx
    private lateinit var rightRearMotor: DcMotorEx
    private lateinit var rightFrontMotor: DcMotorEx

    lateinit var driveMotors: List<DcMotorEx>

    // Subsystem motors
    var viperMotor: DcMotorEx? = null
    var transferMotor: DcMotorEx? = null
    var intakeMotor: DcMotorEx? = null
    var suspendMotor: DcMotorEx? = null

    // Servos
    var bucketServo: Servo? = null
    var droneServo: Servo? = null
    var viperAngleServo: Servo? = null
    var suspendServoLeft: Servo? = null
    var suspendServoRight: Servo? = null

    // Dead wheels
    var leftEncoder: Encoder? = null
    var rightEncoder: Encoder? = null
    var frontEncoder: Encoder? = null

    // Sensors
    var viperPot: AnalogInput? = null
    var viperTouch: TouchSensor? = null

    // DoubleVision
    var webcam: WebcamName? = null
    lateinit var cameraDirection: BuiltinCameraDirection

    init {
        systemCheck(hardwareMap)
        initializeBatteryVoltageSensor(hardwareMap)
        initializeLynxModules(hardwareMap)
        initializeWheelLocalizers(hardwareMap)
        initializeDriveMotors(hardwareMap)
        initializeSubsystemMotors(hardwareMap)
        initializeServos(hardwareMap)
        initializeWebcam(hardwareMap)
        initializeSensors(hardwareMap)
    }

    private fun systemCheck(hardware: HardwareMap)  {
        LynxModuleUtil.ensureMinimumFirmwareVersion(hardware)
    }

    private fun initializeBatteryVoltageSensor(hardware: HardwareMap) {
        batteryVoltageSensor = hardware.voltageSensor.iterator().next()
    }

    private fun initializeLynxModules(hardware: HardwareMap) {
        for (module in hardware.getAll(LynxModule::class.java)) {
            module.bulkCachingMode = LynxModule.BulkCachingMode.AUTO
        }
    }

    private fun initializeWheelLocalizers(hardware: HardwareMap) {
        val leftEncoderMotor = safelyGetHardware<DcMotorEx>(hardware, config.driveMotors.leftFront)
        val rightEncoderMotor = safelyGetHardware<DcMotorEx>(hardware, config.driveMotors.rightFront)
        val frontEncoderMotor = safelyGetHardware<DcMotorEx>(hardware, config.driveMotors.rightRear)

        // If any of the encoders are missing, don't initialize any of them
        if (leftEncoderMotor == null || rightEncoderMotor == null || frontEncoderMotor == null) return

        leftEncoder = Encoder(leftEncoderMotor)
        rightEncoder = Encoder(rightEncoderMotor)
        frontEncoder = Encoder(frontEncoderMotor)

        // frontEncoder!!.direction = Encoder.Direction.REVERSE
    }

    private fun initializeDriveMotors(hardware: HardwareMap) {
        leftFrontMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.leftFront)
        leftRearMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.leftRear)
        rightRearMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.rightRear)
        rightFrontMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.rightFront)

        leftFrontMotor.direction = DcMotorSimple.Direction.REVERSE
        leftRearMotor.direction = DcMotorSimple.Direction.REVERSE
        rightRearMotor.direction = DcMotorSimple.Direction.FORWARD
        rightFrontMotor.direction = DcMotorSimple.Direction.FORWARD

        driveMotors = listOf(leftFrontMotor, leftRearMotor, rightRearMotor, rightFrontMotor)

        for (motor in driveMotors) {
            val motorConfigurationType = motor.motorType.clone()
            motorConfigurationType.achieveableMaxRPMFraction = 1.0
            motor.motorType = motorConfigurationType

            // Set zero power behavior
            motor.zeroPowerBehavior = ZeroPowerBehavior.BRAKE

            // Run without encoder since we're using dead wheels
            motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    private fun initializeWebcam(hardware: HardwareMap) {
        webcam = safelyGetHardware<WebcamName>(hardware, "webcam")
        cameraDirection = BuiltinCameraDirection.BACK
    }

    private fun initializeSubsystemMotors(hardware: HardwareMap) {
        viperMotor = safelyGetHardware<DcMotorEx>(hardware, config.viperMotor)
        viperMotor?.zeroPowerBehavior = ZeroPowerBehavior.BRAKE
        viperMotor?.mode = DcMotor.RunMode.RUN_USING_ENCODER

        transferMotor = safelyGetHardware<DcMotorEx>(hardware, config.transferMotor)
        transferMotor?.zeroPowerBehavior = ZeroPowerBehavior.FLOAT
        transferMotor?.direction = DcMotorSimple.Direction.REVERSE

        intakeMotor = safelyGetHardware<DcMotorEx>(hardware, config.intakeMotor)
        suspendMotor = safelyGetHardware<DcMotorEx>(hardware, config.suspendMotor)
    }

    private fun initializeServos(hardware: HardwareMap) {
        bucketServo = safelyGetHardware<Servo>(hardware, config.bucketServo)
        droneServo = safelyGetHardware<Servo>(hardware, config.droneServo)
        viperAngleServo = safelyGetHardware<Servo>(hardware, config.viperAngleServo)
        suspendServoLeft = safelyGetHardware<Servo>(hardware, config.suspendServoLeft)
        suspendServoRight = safelyGetHardware<Servo>(hardware, config.suspendServoRight)

        bucketServo?.position = 1.0
        droneServo?.position = 0.0
        droneServo?.direction = Direction.REVERSE
        viperAngleServo?.direction = Direction.REVERSE
        suspendServoRight?.direction = Direction.REVERSE
    }

    private fun initializeSensors(hardware: HardwareMap) {
        viperPot = safelyGetHardware<AnalogInput>(hardware, config.viperPot)
        viperTouch = safelyGetHardware<TouchSensor>(hardware, config.viperTouch)
    }

    private inline fun <reified T> safelyGetHardware(hardware: HardwareMap, deviceName: String?): T? {
        if (deviceName.isNullOrBlank()) return null

        return try {
            hardware.get(T::class.java, deviceName)
        } catch (e: Exception) {
            // Ignore exception and return null
            null
        }
    }

    val rawExternalHeading: Double
        get() = 0.0

    val externalHeadingVelocity: Double
        get() = 0.0

    fun setMotorPowers(frontLeft: Double, rearLeft: Double, rearRight: Double, frontRight: Double) {
        leftFrontMotor.power = frontLeft
        leftRearMotor.power = rearLeft
        rightRearMotor.power = rearRight
        rightFrontMotor.power = frontRight
    }
}