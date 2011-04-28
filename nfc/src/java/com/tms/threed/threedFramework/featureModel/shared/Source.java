package com.tms.threed.threedFramework.featureModel.shared;

public enum Source {
    Initial,User, Fixup;

    public boolean isFixup(){
        return this.equals(Fixup);
    }

    public boolean isUser(){
        return this.equals(User);
    }

     public boolean isInitial(){
        return this.equals(Initial);
    }
}
