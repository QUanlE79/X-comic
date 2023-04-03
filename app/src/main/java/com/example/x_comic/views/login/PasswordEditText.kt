package com.example.x_comic.views.login

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.x_comic.R

class PasswordEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    private var isPasswordVisible = false
    private val visibilityIcon = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_18)
    private val invisibilityIcon = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_off_18)

    init {
        // Set the bounds of the visibility icon to match the text bounds
        visibilityIcon?.setBounds(0, 0, visibilityIcon.intrinsicWidth, visibilityIcon.intrinsicHeight)
        invisibilityIcon?.setBounds(0, 0, invisibilityIcon.intrinsicWidth, invisibilityIcon.intrinsicHeight)

        // Set the initial input type to password
//        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            // Check if the touch event was inside the visibility icon bounds
            val iconWidth = visibilityIcon?.intrinsicWidth ?: 0
            val iconHeight = visibilityIcon?.intrinsicHeight ?: 0
            if (event.rawX >= width - iconWidth && event.rawY >= (height - iconHeight)) {
                // Toggle the password visibility
                isPasswordVisible = !isPasswordVisible
                transformationMethod = if (isPasswordVisible) {
                    null
                } else {
                    PasswordTransformationMethod.getInstance()
                }
                setSelection(text?.length ?: 0)
                // Set the visibility icon to match the current password visibility state
                val drawables = compoundDrawablesRelative

                // Lấy Drawable của icon ẩn/mở
                val passwordVisibilityIcon = if (isPasswordVisible) visibilityIcon else invisibilityIcon

                // Đặt lại các Drawable cho EditText
                setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], null, passwordVisibilityIcon, null)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
