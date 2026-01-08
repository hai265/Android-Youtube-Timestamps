package com.example.uma.data.repository.pokemon

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.uma.data.models.CharacterBasic
import com.example.uma.data.network.pokemon.NetworkPokemonCharacter
import com.example.uma.data.network.pokemon.PokemonApiService
import javax.inject.Inject

//TODO: https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data
// https://developer.android.com/codelabs/android-paging-basics#5
// expose paging pokemons from pokemonRepository
private const val STARTING_KEY = 0
private const val NUM_TO_FETCH = 24
class PokemonPagingSource @Inject constructor(
    val pokemonApi: PokemonApiService
): PagingSource<Int, CharacterBasic>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterBasic> {
        val start = params.key ?: STARTING_KEY
        try {
            val response = pokemonApi.getAllPokemon(offset = start, limit = NUM_TO_FETCH)
            return LoadResult.Page(
                data = response.results.map { it.toBasicCharacter() },
                prevKey = response.previous.getOffset(),
                nextKey = response.next.getOffset()
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterBasic>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun String?.getOffset(): Int? {
        this ?: return null
        return Uri.parse(this).getQueryParameter("offset")?.toInt()
    }

    private fun NetworkPokemonCharacter.toBasicCharacter(): CharacterBasic {
        val id = this.extractId()
        return CharacterBasic(
            id = id,
            name = name,
            image = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
            gameId = id,
            colorMain = "0",
            colorSub = "0",
            isFavorite = false,
        )
    }

    private fun NetworkPokemonCharacter.extractId(): Int {
        return url.trimEnd('/').substringAfterLast('/').toInt()
    }
}