package fakes.util

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier.Notifications
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import fakes.drive.FakeCRServo
import fakes.drive.FakeDcMotorEx
import fakes.drive.FakeRevBlinkinLedDriver
import fakes.drive.FakeServo
import fakes.sensors.FakeDigitalChannel
import fakes.sensors.FakeDistanceSensor
import fakes.sensors.FakeRevTouchSensor
import fakes.sensors.FakeVoltageSensor
import org.w3c.dom.Document
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory


class FakeHardwareMapFactory {
    private class HardwareMapCreator {
        private val deviceNames: MutableSet<String?> = HashSet()
        val hardwareMap: HardwareMap =
            object : HardwareMap(null, object : OpModeManagerNotifier {
                override fun registerListener(listener: Notifications): OpMode? {
                    return null
                }

                override fun unregisterListener(listener: Notifications) {}
            }) {
                override fun <T> tryGet(classOrInterface: Class<out T>, deviceName: String): T? {
                    synchronized(lock) {
                        val trimmedDeviceName = deviceName.trim { it <= ' ' }
                        val list: List<HardwareDevice>? = allDevicesMap[trimmedDeviceName]
                        var result: T? = null
                        if (list != null) {
                            for (device in list) {
                                if (classOrInterface.isInstance(device)) {
                                    result = classOrInterface.cast(device)
                                    break
                                }
                            }
                        }
                        return result
                    }
                }
            }

        fun parseUsingDocBuilder(fileInput: InputStream) {
            val builderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = builderFactory.newDocumentBuilder()
            val doc: Document = docBuilder.parse(fileInput)

            // FIXME: Create LynxUsbModules by "lookback" from device nodes so that we can
            //        build fake motor controllers and the like eventually
            addAllMotors(doc)
            addAllServos(doc)
            addAllDigitalChannels(doc)
            addAllRevTouchSensors(doc)
            addAllRevBlinkinLedDrivers(doc)
            addAllDistanceSensors(doc)

            // FIXME: Add implementations for things we don't support, but need:
            // IMU, LynxColorSensor, RevColorSensorV3, AnalogInput
            hardwareMap.voltageSensor.put("Voltage Sensor", FakeVoltageSensor())
        }

        private fun addAllDistanceSensors(doc: Document) {
            val distanceSensors: NodeList = doc.getElementsByTagName(REV_DISTANCE_SENSOR_TAG_NAME)
            addDevices(distanceSensors, object : DeviceFromXml {
                override fun addDeviceToHardwareMap(name: String?, portNumber: Int) {
                    val fakeDistanceSensor = FakeDistanceSensor()
                    hardwareMap.put(name, fakeDistanceSensor)

                    // No DeviceMapping for the Rev2mDistanceSensor
                }
            })
        }

        private fun addAllDigitalChannels(doc: Document) {
            val digitalChannels: NodeList = doc.getElementsByTagName(DIGITAL_DEVICE_TAG_NAME)
            addDevices(digitalChannels, object : DeviceFromXml {
                override fun addDeviceToHardwareMap(name: String?, portNumber: Int) {
                    val fakeDigitalChannel = FakeDigitalChannel()
                    hardwareMap.put(name, fakeDigitalChannel)
                    hardwareMap.digitalChannel.put(name, fakeDigitalChannel)
                }
            })
        }

        private fun addAllRevTouchSensors(doc: Document) {
            val revTouchSensors: NodeList = doc.getElementsByTagName(REV_TOUCH_SENSOR_TAG_NAME)
            addDevices(revTouchSensors, object : DeviceFromXml {
                override fun addDeviceToHardwareMap(name: String?, portNumber: Int) {
                    val fakeRevTouchSensor = FakeRevTouchSensor(portNumber)
                    hardwareMap.put(name, fakeRevTouchSensor)
                    hardwareMap.touchSensor.put(name, fakeRevTouchSensor)
                }
            })
        }

        private fun addAllRevBlinkinLedDrivers(doc: Document) {
            val blinkinLeds: NodeList = doc.getElementsByTagName(REV_BLINKIN_LED_DRIVER_TAG_NAME)
            addDevices(blinkinLeds, object : DeviceFromXml {
                override fun addDeviceToHardwareMap(name: String?, portNumber: Int) {
                    hardwareMap.put(name, FakeRevBlinkinLedDriver(portNumber))
                }
            })
        }

