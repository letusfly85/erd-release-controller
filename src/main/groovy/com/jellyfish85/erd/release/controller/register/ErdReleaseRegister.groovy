package com.jellyfish85.erd.release.controller.register

import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.MsTablesBean
import com.jellyfish85.dbaccessor.bean.erd.release.controller.TpTicketNumbers4releaseBean
import com.jellyfish85.dbaccessor.dao.erd.mainte.tool.MsErdReleasesDao
import com.jellyfish85.dbaccessor.dao.erd.mainte.tool.MsTablesDao
import com.jellyfish85.dbaccessor.dao.erd.release.controller.KrTrkmStatusDao
import com.jellyfish85.dbaccessor.dao.erd.release.controller.TpTicketNumbers4releaseDao
import com.jellyfish85.dbaccessor.manager.DatabaseManager
import com.jellyfish85.erd.release.controller.BaseContext
import org.apache.commons.lang.ArrayUtils

import java.sql.Connection
import java.sql.SQLException


class ErdReleaseRegister extends ErdRegister {

    private DatabaseManager          manager  = null
    private Connection               conn     = null
    private TpTicketNumbers4releaseDao tpDao  = null
    private KrTrkmStatusDao            krDao  = null
    private MsTablesDao                tblDao = null
    private MsErdReleasesDao           erdDao = null


    private BaseContext context = null
    public ErdReleaseRegister(BaseContext _context) {
        this.context = _context

        manager = this.context.manager
        manager.connect()
        conn    = manager.conn()

        tpDao   = new TpTicketNumbers4releaseDao()
        krDao   = new KrTrkmStatusDao()
        tblDao  = new MsTablesDao()
        erdDao  = new MsErdReleasesDao()
    }

    private BigDecimal curReleaseId = null
    private BigDecimal preReleaseId = null

    public setCurReleaseId(BigDecimal _releaseId) {
        this.curReleaseId = _releaseId
    }

    public setPreReleaseId(BigDecimal _releaseId) {
        this.preReleaseId = _releaseId
    }

    public ArrayList<TpTicketNumbers4releaseBean> generateTarget() {
        ArrayList<TpTicketNumbers4releaseBean> beans = new ArrayList<>()

        // find pre trkm id from TP_TICKETNUMBERS4RELEASE
        BigDecimal preTrkmId = tpDao.findMaxTrkmId(this.conn)

        // find current trkm id from KR_TRKM_STATUS
        BigDecimal curTrkmId = krDao.findCurrent(this.conn)

        // if there is no changes between both ids, return empty list with a message
        if (preTrkmId >= curTrkmId) {
            println("***********************************")
            println("*")
            println("* trkm status is not changed")
            println("* there is no release target")
            println("*")
            println("* end")
            println("***********************************")

            return beans
        }

        beans = tblDao.convert(
                    tblDao.findByTrkmIdRange(this.conn, preTrkmId, curTrkmId)
                )

        return beans
    }

    public void prepareErdRelease() throws SQLException {

        BigDecimal preReleaseId = erdDao.findMaxReleaseId(this.conn)
        BigDecimal curReleaseId = preReleaseId + new BigDecimal(1)

        ArrayList<MsTablesBean> beans = generateTarget()
        if (ArrayUtils.isEmpty(beans)) {
            return
        }

        ArrayList<TpTicketNumbers4releaseBean> tpBeans = new ArrayList<>()
        beans.each {MsTablesBean bean ->
            TpTicketNumbers4releaseBean tpBean = new TpTicketNumbers4releaseBean()

            tpBean.releaseIdAttr().setValue(curReleaseId)
            tpBean.ticketNumberAttr().setValue(bean.ticketNumberAttr().value())
            tpBean.trkmIdAttr().setValue(bean.trkmIdAttr().value())

            tpBeans.add(tpBean)
        }

        tpDao.insert(this.conn, tpBeans)
        this.manager.jCommit()
    }

    public void register() {
        //todo refresh ms and rr erd release table
    }

}
