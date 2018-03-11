package com.idba.icore.multiprocess;


/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/

public class PriorityLogicWrapper implements Comparable<PriorityLogicWrapper> {

    public int priority = 0;
    public Class<? extends RouterApplicationLogic> logicClass = null;
    public RouterApplicationLogic instance;

    public PriorityLogicWrapper(int priority, Class<? extends RouterApplicationLogic> logicClass) {
        this.priority = priority;
        this.logicClass = logicClass;
    }

    @Override
    public int compareTo(PriorityLogicWrapper o) {
        return o.priority - this.priority;
    }
}
