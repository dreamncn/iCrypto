package net.ankio.icrypto.rule

import net.ankio.icrypto.BurpExtender
import java.io.BufferedReader
import java.io.InputStreamReader

object Execution {

    fun run(command: String, type: CommandType?, file: String?): Boolean {
        val stringBuilder = StringBuilder()
        stringBuilder.append(command).append(" ")
        when (type) {
            CommandType.RequestFromClient -> stringBuilder.append(0)
            CommandType.RequestToServer -> stringBuilder.append(1)
            CommandType.ResponseToClient -> stringBuilder.append(3)
            else -> stringBuilder.append(2)
        }
        stringBuilder.append(" ").append(file)
        val result: String = exec(stringBuilder.toString()).trim { it <= ' ' }
        return "success" == result
    }

    fun exec(command: String): String {
        try {
            BurpExtender.stdout.println("执行命令：$command")
            // 创建一个 ProcessBuilder 对象来执行系统命令
            val processBuilder = ProcessBuilder(command.split(" "))

            // 设置工作目录（可选）
            // processBuilder.directory(File("path/to/working/directory"))

            // 启动进程
            val process = processBuilder.start()

            // 读取进程的输入流
            val inputStream = process.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))

            // 读取进程的错误流（如果需要）
            val errorStream = process.errorStream
            val errorReader = BufferedReader(InputStreamReader(errorStream))

            val errOutput = StringBuilder()
            var errLine: String?
            while (errorReader.readLine().also { errLine = it } != null) {
                errOutput.append(errLine).append("\n")
            }
            errorReader.close()
            if(errOutput.isNotEmpty()){
                BurpExtender.stderr.println("执行错误：$errOutput")
            }
            // 读取输入流的内容
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            if(output.isEmpty()){
                BurpExtender.stderr.println("执行错误：输出内容为空")
            }

            // 如果需要，等待进程执行完成
            val exitCode = process.waitFor()
            BurpExtender.stdout.println("执行结束：$exitCode")

            // 关闭读取器和进程
            reader.close()
            process.destroy()

            return output.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            BurpExtender.stderr.println("执行异常：${e.message}")
            return e.message ?: ""
        }
    }
}
