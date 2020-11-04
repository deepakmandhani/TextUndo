package com.example.texteditorapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.texteditorapp.ui.main.MainFragment

/**
 * Created by Deepak Mandhani on 2020-06-20.
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val text = sharedPref.getString(PREF_NAME, "")

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(text))
                .commitNow()
        }
    }

    companion object {
        const val PREF_NAME = "editor_store"
        const val PRIVATE_MODE = 0
    }
}
