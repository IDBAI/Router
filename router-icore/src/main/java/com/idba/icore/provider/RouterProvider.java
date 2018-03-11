package com.idba.icore.provider;

import com.idba.icore.action.RouterAction;

import java.util.HashMap;

/**
 *  you must call registerActions to register ACTION，eg：<p></p>
 *  <code>
 *  registerAction(XxxHelper.XXXX_ACTION_XXXX, new XxxAction());
 *  </code>
 */
public abstract class RouterProvider {
    //TODO this field is used for control the provider on and off
    private boolean mValid = true;
    private HashMap<String,RouterAction> mActions;
    public RouterProvider(){
        mActions = new HashMap<>();
        registerActions();
    }
    protected void registerAction(String actionName,RouterAction action){
        mActions.put(actionName,action);
    }

    public RouterAction findAction(String actionName){
        return mActions.get(actionName);
    }

    public boolean isValid(){
        return mValid;
    }

    protected abstract void registerActions();
}
