//package UserSide;
//
//import ServerSide.domain.borrower;
//import ServerSide.protocol.OperationType;
//import ServerSide.protocol.Request;
//import ServerSide.protocol.Response;
//import com.google.gson.Gson;
//import org.junit.Test;
//
//import java.io.*;
//import java.math.BigInteger;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//import java.sql.Date;
//
//public class JsonSocketTest {
//    private static final String HOST = "localhost";
//    private static final int PORT = 9999;
//    private static final Gson gson = new Gson();
//
//    // 1. 注册用户
//    @Test
//    public void testRegisterUser() {
//        borrower b = new borrower();
//        b.setBorrowerNumber("202512345667");
//        b.setName("用户测试4");
//        b.setGender("女");
//        b.setAge(22);
//        b.setRegisterDate(new Date(System.currentTimeMillis()));
//        b.setStatus("正常");
//
//        Request req = new Request(OperationType.REGISTER, b);
//        sendRequestAndPrintResult(req, "📥 用户注册");
//    }
//
//    // 2. 借书
//    @Test
//    public void testBorrowBook() {
//        String[] args = {"240001000001", "TS2018HB002"}; // 替换为真实编号
//        Request req = new Request(OperationType.BORROW, args);
//        sendRequestAndPrintResult(req, "📚 借书");
//    }
//
//    // 3. 还书（请替换为真实交易号）
//    @Test
//    public void testReturnBook() {
//        BigInteger transactionNumber = new BigInteger("1748445768034");
//        Request req = new Request(OperationType.RETURN, transactionNumber);
//        sendRequestAndPrintResult(req, "🔁 还书");
//    }
//
//    // 4. 查询图书
//    @Test
//    public void testListBooks() {
//        Request req = new Request(OperationType.LIST_BOOKS, null);
//        sendRequestAndPrintResult(req, "📚 查询图书");
//    }
//
//    // 公共请求方法
//    private void sendRequestAndPrintResult(Request request, String label) {
//        try (
//                Socket socket = new Socket(HOST, PORT);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
//                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
//        ) {
//            writer.println(gson.toJson(request));
//            String responseLine = reader.readLine();
//            Response response = gson.fromJson(responseLine, Response.class);
//
//            System.out.println(label + " ✅ " + response.isSuccess() + " | 📦 " + response.getMessage());
//
//        } catch (IOException e) {
//            System.out.println(label + " ❌ 异常：" + e.getMessage());
//        }
//    }
//}
