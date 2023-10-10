import net.ankio.icrypto.ui.MainGUI

import javax.swing.SwingUtilities




fun main(){

    SwingUtilities.invokeLater {
        val gui = MainGUI()
        gui.root.isVisible = true
    }
}