package com.example.vino.ui.home

import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MapTileProvider(private val type: String) {

    fun getProvider(): TileProvider {
        return object : UrlTileProvider(256, 256) {
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                /* Define the URL pattern for the tile images */
                val url = String.format(Locale.US, "https://tile.openweathermap.org/map/%s_new/%d/%d/%d.png?appid=ee79ad0d5b1a83ff07fce20435019619", type, zoom, x, y)
                try {
                    return URL(url)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
            }
        }
    }
}