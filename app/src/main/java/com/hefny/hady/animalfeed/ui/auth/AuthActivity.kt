package com.hefny.hady.animalfeed.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hefny.hady.animalfeed.KodeinViewModelFactory
import com.hefny.hady.animalfeed.R
import com.hefny.hady.animalfeed.modelsTest.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AuthActivity() : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by kodein()
    private val viewModelFactory: KodeinViewModelFactory by instance()
    private val authViewModel: AuthViewModel by lazy {
        viewModelFactory.create(AuthViewModel::class.java)
    }

    lateinit var usersList: List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        println("DEBUG: ${authViewModel}")

        CoroutineScope(Dispatchers.IO).launch {
            val users = getUsers()
            withContext(Dispatchers.Main) {
                usersList = users
                usersList.forEach {
                    println("$it")
                }
            }
        }

    }

    suspend fun getUsers(): List<User> {
        return authViewModel.getUsers()
    }
}
