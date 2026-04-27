//package ServerSide.service.tableService;
//
//import org.junit.Test;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
////import java.util.concurrent.TimeUnit;
//
//public class testConcurrency {
//@Test
//        public void TestConcurrency() throws InterruptedException {
//        BorrowingReturningService service = new BorrowingReturningService();
//
//        String borrowerNumber = "202500000101"; // 请确保此借阅者存在并状态正常
//        String bookCode = "TS2010PB001"; // 请确保此书版本存在且库存足够（≥10）
//
//        int threadCount = 10;
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            final int user = i + 1;
//            executor.submit(() -> {
//                boolean success = service.borrowBook(borrowerNumber, bookCode);
//                System.out.println("线程 " + user + " 借书结果：" + (success ? "✅成功" : "❌失败"));
//            });
//        }
//
//        executor.shutdown();
////        executor.awaitTermination(30, TimeUnit.SECONDS);
//
//        System.out.println("✅ 并发借书测试完成。");
//}
//}
