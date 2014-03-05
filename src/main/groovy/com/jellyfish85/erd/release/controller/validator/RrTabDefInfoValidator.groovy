package com.jellyfish85.erd.release.controller.validator

import com.jellyfish85.dbaccessor.bean.erd.mainte.tool.RrTabDefInfoBean
import com.jellyfish85.erd.release.controller.bean.RrTabDefInfoValidateBean

class RrTabDefInfoValidator extends GeneralValidator {

    private ArrayList<RrTabDefInfoValidateBean> validateBeans = null

    public RrTabDefInfoValidator() {
        super()

        validateBeans = new ArrayList<>()
    }

    /**
     * validate RR_TAB_DEF_INFO duplication
     *
     *
     * @param beans
     */
    public void validateDuplicateRecords(ArrayList<RrTabDefInfoBean> beans) {
        HashMap<String, String> duplicateMap = new HashMap<>()

        beans.eachWithIndex {RrTabDefInfoBean bean, int index ->

            beans.eachWithIndex {RrTabDefInfoBean _bean, int _index ->
                if (!index.equals(_index) &&
                        bean.physicalColumnNameAttr().value_$eq(_bean.physicalColumnNameAttr().value())) {
                    duplicateMap.put(bean.physicalColumnNameAttr().value(), "")
                }
            }
        }

        if (duplicateMap.size() > 0) {
            duplicateMap.each {key, value ->
                RrTabDefInfoValidateBean validateBean = new RrTabDefInfoValidator()
                validateBean.setResults(false, key)

                validateBeans.add(validateBean)
            }
        }
    }

    /**
     * validate object type check
     *
     *
     * @param beans
     */
    public void validateYmdColumnDataAttribute(ArrayList<RrTabDefInfoBean> beans) {
        HashMap<String, String> ymdMap = new HashMap<>()

        beans.eachWithIndex {RrTabDefInfoBean bean, int index ->

            beans.eachWithIndex {RrTabDefInfoBean _bean, int _index ->
                if (_bean.physicalColumnNameAttr().value().endsWith("YMD") &&
                    _bean.dataLengthAttr().value() != "8"
                    ) {
                    ymdMap.put(bean.physicalColumnNameAttr().value(), "")
                }
            }
        }

        if (ymdMap.size() > 0) {
            ymdMap.each {key, value ->
                RrTabDefInfoValidateBean validateBean = new RrTabDefInfoValidator()
                validateBean.setResults(false, key)

                validateBeans.add(validateBean)
            }
        }
    }
}
