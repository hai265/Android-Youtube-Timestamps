package com.example.uma.ui.screens.pokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.uma.data.models.CharacterBasic
import com.example.uma.ui.screens.common.GradientBackground
import com.example.uma.ui.screens.common.ImageWithFavoriteButton
import kotlinx.coroutines.flow.flowOf

@Composable
fun PokemonListScreen(modifier: Modifier = Modifier, onTapCharacter: (Int) -> Unit) {
    val viewModel: PokemonListViewModel = hiltViewModel()
    val gridState = rememberLazyGridState()
    val pagingItems = viewModel.items.collectAsLazyPagingItems()

    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load")
                Button(onClick = { pagingItems.retry() }) {
                    Text("Retry")
                }
            }
        }
        else -> {
            CharacterColumn(
                characterPagingItems = pagingItems,
                onTapCharacter = onTapCharacter,
                state = gridState,
                onTapFavorite = { id: Int ->

                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun CharacterColumn(
    characterPagingItems: LazyPagingItems<CharacterBasic>,
    onTapCharacter: (Int) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyGridState,
    onTapFavorite: (Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            characterPagingItems.itemCount,
            key = characterPagingItems.itemKey { it.id }) { index ->
            val character = characterPagingItems[index] ?: return@items
            Card {
                GradientBackground(
                    primaryColorHex = character.colorMain,
                ) {
                    ImageWithFavoriteButton(
                        onClickImage = { onTapCharacter(character.id) },
                        bottomText = character.name,
                        imageUrl = character.image,
                        isFavorite = character.isFavorite,
                        onTapFavorite = { onTapFavorite(character.id) }
                    )
                }
            }
        }
        if (characterPagingItems.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (characterPagingItems.loadState.append is LoadState.Error) {
            item(span = { GridItemSpan(3) }) {
                Button(onClick = { characterPagingItems.retry() }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Preview
@Composable
fun CharacterColumnPreview() {
    val characterBasicLists =
        listOf(
            CharacterBasic(1, 1, "Special Week", "", "", "", false),
            CharacterBasic(2, 2, "Tokai Teio", "", "", "", false),
            CharacterBasic(3, 2, "Silence Suzuka", "", "", "", false),
        )
    val pagingItems = flowOf(
        PagingData.from(characterBasicLists)
    ).collectAsLazyPagingItems()

    CharacterColumn(
        characterPagingItems = pagingItems,
        onTapCharacter = {},
        state = rememberLazyGridState(),
        onTapFavorite = { _ -> })
}