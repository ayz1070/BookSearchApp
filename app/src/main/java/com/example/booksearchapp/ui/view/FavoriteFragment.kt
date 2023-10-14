package com.example.booksearchapp.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksearchapp.R
import com.example.booksearchapp.databinding.FragmentFavoriteBinding
import com.example.booksearchapp.ui.adapter.BookSearchAdapter
import com.example.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.example.booksearchapp.util.collectLatestStateFlow
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding: FragmentFavoriteBinding get() = _binding!!

    private lateinit var bookSearchViewModel: BookSearchViewModel
    private lateinit var bookSearchAdapter: BookSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        setupRecyclerView()
        setupTouchHelper(view)
//        bookSearchViewModel.favoriteBooks.observe(viewLifecycleOwner) {
//            bookSearchAdapter.submitList(it)
//        }

//        lifecycleScope.launch{
//            bookSearchViewModel.favoriteBooks.collectLatest {
//                bookSearchAdapter.submitList(it)
//            }
//        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                bookSearchViewModel.favoriteBooks.collectLatest {
//                    bookSearchAdapter.submitList(it)
//                }
//            }
//        }

        collectLatestStateFlow(bookSearchViewModel.favoriteBooks) {
            bookSearchAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun setupRecyclerView() {
        bookSearchAdapter = BookSearchAdapter()
        binding.rvFavoriteBooks.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = bookSearchAdapter
        }

        // 리사이클러뷰 아이템 클릭 시 이동
        bookSearchAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionFragmentSearchToFragmentBook(it)
            findNavController().navigate(action)
        }
    }


    private fun setupTouchHelper(view: View) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT  // 왼쪽으로
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            // 스와이핑 동작 시 책 삭제
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                // 위치를 가져와서

                val book = bookSearchAdapter.currentList[position]
                // 어답터로 전달하여 책 데이터를 가져온다.

                bookSearchViewModel.deleteBook(book)
                // 삭제
                Snackbar.make(view, "Book has deleted", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        bookSearchViewModel.saveBook(book)
                    }

                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteBooks)
        }
    }

}