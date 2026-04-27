package ServerSide.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class staff  implements Serializable {
    // 员工ID (主键，自增长)
    private Integer staffId;
    // 员工编号 (12位数字，前两位为入职年份)
    private String staffNumber;
    // 员工姓名 (20个字以内)
    private String name;
    // 性别 (枚举: 男/女)
    private String gender;
    // 年龄 (<99)
    private Integer age;
    // 入职时间
    private Date joinDate;
    // 职位 (枚举: 员工/管理员，默认为员工)
    private String position;
    // 薪资 (<1000000)
    private BigDecimal salary;
    // 状态 (枚举: 工作/离职/退休/病逝，默认为工作)
    private String status = "工作";
    // version 字段，用于乐观锁控制
    private Integer version = 0;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public staff() {
    }

    public staff(Integer staffId, String staffNumber, String name,
                 String gender, Integer age, Date joinDate,
                 String position, BigDecimal salary, String status) {
        this.staffId = staffId;
        this.staffNumber = staffNumber;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.joinDate = joinDate;
        this.position = position;
        this.salary = salary;
        this.status = status;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getPosition() {
        return position;
    }


    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "staff{" +
                "staffId=" + staffId +
                ", staffNumber='" + staffNumber + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", joinDate=" + joinDate +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", status='" + status + '\'' +
                '}';
    }
}
