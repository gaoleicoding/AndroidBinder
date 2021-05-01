package com.hx.binder;

import com.hx.binder.Student;

interface IMyAidlInterface {
    int plus(int a, int b);
    String toUpperCase(String str);
    Student doubleAge(in Student student);
}
