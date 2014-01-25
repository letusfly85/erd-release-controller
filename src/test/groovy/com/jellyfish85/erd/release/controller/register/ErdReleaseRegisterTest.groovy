package com.jellyfish85.erd.release.controller.register

import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.MsTablesBean
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

class ErdReleaseRegisterTest extends GroovyTestCase {

    ErdReleaseRegister register = null
    String       environment    = null
    BaseContext  context        = null
    DatabaseManager manager     = null
    Connection   conn           = null

    String       schemaName     = ""
    IDatabaseConnection iConn   = null

    @BeforeClass
    void setUp() {
        environment = System.getProperty("environment")

        context     = new BaseContext(environment)
        manager     = context.manager
        conn        = context.conn

        register    = new ErdReleaseRegister(context)

        DatabaseMetaData metaData = conn.getMetaData()
        schemaName  = metaData.getUserName()

        iConn   = new DatabaseConnection(conn, schemaName)
    }

    void testGenerateTarget() {
        String url                   = "/excel/KR_TRKM_STATUS_03.xls"
        File file                    = new File(getClass().getResource(url).toURI())
        FileInputStream inputStream  = new FileInputStream(file)
        IDataSet partialDataSet      = new XlsDataSet(inputStream)
        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)
        conn.commit()

        url                   = "/excel/TP_TICKET_NUMBERS4RELEASE_01.xls"
        file                    = new File(getClass().getResource(url).toURI())
        inputStream  = new FileInputStream(file)
        partialDataSet      = new XlsDataSet(inputStream)
        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)
        conn.commit()


        url                   = "/excel/MS_TABLES_01.xls"
        file                    = new File(getClass().getResource(url).toURI())
        inputStream  = new FileInputStream(file)
        partialDataSet      = new XlsDataSet(inputStream)
        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)
        conn.commit()

        ArrayList<MsTablesBean> targets = register.generateTarget()

        assertEquals("array size should be 2", targets.size(), 2)
    }

    /*
    void testPrepareErdRelease() {

    }

    void testExecuteErdRelease() {

    }

    void testRegister() {

    }
    */
}
