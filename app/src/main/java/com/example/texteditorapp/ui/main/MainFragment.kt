package com.example.texteditorapp.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.texteditorapp.MainActivity.Companion.PREF_NAME
import com.example.texteditorapp.MainActivity.Companion.PRIVATE_MODE
import com.example.texteditorapp.R
import com.example.texteditorapp.util.TextEditorUtil
import kotlinx.android.synthetic.main.main_fragment.*

/**
 * Created by Deepak Mandhani on 2020-06-20.
 */

class MainFragment : Fragment() {

    companion object {
        const val EDITOR_TEXT = "text"
        fun newInstance(text: String?) = MainFragment().apply {
            arguments = Bundle().apply {
                putString(EDITOR_TEXT, text)
            }
        }
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var textEditorUtil: TextEditorUtil
    private var localText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localText = arguments?.getString(
            EDITOR_TEXT
        ) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(MainViewModel::class.java)

        et_editor.setText(
            if (localText.isEmpty())
                savedInstanceState?.getString(EDITOR_TEXT) ?: ""
            else
                localText
        )

        textEditorUtil = TextEditorUtil(et_editor)
        btn_undo.setOnClickListener(clickListener)
        btn_clear.setOnClickListener(clickListener)
        et_editor.setOnKeyListener { view, i, event ->
            if (event.keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.ACTION_UP)
                refreshWorCount()
            return@setOnKeyListener false
        }
        refreshWorCount()

        /*        et_editor.setOnFocusChangeListener { _, _ ->
                refreshWorCount()
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
//        localText = ""
//        outState.putString(EDITOR_TEXT, textEditorUtil.getText())
//        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        val sharedPref: SharedPreferences =
            requireActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString(PREF_NAME, textEditorUtil.getText())
        editor.apply()
        super.onDestroy()
    }

    private fun refreshWorCount() {
        tv_word_counter.text = getString(
            R.string.number_of_words,
            textEditorUtil.wordCount()
        )
    }

    private val clickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_undo -> {
                textEditorUtil.undo()
                refreshWorCount()
            }
            R.id.btn_clear -> {
                localText = ""
                textEditorUtil.clearHistory()
                refreshWorCount()
            }
        }
    }

}
