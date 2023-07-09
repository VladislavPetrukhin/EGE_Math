package com.vlad.ege_math

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.vlad.ege_math.databinding.ActivityMainBinding
import com.vlad.ege_math.fragments.FeedbackFragment
import com.vlad.ege_math.fragments.HelpFragment
import com.vlad.ege_math.fragments.MainFragment
import com.vlad.ege_math.fragments.MotivationFragment
import com.vlad.ege_math.fragments.RulesFragment
import com.vlad.ege_math.fragments.SettingsFragment
import com.vlad.ege_math.fragments.StatisticsFragment
import java.util.Calendar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "channelID"
    private var contentTitle = ""
    private var contentText = ""
    private var fragmnent = "MainFragment"
    private val TAG = "MainActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(checkNotificationPermission(applicationContext)){  //подключаем уведомления
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    Log.d(TAG, "permission: $isGranted")
                }
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            createNotificationChannel()
            val defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val notifyOn = defaultSharedPref.getBoolean("pref_key_notifications", true)
            Log.d(TAG, "pref_key_notifications $notifyOn")
            if(notifyOn){   //если в pref включены уведы ,то тогда включаем ежедневные уведы
                Log.d("NotifyLog","true")
                //inflateNotificationContent(10)
               // showNotification(applicationContext)
                setupDailyNotification()
            }
        }else{
            val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val notifyWereRefused = sharedPref.getBoolean("notifyWereRefused", false)
            Log.d(TAG, "notifyWereRefused $notifyWereRefused")
            if(!notifyWereRefused){  //если пользователь не выбирал в диалоге про уведомления "больше не показывать", тогда спрашиваем разрешение на уведомления
                showAlertDialog()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                        Log.d(TAG, "permission: $isGranted")
                    }
                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPreferences.getString("pref_key_theme", "system")) {  //устанавлиаем тему приложения
            "system" -> {
                // Установка системной темы
                Log.d(TAG,"Theme: system")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            "light" -> {
                // Установка светлой темы
                Log.d(TAG,"Theme: light")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "dark" -> {
                // Установка темной темы
                Log.d(TAG,"Theme: dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        drawerLayout = binding.drawerLayout
        navView = binding.navigationView

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24) // Устанавливаем значок меню
        setupNavigationDrawer()

        replaceFragment(MainFragment())  //фрагмент который открывается первым
        supportActionBar?.title = resources.getString(R.string.fragment_main_name)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fragment", fragmnent) // Сохранение значения в state
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        when(savedInstanceState.getString("fragment")){ // Извлечение значения из state
            "MainFragment"->{
                replaceFragment(MainFragment())
            }
            "RulesFragment"->{
                replaceFragment(RulesFragment())
            }
            "HelpFragment"->{
                replaceFragment(HelpFragment())
            }
            "MotivationFragment"->{
                replaceFragment(MotivationFragment())
            }
            "StatisticsFragment"->{
                replaceFragment(StatisticsFragment())
            }
            "SettingsFragment"->{
                replaceFragment(SettingsFragment())
            }
            "FeedbackFragment"->{
                replaceFragment(FeedbackFragment())
            }
            else->{
                replaceFragment(MainFragment())
            }
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
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            activity.startActivityForResult(intent, requestCode)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun setupDailyNotification() {  //включает ежедневные уведомления
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        //alarmManager.cancel(pendingIntent)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 18) // Установите желаемое время (час)
        calendar.set(Calendar.MINUTE, 18) // Установите желаемое время (минута)
        calendar.set(Calendar.SECOND, 0) // Установите желаемое время (секунда)

        Log.d("NotifyLog","setupDailyNotification")
        // Устанавливаем повторение каждый день
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun setupNavigationDrawer() {
        navView.setNavigationItemSelectedListener { menuItem ->
            // Обработка выбора элемента навигационного меню
            when (menuItem.itemId) {
                R.id.nav_item1 -> {
                    fragmnent = "MainFragment"
                    replaceFragment(MainFragment())
                    supportActionBar?.title = resources.getString(R.string.fragment_main_name)
                }
                R.id.nav_item2 -> {
                    fragmnent = "RulesFragment"
                    replaceFragment(RulesFragment())
                    supportActionBar?.title = resources.getString(R.string.fragment_rules_name)
                }
                R.id.nav_item3 -> {
                    fragmnent = "HelpFragment"
                    replaceFragment(HelpFragment())
                    supportActionBar?.title = resources.getString(R.string.fragment_help_name)
                }
                R.id.nav_item4 -> {
                    fragmnent = "MotivationFragment"
                    replaceFragment(MotivationFragment())
                    supportActionBar?.title = resources.getString(R.string.fragment_motivation_name)
                }
                R.id.nav_item5 -> {
                    fragmnent = "StatisticsFragment"
                    replaceFragment(StatisticsFragment())
                    supportActionBar?.title = resources.getString(R.string.fragment_statistics_name)
                }
                R.id.nav_item6 -> {
                    fragmnent = "SettingsFragment"
                    replaceFragment(SettingsFragment())
                    supportActionBar?.title = resources.getString(R.string.fragment_settings_name)
                }
                R.id.nav_item7 -> {
                    fragmnent = "FeedbackFragment"
                    replaceFragment(FeedbackFragment())
                    supportActionBar?.title = resources.getString(R.string.fragment_feedback_name)
                }
            }
            // Закрываем Navigation Drawer после выбора элемента
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Обработка нажатия на значок меню в ActionBar (для открытия Navigation Drawer)
        if (item.itemId == android.R.id.home) {
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START)
            }else{
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    private fun inflateNotificationContent(notificationNumber: Int){ //заполняет текст уведомления
        var textResourceId = resources.getIdentifier(
            "notification_title$notificationNumber","string",packageName)
        contentTitle = resources.getString(textResourceId)
        textResourceId = resources.getIdentifier(
            "notification_text$notificationNumber","string",packageName)
        contentText = resources.getString(textResourceId)
    }
        private fun showNotification(context: Context) {  //выводит уведомление
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("MainActivityLog","not granted")
                return
            }
            Log.d("MainActivityLog","granted")
            notify(NOTIFICATION_ID, builder.build())
        }
    }
    private fun showAlertDialog() {  //показывает диалог который просит включить уведомления в настройках приложений
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Уведомления")
            .setMessage("Разрешите уведомления для приложения")
            .setPositiveButton("Разрешить") { dialog, _ ->  //если разрешил - открыает настройки уведомлений приложения
                dialog.dismiss()
                requestNotificationPermission(this,103)
            }
            .setNegativeButton("Не сейчас") { dialog, _ ->  //если не сейчас закрываем диалог, потом спросим еще раз
                dialog.dismiss()
            }
            .setNeutralButton("Нет, больше не спрашивать") { dialog, _ ->  //закрываем диалог и больше не спрашиваем
                val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("notifyWereRefused",true)
                editor.apply()
                dialog.dismiss()
            }
            .setCancelable(false) // Запретить закрытие окна при нажатии вне его пределов

        val dialog = builder.create()
        dialog.show()
    }
}