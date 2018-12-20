package com.stylingandroid.numberdetector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var numberClassifier: NumberClassifier

    private val mainActivityJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + mainActivityJob)
    private val uiScope = CoroutineScope(Dispatchers.Main + mainActivityJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberClassifier = NumberClassifier()

        finger_canvas.drawingListener = { bitmap ->
            ioScope.launch {
                val start = System.currentTimeMillis()
                numberClassifier.classify(bitmap) { result, confidence, elapsed ->
                    val total = System.currentTimeMillis() - start
                    println("Result: $result, confidence: $confidence, elapsed: ${total}ms total, ${elapsed}ms in ML")
                    uiScope.launch {
                        digit.text  = result.toString()
                    }
                }
            }
        }

        button_clear.setOnClickListener {
            finger_canvas.clear()
            digit.text = ""
        }
    }

    override fun onDestroy() {
        mainActivityJob.cancel()
        super.onDestroy()
    }
}
