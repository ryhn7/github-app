package com.example.githubapp.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.githubapp.R
import com.example.githubapp.Utils.Companion.setVisibleOrInvisible
import com.example.githubapp.adapter.SectionsPagerAdapter
import com.example.githubapp.data.Result
import com.example.githubapp.data.local.entity.UserEntity
import com.example.githubapp.data.remote.response.DataUser
import com.example.githubapp.databinding.ActivityDetailBinding
import com.example.githubapp.ui.viewmodel.DetailViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel: DetailViewModel by viewModels()

    private var username: String? = null
    private var blog: String? = null

    private var userDetail: UserEntity? = null
    private var checkFavorite: Boolean? = false


    companion object {
        const val EXTRA_USERNAME = "extra_username"
        private val TAB_TITLE = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        username = intent.extras?.get(EXTRA_USERNAME) as String

        setContentView(binding.root)
        setViewPager()
        setToolbar()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailViewModel.user.collect() { result ->
                        onDetailUserReceived(result)
                    }
                }
                launch {
                    detailViewModel.checkFavorite(username ?: "").collect() { state ->
                        checkFavorite(state)
                        checkFavorite = state
                    }
                }
                launch {
                    detailViewModel.isLoading.collect() { loading ->
                        if (!loading) detailViewModel.getUserDetail(username ?: "")
                    }
                }
            }
        }

        binding.tvProfileBlog.setOnClickListener (this)
        binding.fabFavorite.setOnClickListener (this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_profile_blog -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(blog)
                }.also {
                    startActivity(it)
                }
            }
            R.id.fab_favorite -> {
                if (checkFavorite == true) {
                    userDetail?.let { detailViewModel.deleteFavorite(it) }
                    checkFavorite(false)
                    Toast.makeText(this, "User deleted from favorite", Toast.LENGTH_SHORT).show()
                } else {
                    userDetail?.let { detailViewModel.saveFavorite(it) }
                    checkFavorite(true)
                    Toast.makeText(this, "User added to favorite", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onDetailUserReceived(result: Result<DataUser>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                result.data.let { user ->
                    parseUserDetail(user)

                    val userEntity = UserEntity(
                        user.login,
                        user.avatarUrl,
                        true
                    )

                    userDetail = userEntity
                    blog = user.blog
                }

                showLoading(false)
            }
            is Result.Error -> {
                errorOccurred()
                showLoading(false)
                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkFavorite(favorite: Boolean) {
        if (favorite) {
            binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border_24)
        }
    }

    private fun parseUserDetail(user: DataUser) {
        binding.apply {
            tvProfileUsername.text = user.login
            tvRepo.text = user.publicRepos.toString()
            tvFollowers.text = user.followers.toString()
            tvFollowing.text = user.following.toString()

            tvProfileName.setVisibleOrInvisible(user.name)
            tvProfileType.setVisibleOrInvisible(user.type)
            tvProfileDesc.setVisibleOrInvisible(user.bio)
            tvProfileBlog.setVisibleOrInvisible(user.blog)

            Glide.with(this@DetailActivity)
                .load(user.avatarUrl)
                .into(civDetailAvatarProfile)
        }
    }

    private fun setViewPager() {
        val viewPager: ViewPager2 = binding.viewPager
        val tabs: TabLayout = binding.tabLayout

        viewPager.adapter = SectionsPagerAdapter(this, username!!)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLE[position])
        }.attach()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarDetailProfile)
        binding.collapsingToolbarLayout.isTitleEnabled = false
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = username
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    private fun showLoading(state: Boolean) {
        if (state) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                appBarLayout.visibility = View.GONE
                viewPager.visibility = View.GONE
            }
        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                appBarLayout.visibility = View.VISIBLE
                viewPager.visibility = View.VISIBLE
            }
        }
    }

    private fun errorOccurred() {
        binding.apply {
            clDetailHeader.visibility = View.GONE
            tabLayout.visibility = View.GONE
            viewPager.visibility = View.GONE
        }
        Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        _binding = null
        username = null
        blog = null
        checkFavorite = null

        super.onDestroy()
    }
}