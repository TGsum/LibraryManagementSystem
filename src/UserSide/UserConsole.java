//package UserSide;
//
//import ServerSide.domain.borrowRecord;
//import ServerSide.domain.borrower;
//import ServerSide.protocol.OperationType;
//import ServerSide.protocol.Request;
//import ServerSide.protocol.Response;
//import com.google.gson.*;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.*;
//import java.lang.reflect.Type;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public class UserConsole {
//    private static final String HOST = "localhost";
//    private static final int PORT = 9999;
//    private static final Scanner scanner = new Scanner(System.in);
//    private static final Gson gson = new GsonBuilder()
//            .registerTypeAdapter(BigInteger.class, new JsonDeserializer<BigInteger>() {
//                @Override
//                public BigInteger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
//                    try {
//                        // 如果是数字，先用 BigDecimal 再转为 BigInteger（避免精度丢失）
//                        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
//                            return new BigDecimal(json.getAsString()).toBigInteger();
//                        }
//                        // 如果是字符串，尝试直接转 BigInteger
//                        return new BigInteger(json.getAsString());
//                    } catch (Exception e) {
//                        throw new JsonParseException("无法解析 BigInteger：" + json.toString(), e);
//                    }
//                }
//            })
//            .registerTypeAdapter(BigInteger.class, new JsonSerializer<BigInteger>() {
//                @Override
//                public JsonElement serialize(BigInteger src, Type typeOfSrc, JsonSerializationContext context) {
//                    return new JsonPrimitive(src.toString());
//                }
//            })
//            .setDateFormat("yyyy-MM-dd")
//            .create();
//
//
//    private static borrower currentUser;
//
//    public static void main(String[] args) {
//        while (true) {
//            System.out.println("==== 📚 图书管理系统（借阅者） ====");
//            System.out.println("1. 注册");
//            System.out.println("2. 登录");
//            System.out.println("0. 退出");
//            System.out.print("请选择操作：");
//            String choice = scanner.nextLine();
//
//            switch (choice) {
//                case "1" -> register();
//                case "2" -> login();
//                case "0" -> {
//                    System.out.println("再见！");
//                    return;
//                }
//                default -> System.out.println("无效选择，请重试。");
//            }
//        }
//    }
//
//    private static void register() {
//        System.out.print("借阅者编号（12位）：");
//        String number = scanner.nextLine();
//        System.out.print("姓名：");
//        String name = scanner.nextLine();
//        System.out.print("性别（男/女）：");
//        String gender = scanner.nextLine();
//        System.out.print("年龄：");
//        int age = Integer.parseInt(scanner.nextLine());
//        System.out.print("密码：");
//        String password = scanner.nextLine();
//
//        borrower b = new borrower();
//        b.setBorrowerNumber(number);
//        b.setName(name);
//        b.setGender(gender);
//        b.setAge(age);
//        b.setRegisterDate(new java.sql.Date(System.currentTimeMillis()));
//        b.setPassword(password);
//        b.setStatus("正常");
//
//        Response r = sendRequest(new Request(OperationType.REGISTER, b));
//        System.out.println(r.getMessage());
//    }
//
//    private static void login() {
//        System.out.print("借阅者编号：");
//        String number = scanner.nextLine();
//        System.out.print("密码：");
//        String password = scanner.nextLine();
//
//        List<String> loginPayload = List.of(number, password);
//        Response r = sendRequest(new Request(OperationType.BORROWER_LOGIN, loginPayload));
//        if (r.isSuccess()) {
//            currentUser = gson.fromJson(gson.toJson(r.getData()), borrower.class);
//            System.out.println("🎉 登录成功，欢迎 " + currentUser.getName() + "！");
//            userMenu();
//        } else {
//            System.out.println("❌ 登录失败：" + r.getMessage());
//        }
//    }
//
//    private static void userMenu() {
//        while (true) {
//            System.out.println("\n==== 📘 借阅者功能区 ====");
//            System.out.println("1. 查看图书");
//            System.out.println("2. 借书");
//            System.out.println("3. 还书");
//            System.out.println("4. 查看我的借阅记录");
//            System.out.println("0. 退出登录");
//            System.out.print("请选择操作：");
//
//            String choice = scanner.nextLine();
//            switch (choice) {
//                case "1" -> listBooks();
//                case "2" -> borrowBook();
//                case "3" -> returnBook();
//                case "4" -> viewRecords();
//                case "0" -> {
//                    currentUser = null;
//                    return;
//                }
//                default -> System.out.println("无效选择");
//            }
//        }
//    }
//
//    private static void listBooks() {
//        Response r = sendRequest(new Request(OperationType.LIST_BOOKS, null));
//        System.out.println("📖 图书列表：\n" + gson.toJson(r.getData()));
//    }
//
//    private static void borrowBook() {
//        System.out.print("请输入图书编码（bookCode）：");
//        String bookCode = scanner.nextLine();
//        List<String> args = List.of(currentUser.getBorrowerNumber(), bookCode);
//        Response r = sendRequest(new Request(OperationType.BORROW, args));
//        System.out.println(r.getMessage());
//    }
//
//    private static void returnBook() {
//        System.out.print("请输入借阅编码（transactionNumber）：");
//        String number = scanner.nextLine();
//        Response r = sendRequest(new Request(OperationType.RETURN, number));
//        System.out.println(r.getMessage());
//    }
//
//    private static void viewRecords() {
//        Response r = sendRequest(new Request(OperationType.VIEW_BORROW_RECORDS, currentUser.getBorrowerNumber()));
//        if (r.isSuccess()) {
//            List<borrowRecord> records = gson.fromJson(gson.toJson(r.getData()), new TypeToken<List<borrowRecord>>() {}.getType());
//            System.out.println("📑 借阅记录：");
//            for (borrowRecord record : records) {
//                System.out.println("- " + record.getTransactionNumber() + " | " + record.getBookTitle() + " | " + record.getStatus());
//            }
//        } else {
//            System.out.println("❌ 查询失败：" + r.getMessage());
//        }
//    }
//
//    private static Response sendRequest(Request request) {
//        try (Socket socket = new Socket(HOST, PORT);
//             OutputStream out = socket.getOutputStream();
//             InputStream in = socket.getInputStream()) {
//
//            String json = gson.toJson(request);
//            out.write(json.getBytes(StandardCharsets.UTF_8));
//            out.flush();
//
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            byte[] data = new byte[1024];
//            int len;
//            while ((len = in.read(data)) != -1) {
//                buffer.write(data, 0, len);
//                if (len < 1024) break;
//            }
//
//            String responseJson = buffer.toString(StandardCharsets.UTF_8);
//            return gson.fromJson(responseJson, Response.class);
//        } catch (IOException e) {
//            System.out.println("📛 网络异常: " + e.getMessage());
//            return new Response(false, "客户端异常", null);
//        }
//    }
//}
