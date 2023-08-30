package com.example.githubapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapp.adapter.GithubUserResponseAdapter
import com.example.githubapp.data.Result
import com.example.githubapp.data.remote.response.User
import com.example.githubapp.databinding.FragmentFollowBinding
import com.example.githubapp.ui.viewmodel.FollowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!

    private val followViewModel: FollowViewModel by viewModels()


    companion object {
        const val ARGS_USERNAME = "username"
        const val ARG_POSITION = "position"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString(ARGS_USERNAME) ?: ""
        val position = arguments?.getInt(ARG_POSITION, 0) ?: 0

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            if (position == 1) {
                launch {
                    followViewModel.followers.collect { result ->
                        onFollowersResultReceived(result)
                    }
                }
                launch {
                    followViewModel.isLoading.collect { loading ->
                        if (!loading) followViewModel.getUserFollowers(username)
                    }
                }
            } else {
                launch {
                    followViewModel.following.collect { result ->
                        onFollowingResultReceived(result)
                    }
                }
                launch {
                    followViewModel.isLoading.collect { loading ->
                        if (!loading) followViewModel.getUserFollowing(username)
                    }
                }
            }
        }
    }

    private fun onFollowersResultReceived(result: Result<ArrayList<User>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                showFollowers(result.data)
            }
            is Result.Error -> {
                showLoading(false)
            }
        }
    }

    private fun onFollowingResultReceived(result: Result<ArrayList<User>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                showFollowing(result.data)
            }
            is Result.Error -> {
                showLoading(false)
            }
        }
    }

    private fun showFollowers(users: ArrayList<User>) {
        if (users.size > 0) {
            val linearLayoutManager = LinearLayoutManager(activity)
            val githubUserResponseAdapter = GithubUserResponseAdapter(users)

            binding.rvUsers.apply {
                layoutManager = linearLayoutManager
                adapter = githubUserResponseAdapter
                setHasFixedSize(true)
            }

            githubUserResponseAdapter.setOnItemClickCallback(object :
                GithubUserResponseAdapter.OnItemClickCallback {
                override fun onItemClicked(user: User) {
                    goToDetailUser(user)
                }
            })
        } else {
            binding.tvStatus.visibility = View.VISIBLE
        }
    }

    private fun showFollowing(users: ArrayList<User>) {
        if (users.size > 0) {
            val linearLayoutManager = LinearLayoutManager(activity)
            val githubUserResponseAdapter = GithubUserResponseAdapter(users)

            binding.rvUsers.apply {
                layoutManager = linearLayoutManager
                adapter = githubUserResponseAdapter
                setHasFixedSize(true)
            }

            githubUserResponseAdapter.setOnItemClickCallback(object :
                GithubUserResponseAdapter.OnItemClickCallback {
                override fun onItemClicked(user: User) {
                    goToDetailUser(user)
                }
            })
        } else {
            binding.tvStatus.visibility = View.VISIBLE
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun goToDetailUser(user: User) {
        Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_USERNAME, user.login)
        }.also { startActivity(it) }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}