package net.ankio.icrypto.http

import burp.IRequestInfo
import com.google.common.base.Splitter
import com.google.common.collect.Lists
import net.ankio.icrypto.BurpExtender
import java.io.IOException

class HttpAgreementRequest(requestData: ByteArray?,cache:Cache?) : HttpAgreement() {
    var method = ""
    var path = ""
    var version = ""
    var headers = ""
    var body: ByteArray = byteArrayOf()
    constructor() : this(null,null) {

    }

    init {
        if (requestData != null) {
            val requestInfo: IRequestInfo = BurpExtender.callbacks.helpers.analyzeRequest(requestData)
            val split: Iterable<String> = Splitter.on("\r\n").split(String(requestData))
            val strings: ArrayList<String> = Lists.newArrayList(split)
            val path = strings[0].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() //首行是请求头
            method = path[0]
            this.path = path[1]
            version = path[2]
            headers = ""
            val s = StringBuilder()
            var i: Int = 1
            while (i < strings.size - 2) {
                s.append(strings[i]).append("\r\n")
                i++
            }
            headers = s.toString().trim { it <= ' ' }
            body = subByte(requestData, requestInfo.bodyOffset, requestData.size - requestInfo.bodyOffset)
            if (cache != null) {
                cache.set("method", method)
                cache.set("path", this.path)
                cache.set("version", version)
                cache.set("headers", headers)
                cache.setRaw("body", body)
            }
        }
    }

    fun toRequest(): ByteArray {
        return toRequest(null)
    }


    fun toRequest(cache:Cache?): ByteArray {
        val stringBuilder = StringBuilder()
        var length = 0
        if (cache != null) {
            method = cache.get("method")
            path = cache.get("path")
            version = cache.get("version")
            headers = cache.get("headers")
            body = cache.getRaw("body")
            length = body.size
        }

        headers = headers.replace("Content-Length: \\d+".toRegex(), "Content-Length: $length")
        //更新headers
        stringBuilder.append(method).append(" ").append(path).append(" ").append(version).append("\r\n")
        stringBuilder.append(headers).append("\r\n")
        stringBuilder.append("\r\n")
        val bytes = stringBuilder.toString().toByteArray()
        return byteMerger(bytes, body)
    }
}