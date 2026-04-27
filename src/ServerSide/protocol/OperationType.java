package ServerSide.protocol;

import java.io.Serializable;



public enum OperationType implements Serializable {
    REGISTER,               // 用户注册（借阅者注册）
    BORROWER_LOGIN,         //用户登录
    BORROW,                 // 用户借书
    RETURN,                 // 用户还书
    ADD_BOOK,               // 管理员添加新图书及版本信息
    DELETE_BOOK_VERSION,    // 管理员删除图书版本（需未被借出）
    UPDATE_BOOK_VERSION,    // 管理员修改图书版本信息（价格、库存等）
    LIST_BOOKS,             // 用户/员工 查询所有图书及版本
    ADD_STAFF,              // 高级员工添加新员工账号
    RESIGN_STAFF,           // 高级员工将员工设为离职
    UPDATE_STAFF,           // 员工修改个人或他人信息（带乐观锁 version）
    LIST_STAFF,             // 高级员工查看所有员工列表
    SET_BORROWER_STATUS,     // 高级员工冻结/恢复借阅者账户
    STAFF_LOGIN,             //员工登录
    VIEW_BORROW_RECORDS,      //查询自己的借阅记录
}


