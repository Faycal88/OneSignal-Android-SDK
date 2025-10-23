package com.onesignal.core.internal.http.impl

import com.onesignal.core.BuildConfig
import com.onesignal.core.internal.config.ConfigModelStore
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

internal class HttpConnectionFactory(
    private val _configModelStore: ConfigModelStore,
) : IHttpConnectionFactory {

    private fun baseUrl(): String {
        // Prefer runtime config (apiUrl), else BuildConfig, else fallback
        val cfg = _configModelStore.model.apiUrl
        val fromCfg = if (!cfg.isNullOrEmpty()) cfg else BuildConfig.ONESIGNAL_BASE_URL
        val fallback = fromCfg ?: "https://onesignal.com/api/v1/"
        return if (fallback.endsWith("/")) fallback else "$fallback/"
    }

    private fun resolve(pathOrUrl: String): String {
        return if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
            pathOrUrl
        } else {
            baseUrl() + pathOrUrl.trimStart('/')
        }
    }

    @Throws(IOException::class)
    override fun newHttpURLConnection(url: String): HttpURLConnection {
        val full = resolve(url)
        return URL(full).openConnection() as HttpURLConnection
    }
}
