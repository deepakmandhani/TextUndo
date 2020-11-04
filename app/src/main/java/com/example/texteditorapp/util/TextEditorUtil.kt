package com.example.texteditorapp.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.*

/**
 * Created by Deepak Mandhani on 2020-06-20.
 */

class TextEditorUtil(private var editText: EditText) {

    var index: Int = 0
    private var history: Stack<HistoryEntry> = Stack()
    private var historyBack: Stack<HistoryEntry> = Stack()
    private var editable: Editable
    private var flag = false

    init {
        this.editable = editText.text
        editText.addTextChangedListener(Watcher())
    }

    fun getText() = editable.toString()

    fun setText(string: String) = editText.setText(string)

    fun undo() {
        if (history.empty()) return
        flag = true
        val action = history.pop()
        historyBack.push(action)
        if (action.isAdd) {
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length)
            editText.setSelection(action.startCursor, action.startCursor)
        } else {
            editable.insert(action.startCursor, action.actionTarget)
            if (action.endCursor === action.startCursor) {
                editText.setSelection(action.startCursor + action.actionTarget.length)
            } else {
                editText.setSelection(action.startCursor, action.endCursor)
            }
        }
        flag = false
        if (!history.empty() && history.peek().index == action.index) {
            undo()
        }

    }

    fun wordCount(): Int {
        val text = editable.toString().trim()
        return if (text.isEmpty())
            0
        else text.split(Regex("\\s+")).size
    }

    fun clearHistory() {
        setText("")
        history.clear()
        historyBack.clear()
    }

    private inner class Watcher : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (flag) return
            val end = start + count
            if (end > start && end <= s.length) {
                val charSequence = s.subSequence(start, end)
                if (charSequence.isNotEmpty()) {
                    val action = HistoryEntry(charSequence, start, false)
                    if (count > 1) {
                        action.setSelectCount(count)
                    } else if (count == 1 && count == after) {
                        action.setSelectCount(count)
                    }
                    history.push(action)
                    historyBack.clear()
                    action.setIndex(++index)
                }
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (flag) return
            val end = start + count
            if (end > start) {
                val charSequence = s.subSequence(start, end)
                if (charSequence.isNotEmpty()) {
                    val action = HistoryEntry(charSequence, start, true)
                    history.push(action)
                    historyBack.clear()
                    if (before > 0) {
                        action.setIndex(index)
                    } else {
                        action.setIndex(++index)
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable) {
            if (flag) return
            if (s !== editable) {
                editable = s
            }
        }
    }

    private inner class HistoryEntry(
        internal var actionTarget: CharSequence,
        internal var startCursor: Int,
        internal var isAdd: Boolean
    ) {
        internal var endCursor: Int = 0
        internal var index: Int = 0

        init {
            this.endCursor = startCursor
        }

        fun setSelectCount(count: Int) {
            this.endCursor = endCursor + count
        }

        fun setIndex(index: Int) {
            this.index = index
        }
    }
}