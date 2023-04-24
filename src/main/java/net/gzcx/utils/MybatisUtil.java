package net.gzcx.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Slf4j
public class MybatisUtil {
    private static SqlSession sqlSession = null;

    /** 是否需要初始化 */
    private static boolean needInit = false;

    private static File dbFile = new File(SystemUtil.configHome + File.separator + "RgTool.db");

    private MybatisUtil() {}

    public static SqlSession getSqlSession() {
        if (sqlSession == null) {
            try {
                if (!dbFile.exists()) {
                    initDbFile();
                }
                if (StringUtil.isNotEmpty(SystemUtil.configHome)) {
                    dbFile = new File(SystemUtil.configHome + File.separator + "RgTool.db");
                }
                String resource = "mybatis-config.xml";
                InputStream inputStream = Resources.getResourceAsStream(resource);
                Properties properties = new Properties();
                properties.setProperty("url", "jdbc:sqlite:" + dbFile.getAbsolutePath());
                SqlSessionFactory sqlSessionFactory =
                        new SqlSessionFactoryBuilder().build(inputStream, properties);
                sqlSession = sqlSessionFactory.openSession(true);
                inputStream.close();

                initTables();
            } catch (Exception e) {
                log.error("get sqlSession error!", e);
            }
        }
        return sqlSession;
    }

    public static void setSqlSession(SqlSession sqlSession) {
        MybatisUtil.sqlSession = sqlSession;
    }

    /** 初始化数据库文件 */
    public static void initDbFile() throws SQLException {
        File configHomeDir = new File(SystemUtil.configHome);
        if (!configHomeDir.exists()) {
            configHomeDir.mkdirs();
        }
        // 不存在db文件时会自动创建一个
        String sql =
                FileUtil.readString(
                        MybatisUtil.class.getResource("/db_init.sql"), CharsetUtil.UTF_8);
        executeSql(sql);
        needInit = true;
    }

    /** 初始化数据库表 */
    private static void initTables() {
        if (needInit) {
            // doesn't work
            //            InitMapper initMapper = sqlSession.getMapper(InitMapper.class);
            //            initMapper.createAllTables();
        }
    }

    /**
     * 执行sql
     *
     * @param sql
     */
    public static void executeSql(String sql) throws SQLException {
        Connection connection =
                DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        connection.close();
    }
}
