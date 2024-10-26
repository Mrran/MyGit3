package com.matou.smartcar.bean;

import com.matou.smartcar.global.GlobalVariables;

/**
 * 处理后的胎压信息
 */
public class HandleTirePressInfo {

    private boolean hasValue;

    private double value;

    public HandleTirePressInfo(boolean hasValue, double value) {
        this.hasValue = hasValue;
        this.value = value;
    }

    /**
     * 胎压是否异常
     * @return
     */
    public boolean isAbnormal(){
        if(!hasValue){
            return false;
        }

        if(GlobalVariables.lowTyVal > 0 && value < GlobalVariables.lowTyVal){
            return true;
        }else if(GlobalVariables.highTyVal > 0 && value > GlobalVariables.highTyVal){
            return true;
        }

        return false;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
