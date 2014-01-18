package com.jellyfish85.erd.release.controller.register

import com.jellyfish85.dbaccessor.bean.erd.release.controller.KrTrkmStatusBean
import com.jellyfish85.dbaccessor.dao.erd.release.controller.KrTrkmStatusDao
import com.jellyfish85.erd.release.controller.BaseContext
import com.jellyfish85.erd.release.controller.constant.ErdReleaseControllerConst

import java.sql.SQLException

class TrkmStatusRegister extends ErdRegister {

    private KrTrkmStatusDao dao = new KrTrkmStatusDao()
    private BaseContext context = null
    public TrkmStatusRegister(BaseContext context) {
        this.context = context
    }

    public void register() throws SQLException {
        KrTrkmStatusBean currentBean = dao.findCurrent(this.context.conn)

        if (currentBean.trkmStatusAttr().value().
                equals(ErdReleaseControllerConst.TRKM_STATUS_SHORIZUMI)) {

            BigDecimal nextTrkmId = currentBean.trkmIdAttr().value() + new BigDecimal(1)
            println("*****************************************" )
            println("***" )
            println("***" )
            println("***  [UPDATE][TRKM_ID]" )
            println("***    -- FROM  <<  " +  currentBean.trkmIdAttr().value() )
            println("***    -- TO    >>  " +  nextTrkmId)
            println("*****************************************" )

            KrTrkmStatusBean preBean = currentBean
            preBean.currentFlgAttr().setValue("0")
            dao.update(this.context.conn, [preBean])
            this.context.manager.jCommit()

            currentBean.trkmIdAttr().setValue(nextTrkmId)
            currentBean.currentFlgAttr().setValue("1")
            dao.insert(this.context.conn, [currentBean])
            this.context.manager.jCommit()

        } else {
            println("*****************************************" )
            println("***" )
            println("***" )
            println("***  [NO CHANGE][TRKM_ID]" )
            println("***    -- STILL --   " + currentBean.trkmIdAttr().value() )
            println("***")
            println("*****************************************" )

        }
    }
}
