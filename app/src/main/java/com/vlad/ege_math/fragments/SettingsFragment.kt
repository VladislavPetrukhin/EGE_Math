package com.vlad.ege_math.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.app.NotificationManagerCompat
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.vlad.ege_math.R

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    private val appPackage = "com.vlad.ege_math"
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            Log.d("NotifyLog", "permission: $isGranted")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    //    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(
//            inflater, R.layout.fragment_settings, container, false
//        )
//        return binding.root
//    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val preferenceScreen = preferenceScreen

        for (i in 0 until preferenceScreen.preferenceCount) {   //проходим по всем pref, чтобы выставить выбранное значение
            Log.d("PrefLog", i.toString())
            val preferences = preferenceScreen.getPreference(i)
            if (preferences !is CheckBoxPreference) {
                val value = defaultSharedPref.getString(preferences.key, "").toString()
                setPreferenceLabel(preferences, value)
            }
        }
        onSharedPreferenceChanged(preferenceScreen.sharedPreferences,"pref_key_theme")
        onSharedPreferenceChanged(preferenceScreen.sharedPreferences,"pref_key_font_size")
    }

    private fun setPreferenceLabel(preferences: Preference, value: String) {  //выставляет summary в pref
        if (preferences is ListPreference) {
            val index = preferences.findIndexOfValue(value)
            if (index >= 0) {
                preferences.setSummary(preferences.entries[index])
            }
        }
    }

    override fun onSharedPreferenceChanged(sharefPreferences: SharedPreferences?, key: String?) {
        val preferences = findPreference<Preference>(key.toString()) as Preference
        if (preferences !is CheckBoxPreference) {
            val value = sharefPreferences?.getString(preferences.key, "").toString()
            setPreferenceLabel(preferences, value)
            if (preferences.key.toString() == "pref_key_theme") {   //если была изменена тема в pref - меняем тему приложения
                when (value) {
                    "system" -> {
                        // Установка системной темы
                        Log.d("Theme", "Theme: system")
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }

                    "light" -> {
                        // Установка светлой темы
                        Log.d("Theme", "Theme: light")
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }

                    "dark" -> {
                        // Установка темной темы
                        Log.d("Theme", "Theme: dark")
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
                //recreate(requireActivity())
            }
        } else if (preferences.key.toString() == "pref_key_notifications") {  //подключаем уведомления, если были включены в pref
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            sharedPreferences.edit().putBoolean(key, preferences.isChecked).apply()
            if (preferences.isChecked) {
                val sharedPref =
                    requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("notifyWereRefused", false)
                editor.apply()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notifySetup()
                }
            }
        }
    }
    private fun notifySetup(){
    if(checkNotificationPermission(requireContext())){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }else{
        requestNotificationPermission(requireActivity(),123)
    }
}
    private fun checkNotificationPermission(context: Context): Boolean {
        // Проверяем, разрешено ли отправлять уведомления
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun requestNotificationPermission(activity: Activity, requestCode: Int) {
        // Запрашиваем разрешение на отправку уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, appPackage)
            activity.startActivityForResult(intent, requestCode)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", appPackage, null)
            intent.data = uri
            activity.startActivityForResult(intent, requestCode)
        }
    }
}