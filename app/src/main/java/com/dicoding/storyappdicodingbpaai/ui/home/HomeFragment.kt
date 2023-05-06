package com.dicoding.storyappdicodingbpaai.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.databinding.FragmentHomeBinding
import com.dicoding.storyappdicodingbpaai.ui.MainActivity
import com.dicoding.storyappdicodingbpaai.viewModel.SettingViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.SettingViewModelFactory
import com.dicoding.storyappdicodingbpaai.viewModel.StoryViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.ViewModelFactory
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var viewModel: StoryViewModel? = null
    private lateinit var binding: FragmentHomeBinding
    private val rvAdapter = HomeAdapter()
    private var tempToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory((activity as MainActivity))
        )[StoryViewModel::class.java]

        val pref = SettingPreferences.getInstance((activity as MainActivity).dataStore)

        val settingViewModel =
            ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name)
            .observe(viewLifecycleOwner) { token ->

                if (token == Constanta.preferenceDefaultValue) {
                    (activity as MainActivity).routeToAuth()
                }

                tempToken = StringBuilder("Bearer ").append(token).toString()
                viewModel?.loadStoryData(tempToken)
            }

        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }

        binding.rvStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = rvAdapter
        }

        viewModel?.apply {
            loading.observe(viewLifecycleOwner) { binding.loading.visibility = it }
            error.observe(
                viewLifecycleOwner
            ) { if (it.isNotEmpty()) Toast.makeText(getActivity(), it, Toast.LENGTH_SHORT).show() }

            storyList.observe(viewLifecycleOwner) {
                rvAdapter.apply {
                    initData(it)
                    notifyDataSetChanged()
                }
            }
        }


        return binding.root
    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true

        viewModel?.loadStoryData(tempToken)
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
        }
        binding.nestedScrollView.smoothScrollTo(0, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.swipeRefresh -> {
                onRefresh()
            }
        }
        return true
    }

}