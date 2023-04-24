//package tech.chenxing.ui.pagingTable;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//
//@Slf4j
//public class DBConnFactory {
//    public static Connection getConnHy() {
//        String url = "jdbc:mysql://118.31.175.53:23687/flowmanager?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&createDatabaseIfNotExist=true&&zeroDateTimeBehavior=convertToNull";
//        try {
//            Class.forName("com.mysql.jdbc.Driver"); // 加载JDBC驱动程序
//            Connection con =
//                    DriverManager.getConnection(url, "fengqingyang", "Cx308679291"); // 建立与数据库的连接，后两个参数分别为账号和密码
//            return con;
//        } catch (Exception e) {
//            log.error("get connect error:", e);
//            return null;
//        }
//    }
//}
