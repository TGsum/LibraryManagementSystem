package ServerSide.service.networkCommunicationService;

import ServerSide.protocol.Request;
import ServerSide.protocol.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 9999;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd") // ✅ 告诉 Gson 如何解析日期
            .create();

    public static void main(String[] args) {
        System.out.println("📡 JSON服务端启动，监听端口 " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(() -> handleClient(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            // 🌟 改成逐字节读取直到结束
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len;
            while ((len = in.read(data)) != -1) {
                buffer.write(data, 0, len);
                if (len < 1024) break; // 简单结束判断
            }

            String json = buffer.toString(StandardCharsets.UTF_8);
            System.out.println("📨 收到请求：" + json);

            Request request = gson.fromJson(json, Request.class);
            Response response = RequestDispatcher.dispatch(request);

            String responseJson = gson.toJson(response);
            out.write(responseJson.getBytes(StandardCharsets.UTF_8));
            out.flush();

            System.out.println("📤 返回响应：" + responseJson);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("🔌 客户端连接关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
