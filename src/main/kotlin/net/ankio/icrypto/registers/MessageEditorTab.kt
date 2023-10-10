package net.ankio.icrypto.registers

import burp.IMessageEditorController
import burp.IMessageEditorTab
import burp.ITextEditor
import net.ankio.icrypto.BurpExtender
import net.ankio.icrypto.http.Cache
import net.ankio.icrypto.http.HttpAgreementRequest
import net.ankio.icrypto.http.HttpAgreementResponse
import net.ankio.icrypto.rule.CommandType
import net.ankio.icrypto.rule.Execution
import net.ankio.icrypto.rule.Rule
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.event.ListDataListener

class MessageEditorTab internal constructor(iMessageEditorController: IMessageEditorController?, b: Boolean) :
    IMessageEditorTab {
    private var messageEditor: ITextEditor
    private var isRequest = false
    private var iMessageEditorController: IMessageEditorController? = null
    var httpAgreementRequest: HttpAgreementRequest? = null
    var httpAgreementResponse: HttpAgreementResponse? = null
    private fun selectItem(e: ActionEvent) {
        val rule: Rule = (selectBox.selectedItem as Rule)
        if (rule.command == "") {
            return
        }
        val cache = Cache()
        httpAgreementRequest = HttpAgreementRequest(iMessageEditorController?.request, cache)
        httpAgreementResponse = HttpAgreementResponse(iMessageEditorController?.response, cache)

        val commandType = when {
            rule.name.contains("（收到请求/响应）") -> {
                if (isRequest) CommandType.RequestFromClient else CommandType.ResponseFromServer
            }
            else -> {
                if (isRequest) CommandType.RequestToServer else CommandType.ResponseToClient
            }
        }

        if (Execution.run(rule.command, commandType, cache.temp)) {
            val message = if (isRequest) {
                httpAgreementRequest!!.toRequest(cache)
            } else {
                httpAgreementResponse!!.toResponse(cache)
            }
            setMessage(message, isRequest)
        } else {
            setMessage("加解密失败，详情请看日志".toByteArray(), isRequest)
        }
    }

    private val rootPanel: JSplitPane
    private val selectBox: JComboBox<Rule>

    init {
        this.iMessageEditorController = iMessageEditorController
        messageEditor = BurpExtender.callbacks.createTextEditor()
        messageEditor.setEditable(b)
        val EditorComponent: Component = messageEditor.component
        rootPanel = JSplitPane()
        val panel1 = JPanel()
        val label1 = JLabel()
        selectBox = JComboBox<Rule>()

        //======== splitPane1 ========
        rootPanel.setOrientation(JSplitPane.VERTICAL_SPLIT)
        //======== panel1 ========
        panel1.preferredSize = Dimension(0, 30)
        panel1.maximumSize = Dimension(2147483647, 30)
        panel1.setLayout(BorderLayout())
        //---- label1 ----
        label1.setText("\u8bf7\u9009\u62e9\u811a\u672c  ")
        panel1.add(label1, BorderLayout.WEST)

        //---- comboBox1 ----
        selectBox.addActionListener(ActionListener { e: ActionEvent -> selectItem(e) })
        selectBox.preferredSize = Dimension(0, 30)
        panel1.add(selectBox, BorderLayout.CENTER)
        class Model : ComboBoxModel<Rule?> {
            private var arrayList: ArrayList<Rule> = ArrayList()
            private var rule: Rule? = null
            init {
                arrayList = ArrayList()
                for (item in BurpExtender.config.list) {
                    val newItem1 = item.copy() // 创建第一个副本
                    newItem1.name += "（收到请求/响应）"
                    arrayList.add(newItem1)

                    val newItem2 = item.copy() // 创建第二个副本
                    newItem2.name += "（发出请求/响应）"
                    arrayList.add(newItem2)
                }
            }

            override fun getSize(): Int {
                return arrayList.size
            }

            override fun getElementAt(index: Int): Rule {
                return arrayList[index]
            }

            override fun addListDataListener(l: ListDataListener) {}
            override fun removeListDataListener(l: ListDataListener) {}
            override fun setSelectedItem(anItem: Any?) {
                rule = anItem as Rule
            }

            override fun getSelectedItem(): Rule? {
               return rule
            }
        }
        selectBox.setModel(Model())
        rootPanel.topComponent = panel1
        rootPanel.bottomComponent = EditorComponent
    }


    override fun getTabCaption(): String {
        return BurpExtender.extensionName
    }

    override fun getUiComponent(): Component {
        return rootPanel
    }

    override fun isEnabled(bytes: ByteArray, b: Boolean): Boolean {
        return true
    }

    override fun setMessage(content: ByteArray, isRequest: Boolean) {
        this.isRequest = isRequest
        messageEditor.text = content
    }

    override fun getMessage(): ByteArray {
       return messageEditor.text
    }

    override fun isModified(): Boolean {
        return messageEditor.isTextModified
    }

    override fun getSelectedData(): ByteArray {
        return messageEditor.selectedText
    }

}
