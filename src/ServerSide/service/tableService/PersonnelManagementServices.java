package ServerSide.service.tableService;


import org.junit.BeforeClass;
import org.junit.*;
import utils.JDBCUtilsByDruid;
import ServerSide.dao.borrowerDAO;
import ServerSide.dao.staffDAO;

import ServerSide.domain.staff;


import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Date;

import java.time.LocalDate;
import java.util.List;

/*
高级员工处理普通员工相关业务
 */
public class PersonnelManagementServices {

    private final staffDAO staffDAO = new staffDAO();
    private final borrowerDAO borrowerDAO = new borrowerDAO();

    /**
     * 添加员工
     */
    public boolean addStaff(staff staff) {
        // 校验 staff_number 是否唯一
        if (staffDAO.querySingle("SELECT * FROM staff WHERE staff_number = ?", staff.class, staff.getStaffNumber()) != null) {
            System.out.println("员工编号已存在！");
            return false;
        }

        String sql = """
                    INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        return staffDAO.insert(sql,
                staff.getStaffNumber(),
                staff.getName(),
                staff.getGender(),
                staff.getAge(),
                staff.getJoinDate(),
                staff.getPosition(),
                staff.getSalary(),
                "工作"
        ) > 0;
    }

    /**
     * 设置员工状态为离职（加事务与行锁）
     */
    public boolean logicResignStaff(String staffNumber) {
        Connection conn = null;
        try {
            conn = JDBCUtilsByDruid.getConnection();
            conn.setAutoCommit(false);

            // 1. 锁定并查询员工信息
            staff staff = staffDAO.querySingle(conn,
                    "SELECT * FROM staff WHERE staff_number = ? FOR UPDATE",
                    staff.class, staffNumber);

            if (staff == null) {
                conn.rollback();
                System.out.println("员工不存在。");
                return false;
            }

            if ("离职".equals(staff.getStatus())) {
                conn.rollback();
                System.out.println("员工已处于离职状态。");
                return false;
            }

            // 2. 更新员工状态
            int updated = staffDAO.update(conn,
                    "UPDATE staff SET status = '离职' WHERE staff_number = ?",
                    staffNumber);

            if (updated <= 0) {
                conn.rollback();
                System.out.println("员工状态更新失败。");
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            throw new RuntimeException("设置员工离职失败，事务已回滚", e);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
            JDBCUtilsByDruid.close(null, null, conn);
        }
    }


    /**
     * 修改员工信息（乐观锁）
     */
    public boolean updateStaff(staff updatedStaff) {
        Connection conn = null;
        try {
            conn = JDBCUtilsByDruid.getConnection();
            conn.setAutoCommit(false);

            // 1. 查询原始 version
            staff dbStaff = staffDAO.querySingle(conn,
                    "SELECT * FROM staff WHERE staff_number = ?",
                    staff.class, updatedStaff.getStaffNumber());

            if (dbStaff == null) {
                conn.rollback();
                System.out.println("员工不存在。");
                return false;
            }

            int originalVersion = dbStaff.getVersion();

            // 2. 乐观锁更新语句
            int affected = staffDAO.update(conn,
                    "UPDATE staff SET name = ?, gender = ?, age = ?, position = ?, salary = ?, status = ?, version = version + 1 " +
                            "WHERE staff_number = ? AND version = ?",
                    updatedStaff.getName(),
                    updatedStaff.getGender(),
                    updatedStaff.getAge(),
                    updatedStaff.getPosition(),
                    updatedStaff.getSalary(),
                    updatedStaff.getStatus(),
                    updatedStaff.getStaffNumber(),
                    originalVersion);

            if (affected == 0) {
                conn.rollback();
                System.out.println("数据已被其他人修改，请刷新后重试。");
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            throw new RuntimeException("更新员工信息失败", e);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
            JDBCUtilsByDruid.close(null, null, conn);
        }
    }

    /**
     * 查看所有员工（简单实现）
     */
    public List<staff> listAllStaff() {
        return staffDAO.queryMulti("SELECT * FROM staff", staff.class);
    }

    /**
     * 管理借阅者账户：设置为已注销、恢复正常
     */
    public boolean setBorrowerStatus(String borrowerNumber, String status) {
        if (!status.equals("正常") && !status.equals("已注销")) {
            System.out.println("非法状态更新");
            return false;
        }

        return borrowerDAO.update(
                "UPDATE borrowers SET status = ? WHERE borrower_number = ?",
                status, borrowerNumber
        ) > 0;
    }

    /**
     * 高级员工：带版本号的乐观锁更新员工信息
     */
    public boolean updateStaffInfoWithVersion(staff updated) {
        // 查询当前员工
        staff current = staffDAO.querySingle(
                "SELECT * FROM staff WHERE staff_number = ?", staff.class, updated.getStaffNumber()
        );
        if (current == null) {
            System.out.println("员工不存在！");
            return false;
        }

        // 乐观锁判断（确保 version 字段存在且相等）
        if (updated.getVersion() == null || !updated.getVersion().equals(current.getVersion())) {
            System.out.println("数据版本不一致，请刷新后再试。");
            return false;
        }

        // 更新语句，version +1
        String sql = """
        UPDATE staff
        SET name = ?, gender = ?, age = ?, join_date = ?, position = ?, salary = ?, status = ?, version = version + 1
        WHERE staff_number = ? AND version = ?
    """;

        int result = staffDAO.update(sql,
                updated.getName(),
                updated.getGender(),
                updated.getAge(),
                updated.getJoinDate(),
                updated.getPosition(),
                updated.getSalary(),
                updated.getStatus(),
                updated.getStaffNumber(),
                updated.getVersion()
        );

        return result > 0;
    }
//====================================================================

//    private static PersonnelManagementServices service;
//
//    @BeforeClass
//    public static void setup() {
//        service = new PersonnelManagementServices();
//    }
//
//    @Test
//
//    public void testAddStaff() {
//        staff staff = new staff();
//        staff.setStaffNumber("250520240066");
//        staff.setName("张四");
//        staff.setGender("男");
//        staff.setAge(30);
//        staff.setJoinDate(Date.valueOf(LocalDate.now()));
//        staff.setPosition("员工");
//        staff.setSalary(new BigDecimal("5000.00"));
//        boolean result = service.addStaff(staff);
//        Assert.assertTrue("添加员工失败", result);
//    }
//
//    @Test
//
//    public void testUpdateStaff() {
//        staff staff = new staff();
//        staff.setStaffNumber("250520240001");
//        staff.setName("张三-改名");
//        staff.setGender("男");
//        staff.setAge(31);
//        staff.setPosition("管理员");
//        staff.setSalary(new BigDecimal("8000.00"));
//        staff.setStatus("工作");
//        boolean result = service.updateStaff(staff);
//
//        Assert.assertTrue("修改员工失败", result);
//    }
//
//    @Test
//
//    public void testListAllStaff() {
//        List<staff> list = service.listAllStaff();
//        Assert.assertNotNull(list);
//        Assert.assertTrue("员工列表为空", list.size() > 0); // 注意参数顺序！
//        list.forEach(System.out::println);
//    }
//
//    @Test
//
//    public void testSetBorrowerStatus() {
//        // 假设数据库已有 borrower_number 为 "230016000016" 的借阅者
//        boolean result = service.setBorrowerStatus("230016000016", "已注销");
//        Assert.assertTrue("更新借阅者状态失败", result);
//    }
//
//    @Test
//
//    public void testDeleteStaff() {
//        // 假设先设置为离职才能删除
//        staff staff = new staff();
//        staff.setStaffNumber("250520240001");
//        staff.setName("张三");
//        staff.setGender("男");
//        staff.setAge(31);
//        staff.setPosition("管理员");
//        staff.setSalary(new BigDecimal("8000.00"));
//        service.updateStaff(staff);
//        boolean result = service.logicResignStaff("250520240001");
//        Assert.assertTrue("删除员工失败", result);
//    }


}

