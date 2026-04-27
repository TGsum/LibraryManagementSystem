// node-bridge/index.js
const express = require('express');
const bodyParser = require('body-parser');
const net = require('net');
const cors = require('cors');

const app = express();
const PORT = 3000; // 提供给前端访问的 HTTP 端口
const JAVA_HOST = 'localhost';
const JAVA_PORT = 9999; // Java Socket 监听端口

app.use(bodyParser.json());
app.use(cors()); // ✅ 允许所有来源请求

// 🌟 核心函数：将 JSON 转发给 Java Socket
function sendToJavaSocket(jsonData) {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();
        let buffer = '';

        client.connect(JAVA_PORT, JAVA_HOST, () => {
            client.write(JSON.stringify(jsonData));
        });

        client.on('data', (data) => {
            buffer += data.toString();
        });

        client.on('end', () => {
            try {
                const response = JSON.parse(buffer);
                resolve(response);
            } catch (e) {
                reject(new Error("Java 返回的数据格式有误"));
            }
        });

        client.on('error', (err) => {
            reject(err);
        });
    });
}

// 🌐 提供一个通用接口，前端调用 /api/socket 发送 { operationType, payload }
app.post('/api/socket', async (req, res) => {
    try {
        const requestJson = req.body;
        const result = await sendToJavaSocket(requestJson);
        res.json(result); // 返回给前端
    } catch (err) {
        res.status(500).json({ success: false, message: '服务器错误', error: err.message });
    }
});

// 启动 HTTP 服务
app.listen(PORT, () => {
    console.log(`✅ Node 网关已启动: http://localhost:${PORT}`);
});
