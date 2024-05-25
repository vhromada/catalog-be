package com.github.vhromada.catalog.common.log

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.NotImplementedException
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * A class represents request for multi reading.
 *
 * @author Vladimir Hromada
 */
class MultiReadHttpServletRequest(

    /**
     * Request
     */
    request: HttpServletRequest?

) : HttpServletRequestWrapper(request) {

    /**
     * Body
     */
    private var body: ByteArray? = null

    override fun getInputStream(): ServletInputStream {
        return BufferedServletInputStream(ByteArrayInputStream(getContentAsByteArray()))
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    /**
     * Returns content of body as byte array.
     *
     * @return content of body as byte array
     */
    fun getContentAsByteArray(): ByteArray {
        if (body != null) {
            return body!!.copyOf(body!!.size)
        }
        body = IOUtils.toByteArray(request.reader, request.characterEncoding)
        return body!!
    }

    /**
     * A class represents buffered servlet input stream.
     */
    internal class BufferedServletInputStream(

        /**
         * Buffer
         */
        private val buffer: ByteArrayInputStream

    ) : ServletInputStream() {

        override fun read(): Int {
            return buffer.read()
        }

        override fun isFinished(): Boolean {
            return buffer.available() == 0
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setReadListener(listener: ReadListener) {
            throw NotImplementedException("BufferedServletInputStream#setReadListener not implemented")
        }

    }

}
