package com.ollethunberg.nationsplus.lib;

public class PermissionException extends Exception {
    public PermissionException(String errorMessage) {
        super(errorMessage);
    }
}