        private fun addAllMotors(doc: Document) {
            // FIXME: There are other motor tag names:
            // Bonus points - how do you add these without a lot of copy-and-paste code?
            //                NeveRest3.7v1Gearmotor
            //                NeveRest20Gearmotor
            //                NeveRest40Gearmotor
            //                NeveRest60Gearmotor
            //                Matrix12vMotor
            //                TetrixMotor
            //                goBILDA5201SeriesMotor name="[...]" port="[...]" />
            //                goBILDA5202SeriesMotor
            //                RevRobotics20HDHexMotor name="[...]" port="[...]" />
            //                RevRobotics40HDHexMotor name="[...]" port="[...]" />
            //                RevRoboticsCoreHexMotor name="[...]" port="[...]" />
            val dcMotors: NodeList = doc.getElementsByTagName(MOTOR_TAG_NAME)
            addDevices(dcMotors, object : DeviceFromXml {
                override fun addDeviceToHardwareMap(name: String?, portNumber: Int) {
                    val fakeDcMotorEx = FakeDcMotorEx()
                    hardwareMap.put(name, fakeDcMotorEx)
                    hardwareMap.dcMotor.put(name, fakeDcMotorEx)
                }
            })
        }

        private fun addAllServos(doc: Document) {
            // FIXME: We don't yet support ContinuousRotationServo or RevSPARKMini
            val servos: NodeList = doc.getElementsByTagName(SERVO_TAG_NAME)
            addDevices(servos, object : DeviceFromXml {
                override fun addDeviceToHardwareMap(name: String?, portNumber: Int) {
                    val fakeServo = FakeServo(portNumber)
                    hardwareMap.put(name, fakeServo)
                    hardwareMap.servo.put(name, fakeServo)
                }
            })
            val crServos: NodeList = doc.getElementsByTagName(CR_SERVO_TAG_NAME)
            addDevices(crServos, object : DeviceFromXml {
                override fun addDeviceToHardwareMap(name: String?, portNumber: Int) {
                    val fakeCrServo = FakeCRServo()
                    hardwareMap.put(name, fakeCrServo)
                    hardwareMap.crservo.put(name, fakeCrServo)
                }
            })
        }

        private fun addDevices(deviceNodeList: NodeList, deviceAdder: DeviceFromXml) {
            for (i in 0 until deviceNodeList.length) {
                val deviceNode: Node = deviceNodeList.item(i)
                val attributesByName: NamedNodeMap = deviceNode.attributes
                val nameNode: Node = attributesByName.getNamedItem("name")
                val nameValue: String = nameNode.nodeValue.trim { it <= ' ' }
                require(!deviceNames.contains(nameValue)) {
                    // This isn't exactly real hardware map behavior, but it prevents
                    // problems at runtime if you are using
                    java.lang.String.format(
                        "Non unique device name '%s' for device type '%s'",
                        nameValue, deviceNode.nodeName
                    )
                }
                deviceNames.add(nameValue)
                val portNode: Node = attributesByName.getNamedItem("port")
                val portValue: Int = Integer.valueOf(portNode.nodeValue)
                deviceAdder.addDeviceToHardwareMap(nameValue, portValue)
            }
        }
    }

    internal interface DeviceFromXml {
        fun addDeviceToHardwareMap(name: String?, portNumber: Int)
    }

    companion object {
        const val MOTOR_TAG_NAME = "Motor"
        const val SERVO_TAG_NAME = "Servo"
        const val LYNX_USB_DEVICE_TAG_NAME = "LynxUsbDevice"
        const val ROBOT_TAG_NAME = "Robot"
        const val LYNX_MODULE_TAG_NAME = "LynxModule"
        const val REV_DISTANCE_SENSOR_TAG_NAME = "REV_VL53L0X_RANGE_SENSOR"
        const val DIGITAL_DEVICE_TAG_NAME = "DigitalDevice"
        const val REV_TOUCH_SENSOR_TAG_NAME = "RevTouchSensor"
        const val REV_BLINKIN_LED_DRIVER_TAG_NAME = "RevBlinkinLedDriver"
        const val CR_SERVO_TAG_NAME = "ContinuousRotationServo"

        /**
         * Loads the hardware map with the name &quot;hardwareMapName&quot; from the location used by
         * the FTC SDK when storing hardware map XML files alongside your robot sourcecode and provides
         * fake implementations for the devices found.
         *
         * @param hardwareMapName the name of the hardware map XML file
         * @return a HardwareMap that provides fake implementations for devices found in the file
         */
        fun getFakeHardwareMap(hardwareMapName: String): HardwareMap {
            val path = String.format("src/main/res/xml/%s", hardwareMapName)
            return getFakeHardwareMap(File(path))
        }

        /**
         * Loads the hardware map from the given path
         */
        private fun getFakeHardwareMap(hardwareMapFile: File): HardwareMap {
            val hwMapCreator = HardwareMapCreator()
            if (!hardwareMapFile.exists()) {
                throw IOException("Hardware map file '" + hardwareMapFile.absolutePath + "' does not exist")
            }
            if (!hardwareMapFile.canRead()) {
                throw IOException("Hardware map file '" + hardwareMapFile.absolutePath + "' is not readable")
            }
            hwMapCreator.parseUsingDocBuilder(FileInputStream(hardwareMapFile))
            return hwMapCreator.hardwareMap
        }
    }
}