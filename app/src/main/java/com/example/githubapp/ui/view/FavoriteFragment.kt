package com.example.githubapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapp.adapter.GithubUserResponseAdapter
import com.example.githubapp.data.local.entity.UserEntity
import com.example.githubapp.data.remote.response.User
import com.example.githubapp.databinding.FragmentFavoriteBinding
import com.example.githubapp.ui.viewmodel.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.favorite.collect() {
                    if (it.isNotEmpty()) {
                        showFavoriteList(it)
                    } else {
                        showMessage()
                    }
                }
            }
        }

        return root
    }

    private fun showMessage() {
        binding.tvMessage404.visibility = View.VISIBLE
        binding.rvFavorite.visibility = View.GONE
    }

    private fun showFavoriteList(users: List<UserEntity>) {
        val listUsers = ArrayList<User>()

        users.forEach { user ->
            val data = User(
                user.id,
                user.avatarUrl

            )

            listUsers.add(data)
        }

        val githubUserResponseAdapter = GithubUserResponseAdapter(listUsers)
        binding.rvFavorite.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = githubUserResponseAdapter
            visibility = View.VISIBLE
            setHasFixedSize(true)
        }
        binding.tvMessage404.visibility = View.GONE

        githubUserResponseAdapter.setOnItemClickCallback(object :
            GithubUserResponseAdapter.OnItemClickCallback {
            override fun onItemClicked(user: User) {
                goToDetailUser(user)
            }
        })
    }

    private fun goToDetailUser(user: User) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USERNAME, user.login)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}