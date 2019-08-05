package ru.skillbranch.devintensive.ui.profile

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    companion object{
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var  viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_ProfileActivity","onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(IS_EDIT_MODE, isEditMode)
    }


    private fun initViewModel() {
        Log.d("M_ProfileActivity","initViewModel")
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
        viewModel.getRepositoryState().observe(this, Observer { updateRepository(it) })
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity","updateTheme")
        delegate.setLocalNightMode(mode)
    }

    private fun updateRepository(isError: Boolean) {
        wr_repository.isErrorEnabled = isError
        wr_repository.error = if (isError) "Невалидный адрес репозитория" else null
    }

    private fun updateUI(profile: Profile) {
        Log.d("M_ProfileActivity","updateUI")
        profile.toMap().also {
            for ((k, v) in viewFields) {
                v.text = it[k].toString()
            }
        }
        updateAvatar(profile)
    }

    private fun updateAvatar(profile: Profile) {
        Log.d("M_ProfileActivity","updateAvatar")

        val initials = Utils.toInitials(profile.firstName, profile.lastName)
        Log.d("M_ProfileActivity","initials ${initials}")
        if (initials != null) {
            Log.d("M_ProfileActivity", "setImageDrawable")
            iv_avatar.setImageDrawable(iv_avatar.createAvatar(initials, this))
        }
        else{
            Log.d("M_ProfileActivity","setImageResource")
            iv_avatar.setImageResource(R.drawable.avatar_default)
            //iv_avatar.setImageDrawable(resources.getDrawable(R.drawable.avatar_default, theme))
        }
    }


    private fun initViews(savedInstanceState: Bundle?) {
        Log.d("M_ProfileActivity","initViews")

        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false

        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener {
            if (wr_repository.isErrorEnabled) {
                et_repository.text?.clear()
                wr_repository.isErrorEnabled = false
                //viewModel.setRepositoryState(false)
            }
            if (isEditMode) {
                saveProfileInfo()
            }
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (Utils.validateRepository(it.toString())) {
                        wr_repository.isErrorEnabled = false
                        wr_repository.error = ""
                    } else {
                        wr_repository.isErrorEnabled = true
                        wr_repository.error = getString(R.string.invalid_address_repository)
                    }
                    viewModel.setRepositoryState(wr_repository.isErrorEnabled)
                }
            }
        })

    }

    private fun showCurrentMode(isEdit: Boolean) {
        Log.d("M_ProfileActivity","showCurrentMode")
        val info = viewFields.filter { setOf("firstName","lastName","about","repository").contains(it.key) }
        for ((_,v) in info){
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if(isEdit) 255 else 0
        }

        ic_eye.visibility = if(isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit) {
            val filter: ColorFilter? = if(isEdit){
                PorterDuffColorFilter(
                        getAccentColor(),
                        PorterDuff.Mode.SRC_IN
                )
            }else{
                null
            }

            val icon = if(isEdit){
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            }else{
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    private fun getAccentColor(): Int {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, value, true)
        return value.data
    }

    private fun saveProfileInfo(){
        Profile(
                firstName = et_first_name.text.toString(),
                lastName = et_last_name.text.toString(),
                about = et_about.text.toString(),
                repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }

}
