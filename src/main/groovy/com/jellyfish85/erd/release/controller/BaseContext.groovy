package com.jellyfish85.erd.release.controller

import com.jellyfish85.dbaccessor.manager.DatabaseManager
import com.jellyfish85.erd.release.controller.utils.ErdReleaseProp

import java.sql.Connection

/**
 *
 *
 */
class BaseContext {

    public ErdReleaseProp erdProp = null

    public  DatabaseManager manager = null
    private Connection conn         = null
    private String envName          = ""
    public BaseContext(String _envName) {
        this.envName = _envName
        erdProp      = new ErdReleaseProp(_envName)

        manager      = new DatabaseManager()
        manager.connect()
        this.conn    = manager.conn()
    }
}
