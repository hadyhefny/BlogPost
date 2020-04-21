package com.hefny.hady.blogpost.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.auth.state.AuthStateEvent
import com.hefny.hady.blogpost.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RegisterFragment", "onViewCreated: ${viewModel.hashCode()}")

        btn_register.setOnClickListener {
            register()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            if (authViewState != null) {
                authViewState.registrationFields?.let { registrationFields ->
                    registrationFields.register_email?.let { inputText_email.setText(it) }
                    registrationFields.register_username?.let { inputText_username.setText(it) }
                    registrationFields.register_password?.let { inputText_password.setText(it) }
                    registrationFields.register_confirm_password?.let {
                        inputText_confirm_password.setText(it)
                    }
                }
            }
        })
    }

    fun register() {
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttemptEvent(
                inputText_email.text.toString(),
                inputText_username.text.toString(),
                inputText_password.text.toString(),
                inputText_confirm_password.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                inputText_email.text.toString(),
                inputText_username.text.toString(),
                inputText_password.text.toString(),
                inputText_confirm_password.text.toString()
            )
        )
    }
}
