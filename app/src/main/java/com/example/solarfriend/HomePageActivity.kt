package com.example.solarfriend

import android.content.res.AssetFileDescriptor
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class HomePageActivity : AppCompatActivity() {

    private lateinit var tflite: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Load TFLite Model
        tflite = Interpreter(loadModelFile("solarproduction.tflite"))

        // Input Fields
        val windDirectionInput = findViewById<TextInputEditText>(R.id.edittextwinddirection)
        val windSpeedInput = findViewById<TextInputEditText>(R.id.edittextwindspeed)
        val humidityInput = findViewById<TextInputEditText>(R.id.edittexthumidity)
        val avgWindSpeedInput = findViewById<TextInputEditText>(R.id.edittextaveragewindspeeed)
        val avgPressureInput = findViewById<TextInputEditText>(R.id.edittextaveragepressure)
        val temperatureInput = findViewById<TextInputEditText>(R.id.edittexttemperature)

        // Button
        val submitButton = findViewById<MaterialButton>(R.id.btnSubmit)

        // Output TextViews
        val predictionTextView = findViewById<TextView>(R.id.textViewPrediction)
        val suitabilityTextView = findViewById<TextView>(R.id.textViewSuitability)

        submitButton.setOnClickListener {
            // Get input values
            val windDirection = windDirectionInput.text.toString().toFloatOrNull()
            val windSpeed = windSpeedInput.text.toString().toFloatOrNull()
            val humidity = humidityInput.text.toString().toFloatOrNull()
            val avgWindSpeed = avgWindSpeedInput.text.toString().toFloatOrNull()
            val avgPressure = avgPressureInput.text.toString().toFloatOrNull()
            val temperature = temperatureInput.text.toString().toFloatOrNull()

            // Check for empty values
            if (windDirection == null || windSpeed == null || humidity == null ||
                avgWindSpeed == null || avgPressure == null || temperature == null) {
                Toast.makeText(this, "Please enter valid inputs!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create input array for model
            val inputArray = floatArrayOf(
                windDirection, windSpeed, humidity, avgWindSpeed, avgPressure, temperature
            )

            // Run prediction using TFLite
            val predictedSolarMW = runInference(inputArray)

            // Determine Suitability
            val suitability = when {
                predictedSolarMW > 12481.95 -> "Highly Suitable 🌞"
                predictedSolarMW >= 12288.81 -> "Moderately Suitable ⚡"
                else -> "Less Suitable ⛅"
            }

            // Display Results
            predictionTextView.text = "⚡ Predicted Energy (MW): $predictedSolarMW"
            suitabilityTextView.text = "📍 Suitability: $suitability"
        }
    }

    // Load the TFLite model from assets folder
    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Run inference using TFLite model
    private fun runInference(inputData: FloatArray): Float {
        val output = Array(1) { FloatArray(1) }  // Assuming single output
        tflite.run(inputData, output)
        return output[0][0]  // Return predicted solar MW
    }

    override fun onDestroy() {
        super.onDestroy()
        tflite.close()  // Close model interpreter to free memory
    }
}
