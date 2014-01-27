package com.jellyfish85.erd.release.controller.register

import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.MsTablesBean
import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.RrErdReleasesBean
import com.jellyfish85.dbaccessor.bean.erd.release.controller.TpTicketNumbers4releaseBean
import com.jellyfish85.dbaccessor.dao.erd.mainte.tool.RrErdReleasesDao
import com.jellyfish85.dbaccessor.dao.erd.release.controller.TpTicketNumbers4releaseDao
import com.jellyfish85.dbaccessor.manager.DatabaseManager
import com.jellyfish85.erd.release.controller.BaseContext
import org.dbunit.operation.DatabaseOperation
import org.junit.AfterClass
import org.junit.Before
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

    void setUp() {
        environment = System.getProperty("environment")

        context     = new BaseContext(environment)
        manager     = context.manager
        conn        = context.conn

        register    = new ErdReleaseRegister(context)

        DatabaseMetaData metaData = conn.getMetaData()
        schemaName  = metaData.getUserName()

        iConn   = new DatabaseConnection(conn, schemaName)

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

        url                   = "/excel/MS_ERD_RELEASES_01.xls"
        file                    = new File(getClass().getResource(url).toURI())
        inputStream  = new FileInputStream(file)
        partialDataSet      = new XlsDataSet(inputStream)
        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)
        conn.commit()

        url                   = "/excel/RR_ERD_RELEASES_01.xls"
        file                    = new File(getClass().getResource(url).toURI())
        inputStream  = new FileInputStream(file)
        partialDataSet      = new XlsDataSet(inputStream)
        DatabaseOperation.CLEAN_INSERT.execute(iConn, partialDataSet)
        conn.commit()
    }

    void testGenerateTarget() {
        ArrayList<MsTablesBean> targets = register.generateTarget()

        assertEquals("array size should be 2", targets.size(), 2)
    }


    void testPrepareErdRelease() {
        register.prepareErdRelease()

        TpTicketNumbers4releaseDao tpDao = new TpTicketNumbers4releaseDao()
        TpTicketNumbers4releaseBean bean = new TpTicketNumbers4releaseBean()
        bean.releaseIdAttr().setValue(new BigDecimal(306))
        bean.trkmIdAttr().setValue(new BigDecimal(2843))

        def _list = tpDao.find(this.conn, bean)
        ArrayList<TpTicketNumbers4releaseBean> list = tpDao.convert(_list)

        assertEquals("ticket number should be 33367",
                (new BigDecimal(33367)), list.head().ticketNumberAttr().value())


        bean.trkmIdAttr().setValue(new BigDecimal(2841))
        _list = tpDao.find(this.conn, bean)
        list = tpDao.convert(_list)

        assertEquals("ticket number should be 33574",
                (new BigDecimal(33574)), list.head().ticketNumberAttr().value())
    }

    void testExecuteErdRelease() {

        register.prepareErdRelease()

        register.executeErdRelease()

        RrErdReleasesDao  hstDao  = new RrErdReleasesDao()
        RrErdReleasesBean hstBean = new RrErdReleasesBean()
        hstBean.afReleaseIdAttr().setValue(new BigDecimal(9999999999L))
        hstBean.objectTypeAttr().setValue("TABLE")
        hstBean.objectNameAttr().setValue("a")

        def _result = hstDao.find(this.conn, hstBean)
        ArrayList<RrErdReleasesBean> result = hstDao.convert(_result)

        assertEquals("history release id should be 306",
                (new BigDecimal(306)), result.head().bfReleaseIdAttr().value() )
    }
}
