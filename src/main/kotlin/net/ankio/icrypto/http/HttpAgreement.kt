package net.ankio.icrypto.http

abstract class HttpAgreement {
    fun subByte(b: ByteArray?, off: Int, length: Int): ByteArray {
        val b1 = ByteArray(length)
        if (b != null) {
            System.arraycopy(b, off, b1, 0, length)
        }
        return b1
    }

    fun byteMerger(bt1: ByteArray, bt2: ByteArray): ByteArray {
        val bt3 = ByteArray(bt1.size + bt2.size)
        System.arraycopy(bt1, 0, bt3, 0, bt1.size)
        System.arraycopy(bt2, 0, bt3, bt1.size, bt2.size)
        return bt3
    }
}