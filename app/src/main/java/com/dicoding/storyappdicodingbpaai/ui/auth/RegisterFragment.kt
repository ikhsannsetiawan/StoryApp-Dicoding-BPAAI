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
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.databinding.FragmentRegisterBinding
import com.dicoding.storyappdicodingbpaai.viewModel.AuthenticationViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.ViewModelFactory

class RegisterFragment : Fragment() {

    private var viewModel: AuthenticationViewModel? = null
    private lateinit var binding: FragmentRegisterBinding

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory((activity as AuthenticationActivity))
        )[AuthenticationViewModel::class.java]
        viewModel?.let { vm ->
            vm.registerResult.observe(viewLifecycleOwner) { register ->
                if (!register.error) {

                    Toast.makeText(getActivity(), getString(R.string.UI_info_successful_register_user), Toast.LENGTH_SHORT).show()
                    switchLogin()
                }
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

        binding.btnLogin.setOnClickListener {
            switchLogin()
        }

        binding.btnRegister.setOnClickListener {
            val nama = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when {
                email.isEmpty() or password.isEmpty() or nama.isEmpty() -> {

                    Toast.makeText(getActivity(), getString(R.string.UI_validation_empty_email_password), Toast.LENGTH_SHORT).show()
                }
                !email.matches(Constanta.emailPattern) -> {
                    Toast.makeText(getActivity(), getString(R.string.UI_validation_invalid_email), Toast.LENGTH_SHORT).show()
                }
                password.length < 8 -> {
                    Toast.makeText(getActivity(), getString(R.string.UI_validation_password_rules), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel?.register(nama, email, password)
                }
            }
        }
    }

    private fun switchLogin() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
            /* shared element transition to main activity */
            addSharedElement(binding.labelAuth, "auth")
            addSharedElement(binding.edtEmail, "email")
            addSharedElement(binding.edtPassword, "password")
            addSharedElement(binding.containerMisc, "misc")
            commit()
        }
    }
}