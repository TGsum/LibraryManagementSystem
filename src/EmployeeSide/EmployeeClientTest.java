//package EmployeeSide;
//
//import ServerSide.domain.book;
//import ServerSide.domain.bookVersion;
//import ServerSide.domain.borrower;
//import ServerSide.domain.staff;
//import ServerSide.protocol.OperationType;
//import ServerSide.protocol.Request;
//import ServerSide.protocol.Response;
//
//import org.junit.Test;
//
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.net.Socket;
//import java.sql.Date;
//
//public class EmployeeClientTest {
//    private static final String HOST = "localhost";
//    private static final int PORT = 9999;
//
//    // 1. 普通员工操作：查询图书列表
//    @Test
//    public void testListBooks() throws Exception {
//        try (Socket socket = new Socket(HOST, PORT);
//             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//
//            Request request = new Request(OperationType.LIST_BOOKS, null);
//            oos.writeObject(request);
//            oos.flush();
//
//            Response response = (Response) ois.readObject();
//            System.out.println("✅ 查询图书: " + response.isSuccess() + " | " + response.getMessage());
//            if (response.getData() instanceof java.util.List<?>) {
//                for (Object obj : (java.util.List<?>) response.getData()) {
//                    System.out.println(obj);
//                }
//            }
//        }
//    }
//
//    // 2. 普通员工操作：帮助借阅者手动借书
//    @Test
//    public void testBorrowForUser() throws Exception {
//        String[] args = {"202500000002", "TEST-001"};
//        try (Socket socket = new Socket(HOST, PORT);
//             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//
//            Request request = new Request(OperationType.BORROW, args);
//            oos.writeObject(request);
//            oos.flush();
//
//            Response response = (Response) ois.readObject();
//            System.out.println("✅ 员工代借书: " + response.isSuccess() + " | " + response.getMessage());
//        }
//    }
//
//    // 3. 普通员工操作：帮助借阅者还书（需替换 transactionNumber）
//    @Test
//    public void testReturnForUser() throws Exception {
//        BigInteger transactionNumber = new BigInteger("1748360728692"); // 替换为有效值
//        try (Socket socket = new Socket(HOST, PORT);
//             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//
//            Request request = new Request(OperationType.RETURN, transactionNumber);
//            oos.writeObject(request);
//            oos.flush();
//
//            Response response = (Response) ois.readObject();
//            System.out.println("✅ 员工代还书: " + response.isSuccess() + " | " + response.getMessage());
//        }
//    }
//
//    // 4. 普通员工操作：添加图书及版本
//    @Test
//    public void testAddBookWithVersion() throws Exception {
//        book book = new book(null, "9999999999999", "测试图书", "Test Book");
//        bookVersion version = new bookVersion(null, null, "TEST-EMPLOYEE-001", "测试作者",
//                "第1版", new Date(System.currentTimeMillis()), "中文", 300,
//                new BigDecimal("49.99"), 5);
//
//        Object[] payload = {book, version};
//
//        try (Socket socket = new Socket(HOST, PORT);
//             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//
//            Request request = new Request(OperationType.ADD_BOOK, payload);
//            oos.writeObject(request);
//            oos.flush();
//
//            Response response = (Response) ois.readObject();
//            System.out.println("✅ 添加图书: " + response.isSuccess() + " | " + response.getMessage());
//        }
//    }
//
//    // 5. 高级员工：添加员工
//    @Test
//    public void testAddStaff() throws Exception {
//        staff s = new staff(null, "202500000999", "新员工", "女", 26,
//                new Date(System.currentTimeMillis()), "员工", new BigDecimal("5500.00"), "工作");
//
//        try (Socket socket = new Socket(HOST, PORT);
//             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//
//            Request request = new Request(OperationType.ADD_STAFF, s);
//            oos.writeObject(request);
//            oos.flush();
//
//            Response response = (Response) ois.readObject();
//            System.out.println("✅ 添加员工: " + response.isSuccess() + " | " + response.getMessage());
//        }
//    }
//
//    // 6. 高级员工：设置借阅者状态为“已注销”
//    @Test
//    public void testSetBorrowerStatus() throws Exception {
//        String[] args = {"202500000002", "已注销"};
//
//        try (Socket socket = new Socket(HOST, PORT);
//             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//
//            Request request = new Request(OperationType.SET_BORROWER_STATUS, args);
//            oos.writeObject(request);
//            oos.flush();
//
//            Response response = (Response) ois.readObject();
//            System.out.println("✅ 借阅者状态修改: " + response.isSuccess() + " | " + response.getMessage());
//        }
//    }
//}
