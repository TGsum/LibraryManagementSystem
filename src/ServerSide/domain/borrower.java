package ServerSide.domain;
import java.io.Serializable;
import java.sql.Date;

public class borrower  implements Serializable {
    // 借阅者ID (主键，自增长)
    private Integer borrowerId;

    // 借阅者编号 (12位数字，前两位为办理年份)
    private String borrowerNumber;

    // 借阅者姓名 (20个字以内)
    private String name;

    // 性别 (枚举: 男/女)
    private String gender;

    // 年龄 (<99)
    private Integer age;

    // 注册时间
    private Date registerDate;

    // 状态 (枚举: 未注册/正常/已注销，默认为未注册)
    private String status;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public borrower() {
    }

    public borrower(Integer borrowerId, String borrowerNumber,
                    String name, String gender, Integer age,
                    Date registerDate, String status) {
        this.borrowerId = borrowerId;
        this.borrowerNumber = borrowerNumber;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.registerDate = registerDate;
        this.status = status;
    }

    public Integer getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Integer borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBorrowerNumber() {
        return borrowerNumber;
    }

    public void setBorrowerNumber(String borrowerNumber) {
        this.borrowerNumber = borrowerNumber;
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "borrower{" +
                "borrowerId=" + borrowerId +
                ", borrowerNumber='" + borrowerNumber + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", registerDate=" + registerDate +
                ", status='" + status + '\'' +
                '}';
    }
}
