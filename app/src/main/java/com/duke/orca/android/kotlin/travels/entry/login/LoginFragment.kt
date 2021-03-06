package com.duke.orca.android.kotlin.travels.entry.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.duke.orca.android.kotlin.travels.R
import com.duke.orca.android.kotlin.travels.base.BaseFragment
import com.duke.orca.android.kotlin.travels.databinding.FragmentLoginBinding
import com.duke.orca.android.kotlin.travels.entry.EntryActivity
import com.duke.orca.android.kotlin.travels.entry.EntryViewModel
import com.duke.orca.android.kotlin.travels.main.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment: BaseFragment() {
    private var viewBinding: FragmentLoginBinding? = null
    private val viewModel: EntryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentLoginBinding.inflate(inflater, container, false)

        initializeView()

        return viewBinding?.root
    }

    override fun initializeToolbar() {
        (requireActivity() as EntryActivity).supportActionBar?.apply {
            title = getString(R.string.login)
            setDisplayHomeAsUpEnabled(true)
            setHasOptionsMenu(true)
        }
    }

    private fun initializeView() {
        viewBinding?.materialButtonLogin?.setOnClickListener {
            val userId = viewBinding?.sugarEditTextUserId?.text() ?: run {
                viewBinding?.sugarEditTextUserId?.setError(getString(R.string.error_login_000))
                return@setOnClickListener
            }

            val password = viewBinding?.sugarEditTextPassword?.text() ?: run {
                viewBinding?.sugarEditTextPassword?.setError(getString(R.string.error_login_001))
                return@setOnClickListener
            }

            if (userId.isBlank()) {
                viewBinding?.sugarEditTextUserId?.setError(getString(R.string.error_login_000))
                return@setOnClickListener
            }

            if (password.isBlank()) {
                viewBinding?.sugarEditTextUserId?.setError(getString(R.string.error_login_001))
                return@setOnClickListener
            }

            viewModel.loginWithEmail(userId, password, {
                startMainActivity()
            }, { message ->
                showToast(message)
            })
        }

        viewBinding?.loginButtonFacebook?.setOnClickListener {
            viewModel.loginWithFacebook(this, { token ->
                    getString(R.string.client_id)
                    startMainActivity()
                }, {
                getString(R.string.client_id)
                    showToast(it?.message ?: getString(R.string.error_login_002))
                    Timber.e(it)
                }
            )
        }

        viewBinding?.loginButtonGoogle?.setOnClickListener {
            viewModel.loginWithGoogle(requireActivity()) {
                Timber.e(it)
            }
        }

        viewBinding?.loginButtonKakao?.setOnClickListener {
            viewModel.loginWithKakao(requireContext(), { token ->
                    startMainActivity()
                }, {
                    Timber.e(it)
                }
            )
        }

        viewBinding?.loginButtonNaver?.setOnClickListener {
            viewModel.loginWithNaver(requireActivity(), { accessToken, refreshToken, expiresAt, tokenType ->
                startMainActivity()
            }, { errorCode, errorDesc ->
                Timber.e("errorCode $errorCode")
                Timber.e("errorDesc $errorDesc")
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.callbackManager().onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}