package com.example.finalproject

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChoiceViewModel: ViewModel() {
    val choice:MutableLiveData<String> = MutableLiveData()
}