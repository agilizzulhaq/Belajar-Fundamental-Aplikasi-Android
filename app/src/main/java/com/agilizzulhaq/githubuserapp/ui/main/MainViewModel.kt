package com.agilizzulhaq.githubuserapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agilizzulhaq.githubuserapp.api.RetrofitClient
import com.agilizzulhaq.githubuserapp.data.model.User
import com.agilizzulhaq.githubuserapp.data.model.UserResponse
import com.agilizzulhaq.githubuserapp.setting.SettingPreferences
import com.agilizzulhaq.githubuserapp.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {

    val listUsers = MutableLiveData<ArrayList<User>?>()

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun setSearchUsers(query: String) {
        RetrofitClient.apiInstance
            ?.getSearchUsers(query)
            ?.enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        val users = response.body()?.items
                        listUsers.postValue(users)
                        if (users.isNullOrEmpty()) {
                            popMessage("User Not found")
                        } else {
                            listUsers.postValue(users)
                            popMessage("User Found")
                        }
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.d("Failure", t.message.toString())
                }
            })
    }

    fun getSearchUsers(): MutableLiveData<ArrayList<User>?> {
        return listUsers
    }

    private fun popMessage(text: String) {
        _snackbarText.postValue(Event(text))
    }
}