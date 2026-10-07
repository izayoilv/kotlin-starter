package com.example.notesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
  private lateinit var binding: ActivitySettingsBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySettingsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val prefs = PrefsManager(this)
    binding.nameInput.setText(prefs.userName)
    binding.fontGroup.check(
      when (prefs.fontSize) {
        FontSize.SMALL -> R.id.fontSmall
        FontSize.MEDIUM -> R.id.fontMedium
        FontSize.LARGE -> R.id.fontLarge
      },
    )
    binding.showInfoSwitch.isChecked = prefs.showUserInfo

    binding.saveButton.setOnClickListener {
      prefs.userName = binding.nameInput.text?.toString().orEmpty().trim()
      prefs.fontSize = when (binding.fontGroup.checkedRadioButtonId) {
        R.id.fontSmall -> FontSize.SMALL
        R.id.fontLarge -> FontSize.LARGE
        else -> FontSize.MEDIUM
      }
      prefs.showUserInfo = binding.showInfoSwitch.isChecked
      finish()
    }
  }
}
