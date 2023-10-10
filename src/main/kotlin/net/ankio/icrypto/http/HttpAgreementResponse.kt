package net.ankio.icrypto.http

import burp.IResponseInfo
import com.google.common.base.Splitter
import com.google.common.collect.Lists
import net.ankio.icrypto.BurpExtender

class HttpAgreementResponse(responseData: ByteArray?, cache:Cache?): HttpAgreement() {
    var response_version = ""
    var state = ""
    var state_msg = ""
    var response_headers = ""
    lateinit var response_body: ByteArray

    constructor():this(null,null){

    }


    init {
        if (responseData != null) {
            val split = Splitter.on("\r\n").split(String(responseData))
            val strings = Lists.newArrayList(split)
            val path = strings[0].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() //首行是请求头
            var i: Int
            state = path[1]
            state_msg = path[2]
            response_version = path[0]
            response_headers = ""
            val s = StringBuilder()
            i = 1
            while (i < strings.size - 2) {
                s.append(strings[i]).append("\r\n")
                i++
            }
            response_headers = s.toString().trim { it <= ' ' }
            val responseInfo: IResponseInfo = BurpExtender.callbacks.helpers.analyzeResponse(responseData)
            response_body = subByte(responseData, responseInfo.bodyOffset, responseData.size - responseInfo.bodyOffset)
            if (cache != null) {
                cache.set("state", state)
                cache.set("state_msg", state_msg)
                cache.set("response_version", response_version)
                cache.set("response_headers", response_headers)
                cache.setRaw("response_body", response_body)
            }
        }
    }



    fun toResponse(): ByteArray {
        return toResponse(null)
    }

    fun toResponse(cache:Cache?): ByteArray {
        val stringBuilder = StringBuilder()
        if (cache != null) {
            state = cache.get("state")
            state_msg = cache.get("state_msg")
            response_version = cache.get("response_version")
            response_headers = cache.get("response_headers")
            response_body = cache.getRaw("response_body")
        }
        stringBuilder.append(response_version).append(" ").append(state).append(" ").append(state_msg).append("\r\n")
        stringBuilder.append(response_headers).append("\r\n")
        stringBuilder.append("\r\n")
        val bytes = stringBuilder.toString().toByteArray()
        return byteMerger(bytes, response_body)
    }

}