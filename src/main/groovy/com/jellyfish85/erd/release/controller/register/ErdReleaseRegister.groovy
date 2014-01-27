package com.jellyfish85.erd.release.controller.register

import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.MsErdReleasesBean
import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.MsTablesBean
import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.RrErdReleasesBean
import com.jellyfish85.dbaccessor.bean.erd.release.controller.TpTicketNumbers4releaseBean
import com.jellyfish85.dbaccessor.dao.erd.mainte.tool.MsErdReleasesDao
import com.jellyfish85.dbaccessor.dao.erd.mainte.tool.MsTablesDao
import com.jellyfish85.dbaccessor.dao.erd.mainte.tool.RrErdReleasesDao
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
    private MsErdReleasesDao           curDao = null
    private RrErdReleasesDao           hstDao = null


    private BaseContext context = null
    public ErdReleaseRegister(BaseContext _context) {
        this.context = _context

        manager = this.context.manager
        manager.connect()
        conn    = manager.conn()

        tpDao   = new TpTicketNumbers4releaseDao()
        krDao   = new KrTrkmStatusDao()
        tblDao  = new MsTablesDao()
        curDao  = new MsErdReleasesDao()
        hstDao  = new RrErdReleasesDao()
    }

    private BigDecimal curReleaseId = null
    private BigDecimal preReleaseId = null

    public setCurReleaseId(BigDecimal _releaseId) {
        this.curReleaseId = _releaseId
    }

    public setPreReleaseId(BigDecimal _releaseId) {
        this.preReleaseId = _releaseId
    }

    public ArrayList<MsTablesBean> generateTarget() {
        ArrayList<MsTablesBean> beans = new ArrayList<>()

        // find pre trkm id from TP_TICKET_NUMBERS4RELEASE
        BigDecimal preTrkmId = tpDao.findMaxTrkmId(this.conn)

        // find current trkm id from KR_TRKM_STATUS
        BigDecimal curTrkmId = krDao.findCurrent(this.conn).trkmIdAttr().value()

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

    public void prepareErdRelease() {

        BigDecimal preReleaseId = curDao.findMaxReleaseId(this.conn)
        BigDecimal curReleaseId = preReleaseId + new BigDecimal(1)

        setPreReleaseId(preReleaseId)
        setCurReleaseId(curReleaseId)

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

            //todo println release id and trkm id list

            tpBeans.add(tpBean)
        }

        tpDao.insert(this.conn, tpBeans)
        this.conn.commit()
    }

    public void executeErdRelease() {
        hstDao.updateByReleaseIds(this.conn, this.preReleaseId, this.curReleaseId)

        curDao.deleteAll(this.conn)

        ArrayList<MsErdReleasesBean> targets = curDao.findTargetFromTemporary(this.conn, this.curReleaseId)
        targets = curDao.convert(targets)
        curDao.insert(this.conn, targets)

        ArrayList<RrErdReleasesBean> hstTargets = new ArrayList<>()
        targets.each {MsErdReleasesBean bean ->
            RrErdReleasesBean hstBean = new RrErdReleasesBean()

            hstBean.afReleaseIdAttr().setValue(new BigDecimal(9999999999L))
            hstBean.bfReleaseIdAttr().setValue(bean.releaseIdAttr().value())
            hstBean.diffTypeAttr().setValue(bean.diffTypeAttr().value())
            hstBean.objectIdAttr().setValue(bean.objectIdAttr().value())
            hstBean.objectNameAttr().setValue(bean.objectNameAttr().value())
            hstBean.objectTypeAttr().setValue(bean.objectTypeAttr().value())
            hstBean.revisionAttr().setValue(bean.revisionAttr().value())

            hstTargets.add(hstBean)
        }
        hstDao.insert(this.conn, hstTargets)

        curDao.insertFromHst(this.conn, curReleaseId)
        hstDao.insertFromCur(this.conn, curReleaseId)
    }

    public void register() {
        // specify targets
        prepareErdRelease()

        // release targets
        executeErdRelease()
    }
}