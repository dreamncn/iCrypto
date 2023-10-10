package net.ankio.icrypto.registers

import burp.IHttpListener
import burp.IHttpRequestResponse
import burp.IInterceptedProxyMessage
import burp.IProxyListener
import net.ankio.icrypto.BurpExtender
import net.ankio.icrypto.http.Cache
import net.ankio.icrypto.http.HttpAgreementRequest
import net.ankio.icrypto.http.HttpAgreementResponse
import net.ankio.icrypto.rule.CommandType
import net.ankio.icrypto.rule.Execution
import java.io.IOException
import java.util.*


class HttpListener internal constructor() : IHttpListener, IProxyListener {
    var httpAgreementRequest: HttpAgreementRequest? = null
    var httpAgreementResponse: HttpAgreementResponse? = null
    /**
     * 检查是否需要拦截
     *
     */
    @Throws(IOException::class)
    private fun analyze(
        request: IHttpRequestResponse,
        messageIsRequest: Boolean,
        cmd: Array<String?>,
        cache: Cache?
    ): Boolean {
        if (!BurpExtender.config.auto) {
            return false
        }
        val url = request.httpService.protocol + "://" + request.httpService.host
        val rule = BurpExtender.config.find(url) ?: return false
        // 先查找脚本
        if (messageIsRequest) {
            httpAgreementRequest = HttpAgreementRequest(request.request,  cache)
        } else {
            httpAgreementRequest = HttpAgreementRequest(request.request,  cache)
            httpAgreementResponse = HttpAgreementResponse(request.response, cache)
        }

        BurpExtender.stdout.println(java.lang.String.format("脚本: %s 执行", rule.name))
        if (rule.command == "") return false
        cmd[0] = rule.command // 将 rule.command 的值放入 cmd 数组
        return true
    }


    /**
     * 当即将发出HTTP请求以及收到HTTP响应时，会调用此方法。
     *
     * @param toolFlag         指示发出请求的Burp工具的flag。
     * Burp工具 flags 定义在 `IBurpExtenderCallbacks` 接口.
     * @param messageIsRequest 是否为请求。
     * @param messageInfo      要处理的请求/回复的详细信息。 扩展可以调用此对象上的setter方法来更新当前消息，从而修改Burp的行为。
     */
    override fun processHttpMessage(toolFlag: Int, messageIsRequest: Boolean, messageInfo: IHttpRequestResponse) {
        val cmd = arrayOfNulls<String>(1)
        //使用引用传递获取需要执行的命令
        //返回值标识是否需要拦截

        try {
            val cache = Cache()
            //不需要处理直接返回
            if (!analyze(messageInfo, messageIsRequest, cmd, cache)) {
                cache.destroy()
                return
            }
            BurpExtender.stdout.println("=================================================")
            if (messageIsRequest) {
                BurpExtender.stdout.println("======> 发送给服务端")
                cmd[0]?.let { requestOut(messageInfo, cache, it) } //发送请求
            } else {
                BurpExtender.stdout.println("======> 收到服务端")
                cmd[0]?.let { responseIn(messageInfo, cache, it) } //收到响应
            }
        } catch (e: IOException) {
            e.printStackTrace()
            BurpExtender.stderr.println("错误信息：" + e.message)
        }
        BurpExtender.stdout.println("=================================================")
    }

    /**
     * 当代理处理HTTP消息时，会调用此方法。
     *
     * @param messageIsRequest 是否为请求。
     * @param message          扩展可用于查询和更新消息的详细信息，并控制消息是否应拦截并显示给用户进行手动审查或修改。
     */
    override fun processProxyMessage(messageIsRequest: Boolean, message: IInterceptedProxyMessage) {
        val cmd = arrayOfNulls<String>(1)
        //使用引用传递获取需要执行的命令
        //返回值标识是否需要拦截\
        try {
            val cache = Cache()
            if (!analyze(message.messageInfo, messageIsRequest, cmd, cache)) {
                cache.destroy()
                return
            }
            BurpExtender.stdout.println("=================================================")
            if (messageIsRequest) {
                BurpExtender.stdout.println("======> 发送给客户端")
                cmd[0]?.let { requestIn(message, cache, it) } //收到请求
            } else {
                BurpExtender.stdout.println("======> 收到客户端")
                cmd[0]?.let { responseOut(message, cache, it) } //发送响应
            }
        } catch (e: IOException) {
            e.printStackTrace()
            BurpExtender.stdout.println("错误信息：" + e.message)
        }
        BurpExtender.stdout.println("=================================================")
    }

    /**
     * requestIn阶段，收到客户端发送的加密request，进行解密并替换requestBody，使得BurpSuite中显示明文request；
     *
     */
    private fun requestIn(message: IInterceptedProxyMessage, cache:Cache, cmd: String) {
        if (Execution.run(cmd, CommandType.RequestFromClient, cache.temp)) {
            message.messageInfo.request = httpAgreementRequest?.toRequest(cache) ?: byteArrayOf()
        }
    }

    //requestOut阶段，即将发送request到服务端，读取明文的request，重新进行加密（包括签名、编码、更新时间戳等），使得服务端正常解析；
    private fun requestOut(messageInfo: IHttpRequestResponse, cache:Cache, cmd: String) {
        if (Execution.run(cmd, CommandType.RequestToServer, cache.temp)) {
            messageInfo.request = httpAgreementRequest?.toRequest(cache) ?: byteArrayOf()
        }
    }

    //responseIn阶段，收到加密response，进行解密并替换responseBody，使得BurpSuite中显示明文response；
    private fun responseIn(messageInfo: IHttpRequestResponse, cache:Cache, cmd: String) {
        if (Execution.run(cmd, CommandType.ResponseFromServer, cache.temp)) {
            messageInfo.response = httpAgreementResponse?.toResponse(cache) ?: byteArrayOf()
        }
    }

    //responseOut阶段，即将发送response到客户端，读取明文的response，重新进行加密，使得客户端正常解析。
    private fun responseOut(message: IInterceptedProxyMessage, cache:Cache, cmd: String) {
        if (Execution.run(cmd, CommandType.ResponseToClient, cache.temp)) {
            message.messageInfo.response = httpAgreementResponse?.toResponse(cache) ?: byteArrayOf()
        }
    }
}
