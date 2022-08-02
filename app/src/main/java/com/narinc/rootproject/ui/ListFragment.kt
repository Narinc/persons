package com.narinc.rootproject.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.narinc.rootproject.databinding.FragmentListBinding
import com.narinc.rootproject.extension.makeGone
import com.narinc.rootproject.extension.makeVisible
import com.narinc.rootproject.extension.observe
import com.narinc.rootproject.presenter.viewmodel.ListViewModel
import com.narinc.rootproject.presenter.viewmodel.PersonUIModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ListFragment : BaseFragment<FragmentListBinding, ListViewModel>() {

    override val viewModel: ListViewModel by viewModels()

    override fun getViewBinding() = FragmentListBinding.inflate(layoutInflater)

    @Inject
    lateinit var personListAdapter: PersonListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.personList, ::onViewStateChange)
        binding.persons.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.HORIZONTAL
            )
        )
        binding.persons.adapter = personListAdapter
        binding.persons.addOnScrollListener(object :
            PaginationScrollListener(binding.persons.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                viewModel.getPersons()
            }

            override val isLastPage: Boolean
                get() = viewModel.isLastPage()
            override val isLoading: Boolean
                get() = viewModel.isLoading()
        })

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.getPersons(true)
        }
    }

    private fun onViewStateChange(event: PersonUIModel) {
        binding.pullToRefresh.isRefreshing = false
        when (event) {
            is PersonUIModel.Error -> handleErrorMessage(event.error)
            is PersonUIModel.Loading -> binding.progressBar.makeVisible()
            is PersonUIModel.Success -> {
                binding.progressBar.makeGone()
                event.data.let {
                    personListAdapter.list = it
                }
            }
        }
    }
}
