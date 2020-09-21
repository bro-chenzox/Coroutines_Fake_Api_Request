package com.palchak.sergey.coroutinesfakeapirequest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            setNewText("Click")

            fakeApiRequest()
        }
    }

    private fun fakeApiRequest() {

        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {

                val result1 =
                    withContext(Default) {
                        println("debug: launching job ${Thread.currentThread().name}")
                        getResult1FromApi()
                    }
                setTextOnMainThread(result1)
                /*
                 * withContext(Dispatchers.Default) { } is the same as chain call async { }.await()
                 */

                val result2 = // firstly argument is result1(instead of "eeeee"
                    withContext(Default) {
                        println("debug: launching job ${Thread.currentThread().name}")
                        try {
                            // firstly argument is result1(instead of "eeeee"
                            getResult2FromApi("eeeee")
                        } catch (e: CancellationException) {
                            e.message
                        }
                    }
                setTextOnMainThread(result2 ?: "Result #1 was incorrect...")

                println("debug: got result2: $result2")
            }
            println("debug: total elapsed time: $executionTime ms.")
        }
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(result1: String): String {
        logThread("getResult2FromApi")
        delay(1700)
        if (result1 == RESULT_1) {
            return RESULT_2
        }
        throw CancellationException("Result #1 was incorrect...")
    }

    private fun logThread(methodName: String) {
        println("debug: $methodName: ${Thread.currentThread().name}")
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
            logThread("setTextOnMainThread")
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }
}