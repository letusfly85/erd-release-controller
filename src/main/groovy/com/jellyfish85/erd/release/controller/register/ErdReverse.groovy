package com.jellyfish85.erd.release.controller.register

class ErdReverse extends GeneralRegister {

    // ticket number for MS_TABLES
    private BigDecimal        ticketNumber     = null

    // trkm id for MS_TABLES
    private BigDecimal        trkmId           = null

    // table define names
    private ArrayList<String> tableDefineNames = null

    public void setTicketNumber(BigDecimal _ticketNumber){
        this.ticketNumber = _ticketNumber
    }

    public void setTrkmId(BigDecimal _trkmId){
        this.trkmId = _trkmId
    }

    public void setTableDefineNames(ArrayList<String> _tableDefineNames){
        this.tableDefineNames = _tableDefineNames
    }

    public void register(){
        //todo get KR_TAB_DEF_REVISIONS entries


        //todo get RR_TAB_DEF_INFO entries


        //todo validate table defines


        //todo check consistency


        //todo register MS_COLUMN_DICTIONARY


        //todo register MS_TABLES, MS_TAB_COLUMNS, RR_TABLES, RR_TAB_COLUMNS


        //todo register TR_PJ_ERD_TABLES, TR_PJ_ERD_COLUMNS

    }
}
