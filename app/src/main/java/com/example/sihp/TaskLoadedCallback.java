package com.example.sihp;

import com.example.sihp.Models.ComPojo;

import java.util.ArrayList;

public interface TaskLoadedCallback {
    void onTaskDone(Object... values);

    void onCompareDone(ArrayList<ComPojo> list);
}
