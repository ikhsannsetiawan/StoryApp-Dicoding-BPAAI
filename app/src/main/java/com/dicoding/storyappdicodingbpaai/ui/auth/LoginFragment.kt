package com.dicoding.storyappdicodingbpaai.ui.auth

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.databinding.FragmentLoginBinding
import com.dicoding.storyappdicodingbpaai.viewModel.AuthenticationViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.SettingViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.SettingViewModelFactory
import com.dicoding.storyappdicodingbpaai.viewModel.ViewModelFactory

class LoginFragment : Fragment() {

    private var viewModel: AuthenticationViewModel? = null
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val setPreferences = SettingPreferences.getInstance((activity as AuthenticationActivity).dataStore)

        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(setPreferences))[SettingViewModel::class.java]

        viewModel = ViewModelProvider(this, ViewModelFactory((activity as AuthenticationActivity)))[AuthenticationViewModel::class.java]

        viewModel?.let { vm ->
            vm.loginResult.observe(viewLifecycleOwner) { login ->
                settingViewModel.setUserPreferences(
                    login.loginResult.token,
                    login.loginResult.userId,
                    login.loginResult.name,
                    viewModel!!.emailTemp.value ?: Constanta.preferenceDefaultDateValue
                )
            }
            vm.error.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show()
                }
            }
            vm.loading.observe(viewLifecycleOwner) { state ->
                binding.loading.visibility = state
            }
        }

        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name)
            .observe(viewLifecycleOwner) { token ->
                // token changes -> redirect to Main Activity
                if (token != Constanta.preferenceDefaultValue) (activity as AuthenticationActivity).routeToMainActivity()
            }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when {
                email.isEmpty() or password.isEmpty() -> {
                    Toast.makeText(getActivity(), getString(R.string.UI_validation_empty_email_password), Toast.LENGTH_SHORT).show()
                }

                !email.matches(Constanta.emailPattern) -> {
                    Toast.makeText(getActivity(), getString(R.string.UI_validation_invalid_email), Toast.LENGTH_SHORT).show()
                }

                password.length < 8 -> {
                    Toast.makeText(getActivity(), getString(R.string.UI_validation_password_rules), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    viewModel?.login(email, password)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                /* shared element transition to main activity */
                addSharedElement(binding.labelAuth, "auth")
                addSharedElement(binding.edtEmail, "email")
                addSharedElement(binding.edtPassword, "password")
                addSharedElement(binding.containerMisc, "misc")
                commit()
            }
        }

    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}