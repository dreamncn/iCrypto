package net.ankio.icrypto.registers

import burp.IMessageEditorController
import burp.IMessageEditorTab
import burp.IMessageEditorTabFactory

class MessageEditorTabFactory : IMessageEditorTabFactory {
    override fun createNewInstance(iMessageEditorController: IMessageEditorController, b: Boolean): IMessageEditorTab {
        return MessageEditorTab(iMessageEditorController, b)
    }

}
