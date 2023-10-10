var v = "257624b6edf725c09eb79e2d51705f67", g = "7a2d05c2b810c55555e817d0f9a0a1b5";
const RequestFromClient = "0";// 日志/Interrupt收到请求（请求包解密）
const RequestToServer = "1";// Repeater/Interrupt发出请求（请求包加密）
const ResponseFromServer = "2";// 日志/Repeater/Interrupt收到响应（响应包解密）
const ResponseToClient = "3";// Repeater/Interrupt发出响应（响应包加密）

//从命令行获取参数
var args = process.argv.slice(2);
if(args.length!==2){
    throw "错误，至少有两个参数！"
}

//数据
var path = args[1];
const fs = require('fs');
const {sm4} = require("sm-crypto");

//获取
function getRequestBody() {
    return fs.readFileSync(path + '/body.txt').toString();
}
//设置
function setRequestBody(data) {
    fs.writeFileSync(path + '/body.txt',data);
}

function getResponseBody() {
    return fs.readFileSync(path + '/response_body.txt').toString();
}
//设置
function setResponseBody(data) {
    fs.writeFileSync(path + '/response_body.txt',data);
}

function encrypt(msg) {
    const sm4 = require('sm-crypto').sm4

    return  sm4.encrypt(msg, v,{
        mode: "cbc",
        iv: g
    }) // 加密，默认输出 16 进制字符串，默认使用 pkcs#7 填充（传 pkcs#5 也会走 pkcs#7 填充）

}

function decrypt(msg) {
    const sm4 = require('sm-crypto').sm4

    return  sm4.decrypt(msg, v,{
        mode: "cbc",
        iv: g
    }) // 加密，默认输出 16 进制字符串，默认使用 pkcs#7 填充（传 pkcs#5 也会走 pkcs#7 填充）

}



if(args[0]===RequestFromClient){
   setRequestBody(decrypt(getRequestBody()));
}

if(args[0]===RequestToServer){
    setRequestBody(encrypt(getRequestBody()));
}

if(args[0]===ResponseFromServer){
    let body = JSON.parse(getResponseBody());
    body.data = JSON.parse(decrypt(body.data));
    setResponseBody(JSON.stringify(body));
}

if(args[0]===ResponseToClient){
    let body = JSON.parse(getResponseBody());
    body.data = encrypt(JSON.stringify(body.data));
    setResponseBody(JSON.stringify(body));
}

//最后一定要输出 success
console.log("success")
