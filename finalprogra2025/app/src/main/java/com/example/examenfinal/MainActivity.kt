// FILE: app/src/main/java/com/example/examenfinal/MainActivity.kt
package com.example.examenfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.examenfinal.ui.theme.ExamenfinalTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExamenfinalTheme {
                ExamenFinalApp()
            }
        }
    }
}
