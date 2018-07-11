package com.hx.binder;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String name;
    private int age;

    //必须提供一个名为CREATOR的static final属性 该属性需要实现android.os.Parcelable.Creator<T>接口
    public static final Creator<Student> CREATOR = new Creator<Student>() {
        //通过source对象，根据writeToParcel()方法序列化的数据，反序列化一个Parcelable对象，注意读取变量和写入变量的顺序应该一致,不然得不到正确的结果
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        //创建一个新的Parcelable对象的数组
        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public Student() {}

    public Student(Parcel pl) {
        name = pl.readString();
        age = pl.readInt();
    }

    //返回一个位掩码，表示一组特殊对象类型的Parcelable，一般返回0即可
    @Override
    public int describeContents() {
        return 0;
    }

    //实现对象的序列化，注意写入变量和读取变量的顺序应该一致,不然得不到正确的结果
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}