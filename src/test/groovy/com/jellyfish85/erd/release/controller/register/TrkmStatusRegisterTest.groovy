package com.jellyfish85.erd.release.controller.register

import com.jellyfish85.dbaccessor.bean.erd.release.controller.KrTrkmStatusBean
import com.jellyfish85.dbaccessor.dao.erd.release.controller.KrTrkmStatusDao
import com.jellyfish85.dbaccessor.manager.DatabaseManager
import com.jellyfish85.erd.release.controller.BaseContext
import org.dbunit.operation.DatabaseOperation
import org.junit.AfterClass
import org.junit.BeforeClass

import java.sql.Connection
import java.sql.DatabaseMetaData

import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.excel.XlsDataSet

import java.sql.SQLException

class TrkmStatusRegisterTest extends GroovyTestCase {

    String       environment    = null
    BaseContext  context        = null
    DatabaseManager manager     = null
    Connection   conn           = null
    KrTrkmStatusDao dao         = null

    String       schemaName     = ""
    IDatabaseConnection iConn   = null

    TrkmStatusRegister register = null

    @BeforeClass
    void setUp() {
        environment = System.getProperty("environment")
        dao         = new KrTrkmStatusDao()

        context     = new BaseContext(environment)
        manager     = context.manager
        conn        = context.conn

        register    = new TrkmStatusRegister(context)

        DatabaseMetaData metaData = conn.getMetaData()
        schemaName  = metaData.getUserName()

        iConn   = new DatabaseConnection(conn, schemaName)
    }

    void testSchema() {
        assertEquals("schemaName should be test ",
                context.erdProp.erdSchemaNameAdminUnitTest(), schemaName)
    }

    void testRegister01() {
        String url                   = "/excel/KR_TRKM_STATUS_01.xls"
        File file                    = new File(getClass().getResource(url).toURI())
        FileInputStream inputStream  = new FileInputStream(file)
        IDataSet partialDataSet      = new XlsDataSet(inputStream)

        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)

        register.register()

        KrTrkmStatusBean bean = dao.findCurrent(conn)

        assertEquals("trkm id should be updated", new BigDecimal(2843), bean.trkmIdAttr().value())
    }

    void testRegister02() {
        String url                   = "/excel/KR_TRKM_STATUS_02.xls"
        File file                    = new File(getClass().getResource(url).toURI())
        FileInputStream inputStream  = new FileInputStream(file)
        IDataSet partialDataSet      = new XlsDataSet(inputStream)

        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)

        register.register()

        KrTrkmStatusBean bean = dao.findCurrent(conn)

        assertEquals("trkm id should not be updated", new BigDecimal(2842), bean.trkmIdAttr().value())
    }

    void testRegisterErr01() {
        String url                   = "/excel/KR_TRKM_STATUS_ERR_01.xls"
        File file                    = new File(getClass().getResource(url).toURI())
        FileInputStream inputStream  = new FileInputStream(file)
        IDataSet partialDataSet      = new XlsDataSet(inputStream)

        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)

        shouldFail(RuntimeException) {register.register()}
    }
}