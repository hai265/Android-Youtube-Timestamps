package com.example.uma.ui.noncompose

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uma.R
import com.example.uma.ui.screens.character.CharacterListState
import com.example.uma.ui.screens.character.CharacterListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint // Hilt requires this annotation
class NonComposeActivity : AppCompatActivity() {

    // Use Hilt to get the ViewModel instance
    private val viewModel: CharacterListViewModel by viewModels()

    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_non_compose)

        setupRecyclerView()

        progressBar = findViewById(R.id.progress_bar)

        // Observe the state from the ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleState(state)
                }
            }
        }
        viewModel.start()
    }

    private fun setupRecyclerView() {
        characterAdapter = CharacterAdapter()
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = characterAdapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Add a divider for better visual separation
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        )
    }

    private fun handleState(state: CharacterListState) {
        Log.d("NonComposeActivity", "Handling state: syncing=${state.syncing}, list size=${state.list.size}")

        progressBar.visibility = if (state.syncing) View.VISIBLE else View.GONE
        characterAdapter.submitList(state.list)

        // Add a check for an empty list after loading
        if (!state.syncing && state.list.isEmpty()) {
            Log.w("NonComposeActivity", "Data loaded, but the list is empty.")
            // Optionally, show an "empty state" TextView here
        }
    }
}
