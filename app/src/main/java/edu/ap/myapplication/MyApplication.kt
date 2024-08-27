package edu.ap.myapplication

import android.app.Application
import com.google.firebase.FirebaseApp
import org.osmdroid.config.Configuration
import java.io.File

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Configuration.getInstance().osmdroidBasePath = File(cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache = File(cacheDir, "osmdroid/tiles")
    }
}