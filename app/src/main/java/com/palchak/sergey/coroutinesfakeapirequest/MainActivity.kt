package com.palchak.sergey.coroutinesfakeapirequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

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
            val job = launch {
                val result1 = getResult1FromApi()
                setTextOnMainThread(result1)
            }

            val result2 = async {
                getResult2FromApi()
            }
            setTextOnMainThread(result2.await())
        }
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(): String {
        logThread("getResult2FromApi")
        delay(1700)
        return RESULT_2
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