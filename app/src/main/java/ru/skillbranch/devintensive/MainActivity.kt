package ru.skillbranch.devintensive

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.extensions.isKeyboardOpen
import ru.skillbranch.devintensive.models.Bender

class MainActivity : AppCompatActivity(), View.OnClickListener, TextView.OnEditorActionListener {

    lateinit var mainView : LinearLayout
    lateinit var benderImage: ImageView
    lateinit var textTxt: TextView
    lateinit var messageEt: EditText
    lateinit var sendBtn: ImageView

    lateinit var benderObj: Bender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        benderImage = iv_bender
        textTxt = tv_text
        messageEt = et_message
        sendBtn = iv_send
        mainView = ll_main

        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name
        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name
        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))

        Log.d("M_MainActivity", "onCreate $status $question")

        val (r, g, b) = benderObj.status.color
        benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)

        textTxt.text = benderObj.askQuestion()
        sendBtn.setOnClickListener(this)
        messageEt.setOnEditorActionListener(this)

    }

    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity","onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity","onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity","onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity","onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity","Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity","onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        outState?.putString("STATUS", benderObj.status.name)
        outState?.putString("QUESTION", benderObj.question.name)
        Log.d("M_MainActivity", "onSaveInstanceState ${benderObj.status.name} ${benderObj.question.name}")
        super.onSaveInstanceState(outState)
    }

    override fun onClick(v: View?) {
        if (isKeyboardOpen()) {
            Log.d("M_Activity","keyboard opened")
        } else {
            Log.d("M_Activity","keyboard closed");
        }
        hideKeyboard();
        if(v?.id == R.id.iv_send){
            onClickSend()
        }
    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        return when (p1) {
            EditorInfo.IME_ACTION_DONE -> {
                hideKeyboard()
                onClickSend()
                return true
            }
            else -> false
        }
    }

    private fun onClickSend() {
        val textMessage = messageEt.text.toString()

        val validationMessage = benderObj.validation(textMessage)

        if (validationMessage.isEmpty()) {
            val (phrase, color) = benderObj.listenAnswer(textMessage.trim().toLowerCase())
            messageEt.setText("")

            val (r, g, b) = color
            benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)

            textTxt.text = phrase
        } else {
            messageEt.setText("")
            textTxt.text = validationMessage
        }
    }

}
