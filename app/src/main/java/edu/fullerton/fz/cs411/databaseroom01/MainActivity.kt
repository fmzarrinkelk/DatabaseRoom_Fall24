package edu.fullerton.fz.cs411.databaseroom01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

const val TAG = "MyApplication"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val peopleFragment = this.supportFragmentManager.findFragmentById(R.id.fragment_persons_container)
        if ( peopleFragment == null ) {

            val fragment = PeopleFragment()
            this.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_persons_container, fragment)
                .commit()
        }

    }
}