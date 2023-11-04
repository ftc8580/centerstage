package org.firstinspires.ftc.teamcode.util

class PIDController(
    private val kp: Double = 1.0, // Proportional gain
    private val ki: Double = 0.05, // Integral gain
    private val kd: Double = 0.0  // Derivative gain
) {
    private var previousError: Double = 0.0
    private var integral: Double = 0.0

    fun calculateControlSignal(currentPosition: Double, targetPosition: Double): Double {
        val error = targetPosition - currentPosition

        // Calculate the proportional component
        val proportional = kp * error

        // Calculate the integral component
        integral += ki * error

        // Calculate the derivative component
        val derivative = kd * (error - previousError)

        // Calculate the control signal
        val controlSignal = proportional + integral + derivative

        // Update the previous error for the next iteration
        previousError = error

        return controlSignal
    }
}