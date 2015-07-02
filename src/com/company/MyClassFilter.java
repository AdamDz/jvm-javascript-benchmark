package com.company;

import jdk.nashorn.api.scripting.ClassFilter;

public class MyClassFilter implements ClassFilter {
    @Override
    public boolean exposeToScripts(String s) {
        return true;
    }
}
