package net.ankio.icrypto

import Config
import burp.IBurpExtender
import burp.IBurpExtenderCallbacks
import burp.ITab
import net.ankio.icrypto.registers.HttpListener
import net.ankio.icrypto.registers.MessageEditorTabFactory
import net.ankio.icrypto.ui.MainGUI
import java.awt.Component
import java.io.IOException
import java.io.PrintWriter

class BurpExtender : IBurpExtender,ITab {
    override fun registerExtenderCallbacks(callbacks: IBurpExtenderCallbacks) {
        Companion.stdout = PrintWriter(callbacks.stdout, true)
        Companion.stderr = PrintWriter(callbacks.stderr, true)
        Companion.callbacks = callbacks
        Companion.config  = Config.load()

        callbacks.setExtensionName("$extensionName $version")


        try {
            callbacks.registerHttpListener(HttpListener())
            callbacks.registerProxyListener(HttpListener())
            callbacks.registerMessageEditorTabFactory(MessageEditorTabFactory())
            callbacks.addSuiteTab(this@BurpExtender)
            stdout.println(
                """[+] $extensionName is loaded
[+] ^_^
[+]
[+] #####################################
[+]    $extensionName v$version
[+]    author: ankio
[+]    email:  admin@ankio.net
[+]    github: https://github.com/dreamncn
[+] ####################################"""
            )
        } catch (e: IOException) {
            stdout.println("初始化异常：" + e.message)
            e.printStackTrace()
        }
    }


    companion object {

        lateinit var callbacks: IBurpExtenderCallbacks
        lateinit var config: Config
        lateinit var stdout: PrintWriter
        lateinit var stderr: PrintWriter
        const val version: String = "1.0.0"
        const val extensionName: String = "iCrypto"

    }

    override fun getTabCaption(): String {
        return extensionName
    }

    override fun getUiComponent(): Component {
        val gui = MainGUI()
        return gui.root
    }

    fun finalize(){
        Config.save(config)
    }
}