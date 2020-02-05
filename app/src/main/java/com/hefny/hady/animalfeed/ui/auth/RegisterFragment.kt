package com.hefny.hady.animalfeed.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hefny.hady.animalfeed.R
import com.hefny.hady.animalfeed.ui.auth.state.RegistrationFields
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

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            authViewState.registrationFields?.let { registrationFields ->
                registrationFields.register_email?.let { editText_email.setText(it) }
                registrationFields.register_username?.let { editText_username.setText(it) }
                registrationFields.register_password?.let { editText_password.setText(it) }
                registrationFields.register_confirm_password?.let {
                    editText_confirm_password.setText(it)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                editText_email.text.toString(),
                editText_username.text.toString(),
                editText_password.text.toString(),
                editText_confirm_password.text.toString()
            )
        )
    }
}
