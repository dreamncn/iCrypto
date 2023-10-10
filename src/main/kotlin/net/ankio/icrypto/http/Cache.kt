package net.ankio.icrypto.http

import net.ankio.icrypto.BurpExtender
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * 交互
 */
class Cache {
    var temp = ""

    init {
        //创建临时文件存储数据
        val tmpCustomPrefix = Files.createTempDirectory("CustomCrypto")
        temp = tmpCustomPrefix.toString()
        //BurpExtender.stdout.println(String.format("临时会话文件夹[ %s ]创建成功", temp))
    }

    /**
     * 设置值
     * @param key String
     * @param value String
     */
    operator fun set(key: String, value: String) {
        try {
            Files.write(Paths.get("$temp/$key.txt"), value.toByteArray())
            //BurpExtender.stdout.println(String.format("写入文件（%s）数据：%s", key, value))
        } catch (e: IOException) {
            e.printStackTrace()
            BurpExtender.stderr.println(String.format("错误：%s", e.message))
            BurpExtender.stderr.println(String.format("写入文件失败：%s", "$temp/$key.txt"))
        }
    }

    /**
     * 获取值
     * @param key String
     */
    operator fun get(key: String): String {
        try {
           // BurpExtender.stdout.println("读取：$temp/$key.txt")
            return String(Files.readAllBytes(Paths.get("$temp/$key.txt"))).trim { it <= ' ' }
        } catch (e: IOException) {
            e.printStackTrace()
            BurpExtender.stderr.println(String.format("错误：%s", e.message))
            BurpExtender.stderr.println(String.format("读取文件失败：%s", "$temp/$key.txt"))
        }
        return ""
    }

    fun getRaw(key: String): ByteArray {
        try {
            BurpExtender.stdout.println("读取：$temp/$key.txt")
            return Files.readAllBytes(Paths.get("$temp/$key.txt"))
        } catch (e: IOException) {
            e.printStackTrace()
            BurpExtender.stderr.println(String.format("错误：%s", e.message))
            BurpExtender.stderr.println(String.format("读取文件失败：%s", "$temp/$key.txt"))
        }
        return byteArrayOf()
    }

    fun setRaw(key: String, value: ByteArray) {
        try {
            Files.write(Paths.get("$temp/$key.txt"), value)
        } catch (e: IOException) {
            e.printStackTrace()
            BurpExtender.stderr.println(String.format("错误：%s", e.message))
            BurpExtender.stderr.println(String.format("写入文件失败：%s", "$temp/$key.txt"))
        }
    }

    fun destroy() {
        try {
            val path: Path = Paths.get(temp)
            Files.walkFileTree(path,
                object : SimpleFileVisitor<Path?>() {
                    // 先去遍历删除文件
                    @Throws(IOException::class)
                    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        if (file != null) {
                            Files.delete(file)
                        }
                     //   System.out.printf("文件被删除 : %s%n", file)
                        return FileVisitResult.CONTINUE
                    }

                    // 再去遍历删除目录
                    @Throws(IOException::class)
                    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                        if (dir != null) {
                            Files.delete(dir)
                        }
                     ///   System.out.printf("文件夹被删除: %s%n", dir)
                        return FileVisitResult.CONTINUE
                    }
                }
            )
          //  BurpExtender.stdout.println(String.format("临时文件夹[ %s ]已删除", temp))
        } catch (ignored: IOException) {
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        destroy()
    }


}
