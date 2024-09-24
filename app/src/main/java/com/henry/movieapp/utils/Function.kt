package com.henry.movieapp.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Kiểm tra xem liệu thiết bị đang thực sự có mạng Internet hay không.
 * @param context [Context] của hoạt động hoặc ứng dụng.
 * @return `true` nếu thiết bị có mạng Internet, ngược lại `false`.
 */
fun checkInternetStatus(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        ) {
            return hasInternetAccess()
        }
    }

    return false
}

/**
 * Hiển thị [Toast] với thông báo tùy chỉnh.
 * @param context [Context] của [Activity] muốn hiển thị [Toast].
 * @param message Nội dung thông báo cần hiển thị trên [Toast].
 * @param duration Thời lượng hiển thị của [Toast]. Chỉ có thể là [Toast.LENGTH_SHORT] hoặc [Toast.LENGTH_LONG].
 */
fun displayToast(
    context: Context,
    message: CharSequence,
    duration: Int
) = Toast.makeText(context, message, duration).show()

private fun hasInternetAccess() = try {
    val socket = Socket()
    val socketAddress = InetSocketAddress("8.8.8.8", 53)

    // Thử ping tới DNS trên trong 2 giây
    socket.connect(socketAddress, 2000)
    socket.close()
    // Nếu ping được thì ...
    true
} catch (e: IOException) {
    false
}