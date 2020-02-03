package com.hefny.hady.animalfeed.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hefny.hady.animalfeed.R
import com.hefny.hady.animalfeed.util.ApiEmptyResponse
import com.hefny.hady.animalfeed.util.ApiErrorResponse
import com.hefny.hady.animalfeed.util.ApiSuccessResponse

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

        viewModel.testRegister("hady123@gmail.com", "hady123", "123456789", "123456789")
            .observe(viewLifecycleOwner,
                Observer { response ->
                    when (response) {
                        is ApiSuccessResponse -> Log.d(
                            "AppDebug",
                            "RegisterFragment: ${response.body}"
                        )
                        is ApiEmptyResponse -> Log.d("AppDebug", "RegisterFragment: Empty Response")
                        is ApiErrorResponse -> Log.d(
                            "AppDebug",
                            "RegisterFragment: Error: ${response.errorMessage}"
                        )
                    }
                })
    }
}
