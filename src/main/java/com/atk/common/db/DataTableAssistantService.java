package com.atk.common.db;

import com.zhiliao.common.db.impl.M;
import com.zhiliao.common.db.impl.MysqlDbTableAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.zhiliao.common.db.impl.M.BIG_INT_TYPE;

@Service
@Scope("prototype")
public class DataTableAssistantService {
    private final static Logger log = LoggerFactory.getLogger(DataTableAssistantService.class);

    private final String PRIMARY_KEY = "data_id";
    private final String TABLE_PREFIX = "t_cms_data_";

    @Autowired
    private MysqlDbTableAssistant mysql;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource dataSource;


    public void createDbtable(String tableName) throws SQLException {
        String sql = mysql.create().TableName(TABLE_PREFIX+tableName).InitColumn(PRIMARY_KEY, BIG_INT_TYPE,20,false,"0",true,true).BuilderSQL();
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
        connection.close();
    }

    public void deleteDbtable(String tableName) throws SQLException {
        log.info("create table  [{}] begin",tableName);

        String sql = mysql.delete().TableName(TABLE_PREFIX+tableName).BuilderSQL();
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
        connection.close();
    }

    public void addDbTableColumn(String tableName, String columnName, M columnType, Integer length, boolean autoIncrement, String defaultValue, boolean isNotNull, boolean isPrimaryKey) throws SQLException {
        log.info("create table [{}] column [{}] begin",tableName,columnName);
        String sql = mysql.edit().TableName(TABLE_PREFIX+tableName).AddColumn(columnName,columnType,length,autoIncrement,defaultValue,isNotNull,isPrimaryKey).BuilderSQL();
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
        connection.close();
    }


    public void editDbTableColumn(String tableName, String columnName,String newColumnName, M columnType, Integer length, boolean autoIncrement, String defaultValue, boolean isNotNull) throws SQLException {
        log.info("create table [{}] column [{}] begin",tableName,columnName);
        String sql = mysql.edit().TableName(TABLE_PREFIX+tableName).ChangeColumn(columnName,newColumnName,columnType,length,autoIncrement,defaultValue,isNotNull).BuilderSQL();
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
        connection.close();
    }

    public void deleteDbTableColumn(String tableName, String columnName, boolean isPrimaryKey) throws SQLException {
        log.info("create table [{}]  column [{}] begin",tableName,columnName);

        String sql = mysql.edit().TableName(TABLE_PREFIX+tableName).DropColumn(columnName,isPrimaryKey).BuilderSQL();
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
        connection.close();
        log.info("created table [{}] column [{}] success",tableName,columnName);
    }
}
