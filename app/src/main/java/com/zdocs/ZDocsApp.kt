package com.zdocs

import android.app.Application
import com.zdocs.util.StorageHelper

/**
 * Application class for ZDocs.
 * Initializes storage directories on first launch.
 */
class ZDocsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize ZDocs directory structure
        StorageHelper(this).initDirectories()
    }
}
