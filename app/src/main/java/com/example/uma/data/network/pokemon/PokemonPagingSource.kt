package com.example.uma.data.network.pokemon

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject

//TODO: https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data
// https://developer.android.com/codelabs/android-paging-basics#5
// expose paging pokemons from pokemonRepository
private const val STARTING_KEY = 0
class PokemonPagingSource @Inject constructor(
    val pokemonApi: PokemonApiService
): PagingSource<Int, NetworkPokemonCharacter>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkPokemonCharacter> {
        val start = params.key ?: STARTING_KEY

        try {
            val response = pokemonApi.getAllPokemon(start)
            return LoadResult.Page(
                data = response.results,
                prevKey = null,
                nextKey = response.next.getNextKey()
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NetworkPokemonCharacter>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun String?.getNextKey(): Int {
        return Uri.parse(this).getQueryParameter("offset")?.toInt() ?: 0
    }
}