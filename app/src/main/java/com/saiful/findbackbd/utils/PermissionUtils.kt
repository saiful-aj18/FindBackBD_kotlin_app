package com.saiful.findbackbd.utils

import android.Manifest

object PermissionUtils {
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    const val cameraPermission = Manifest.permission.CAMERA
}
