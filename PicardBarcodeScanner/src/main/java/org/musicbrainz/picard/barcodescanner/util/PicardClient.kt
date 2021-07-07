/*
 * Copyright (C) 2012 Philipp Wolfer <ph.wolfer@googlemail.com>
 * 
 * This file is part of MusicBrainz Picard Barcode Scanner.
 * 
 * MusicBrainz Picard Barcode Scanner is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * MusicBrainz Picard Barcode Scanner is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MusicBrainz Picard Barcode Scanner. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.musicbrainz.picard.barcodescanner.util

import android.util.Log
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.AbstractHttpClient
import org.apache.http.impl.client.DefaultHttpClient
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class PicardClient(private val mIpAddress: String, private val mPort: Int) {
    private val mHttpClient: AbstractHttpClient

    @Throws(IOException::class)
    fun openRelease(releaseId: String): Boolean {
        val url = String.format(
            PICARD_OPENALBUM_URL, mIpAddress,
            mPort, uriEncode(releaseId)
        )
        val response = get(url)
        return isResponseSuccess(response)
    }

    @Throws(IOException::class)
    private operator fun get(url: String): HttpResponse {
        val get = HttpGet(url)
        return mHttpClient.execute(get)
    }

    private fun isResponseSuccess(response: HttpResponse): Boolean {
        return response.statusLine.statusCode < 400
    }

    private fun uriEncode(releaseId: String): String {
        return try {
            URLEncoder.encode(releaseId, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.e(this.javaClass.name, e.message, e)
            URLEncoder.encode(releaseId)
        }
    }

    companion object {
        private const val PICARD_OPENALBUM_URL = "http://%s:%d/openalbum?id=%s"
    }

    init {
        mHttpClient = DefaultHttpClient()
    }
}