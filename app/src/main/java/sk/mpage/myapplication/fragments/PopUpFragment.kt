package sk.mpage.myapplication.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sk.mpage.myapplication.R

class PopUpFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_pop_up_window)
    }
}