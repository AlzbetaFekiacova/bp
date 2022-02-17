package sk.mpage.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PopUpFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_pop_up_window)
    }
}