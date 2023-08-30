package com.example.githubapp.ui.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapp.R
import com.example.githubapp.adapter.GithubUserResponseAdapter
import com.example.githubapp.data.Result
import com.example.githubapp.data.remote.response.User
import com.example.githubapp.databinding.FragmentHomeBinding
import com.example.githubapp.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.users.collect { result ->
                    showSearchResult(result)
                }
            }
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)

        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                homeViewModel.findUser(query ?: "")
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun errorOccurred() {
        Toast.makeText(context, "ERROR GAN", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvUsers.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvUsers.visibility = View.VISIBLE
        }
    }

    private fun showSearchResult(result: Result<ArrayList<User>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Error -> {
                errorOccurred()
                showLoading(false)
            }
            is Result.Success -> {
                binding.tvResultCount.text = getString(R.string.results, result.data.size)

                val githubUserResponseAdapter = GithubUserResponseAdapter(result.data)
                binding.rvUsers.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = githubUserResponseAdapter
                    setHasFixedSize(true)
                }
                githubUserResponseAdapter.setOnItemClickCallback(object :
                    GithubUserResponseAdapter.OnItemClickCallback {
                    override fun onItemClicked(user: User) {
                        goToDetailUser(user)
                    }
                })
                showLoading(false)
            }
        }
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
