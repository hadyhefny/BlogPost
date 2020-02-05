package com.hefny.hady.animalfeed.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hefny.hady.animalfeed.R
import com.hefny.hady.animalfeed.models.AuthToken
import com.hefny.hady.animalfeed.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseAuthFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LoginFragment", "onViewCreated: ${viewModel.hashCode()}")
        subscribeObservers()
        login_button.setOnClickListener {
            viewModel.setAuthToken(
                AuthToken(
                    1,
                    "gdfngidfng4nt43n43jn34jn"
                )
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            authViewState.loginFields?.let { loginFields ->
                loginFields.login_email?.let { input_email.setText(it) }
                loginFields.login_password?.let { input_password.setText(it) }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(input_email.text.toString(), input_password.text.toString())
        )
    }
}